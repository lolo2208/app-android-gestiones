package com.upc.appgestiones.core.services

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.EstadoOperacion
import com.upc.appgestiones.core.data.model.Operacion
import com.upc.appgestiones.core.data.repository.DireccionRepository
import com.upc.appgestiones.core.utils.DateUtil
import com.upc.appgestiones.ui.map.MapViewModel
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class MapService(private val context: Context, private val mapView: MapView) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null
    private var myLocationMarker: Marker? = null

    // Crear marcadores a partir de una lista de operaciones
    fun addOperacionMarkers(
        operaciones: List<Operacion>,
        conNumeracion: Boolean = true,
        onMarkerClick: ((Operacion) -> Unit)? = null,
        onGeoClick: ((Operacion) -> Unit)? = null
    ) {
        mapView.overlays.removeAll { it is Marker && it != myLocationMarker }

        operaciones.forEachIndexed { index, operacion ->
            val direccion = operacion.direccionNavigation

            val diasRestantes = DateUtil.diferenciaDeFechaActual(operacion.fechaVencimiento)


            val colorRes = when {
                diasRestantes < 0 -> R.color.custom_red
                diasRestantes in 0..3 -> R.color.custom_orange
                else -> R.color.custom_green
            }

            val numero = if (conNumeracion) index + 1 else null
            val icono = createNumberedIcon(
                numero,
                colorRes,
                if (operacion.estado == EstadoOperacion.EN_RUTA) R.color.nav_item_color else R.color.white
            )

            val marker = Marker(mapView).apply {
                position = GeoPoint(direccion.latitud ?: 0.0, direccion.longitud ?: 0.0)
                title = operacion.asunto
                subDescription = "${operacion.clienteNavigation.nombres} ${operacion.clienteNavigation.apellidos}"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = icono
            }

            marker.setOnMarkerClickListener { _, _ ->
                onMarkerClick?.invoke(operacion)
                true
            }

            mapView.overlays.add(marker)
        }

        mapView.invalidate()
    }


    // Obtener ubicación actual una sola vez
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onSuccess: (GeoPoint) -> Unit, onFailure: (Exception) -> Unit) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val geoPoint = GeoPoint(it.latitude, it.longitude)
                    showMyLocationMarker(geoPoint)
                    onSuccess(geoPoint)
                } ?: onFailure(Exception("No se pudo obtener la ubicación"))
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Seguir ubicación en tiempo real
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(onLocationUpdate: (GeoPoint) -> Unit) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000
        ).setMinUpdateDistanceMeters(10f).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { location ->
                    val geoPoint = GeoPoint(location.latitude, location.longitude)
                    showMyLocationMarker(geoPoint)
                    onLocationUpdate(geoPoint)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            null
        )
    }

    // Mostrar o actualizar marcador de ubicación actual
    private fun showMyLocationMarker(point: GeoPoint) {
        if (myLocationMarker == null) {
            myLocationMarker = Marker(mapView).apply {
                position = point
                icon = createCircleMarker()
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            }
            mapView.overlays.add(myLocationMarker)
        } else {
            myLocationMarker?.position = point
        }

        mapView.controller.setCenter(point)

        myLocationMarker?.setOnMarkerClickListener() { marker, mapview ->
            mapView.controller.setCenter(point)
            false
        }
        mapView.invalidate()
    }


    @SuppressLint("MissingPermission")
    fun updateOperacionWithCurrentLocation(
        operacion: Operacion,
        context: Context,
        onSuccess: (Operacion) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {

                    val nuevaDireccion = operacion.direccionNavigation.copy(
                        latitud = location.latitude,
                        longitud = location.longitude
                    )

                    val direccionRepository = DireccionRepository(context)
                    direccionRepository.updateDireccion(
                        direccion = nuevaDireccion,
                        onSuccess = { direccionActualizada ->
                            val operacionActualizada = operacion.copy(direccionNavigation = direccionActualizada)
                            onSuccess(operacionActualizada)
                        },
                        onError = { e ->
                            onFailure(e)
                        }
                    )
                } else {
                    onFailure(Exception("No se pudo obtener la ubicación actual"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    @SuppressLint("MissingPermission")
    fun updateOperacionWithCurrentLocation(
        operacion: Operacion,
        direccionRepo: DireccionRepository,
        onSuccess: (Operacion) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val nuevaDireccion = operacion.direccionNavigation.copy(
                        latitud = location.latitude,
                        longitud = location.longitude
                    )
                    direccionRepo.updateDireccion(
                        nuevaDireccion,
                        onSuccess = {
                            val operacionActualizada = operacion.copy(direccionNavigation = nuevaDireccion)
                            onSuccess(operacionActualizada)
                        },
                        onError = { e -> onFailure(e) }
                    )
                } else {
                    onFailure(Exception("No se pudo obtener la ubicación actual"))
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }


    fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }


    private fun createCircleMarker(): BitmapDrawable {
        val size = 60
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val center = size / 2f

        val haloPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#553399FF")
            style = Paint.Style.FILL
        }
        val haloRadius = size / 2f
        canvas.drawCircle(center, center, haloRadius, haloPaint)

        val solidPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#3399FF")
            style = Paint.Style.FILL
        }
        val solidRadius = size / 3f
        canvas.drawCircle(center, center, solidRadius, solidPaint)

        return BitmapDrawable(context.resources, bitmap)
    }

    private fun createNumberedIcon(number: Int?, @ColorRes colorCode: Int, @ColorRes innerColorCode: Int): BitmapDrawable {
        val width = 100
        val height = 100
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        //Semicirculo superior
        val paintFill = Paint().apply {
            isAntiAlias = true
            color = ContextCompat.getColor(context, colorCode)
            style = Paint.Style.FILL
        }

        val rect = RectF(
            10f, 0f, width.toFloat() - 10,  height.toFloat()
        )
        canvas.drawArc(rect, 180f, 180f, true, paintFill)


        // Dibuja la punta (más estrecha y corta)
        val path = android.graphics.Path().apply {
            moveTo(10f, height.toFloat()/2)
            lineTo(width.toFloat()/2, height.toFloat())
            lineTo(width.toFloat() - 10, height.toFloat()/2)
            close()
        }
        canvas.drawPath(path, paintFill)


        val innerFill = Paint().apply {
            isAntiAlias = true
            color = ContextCompat.getColor(context, innerColorCode)
            style = Paint.Style.FILL
        }

        canvas.drawCircle(width.toFloat()/2, height.toFloat()/2 - 10, height.toFloat()/2 - 25, innerFill)

        // Texto en el centro del círculo
        number?.let {
            val paintText = Paint().apply {
                color = Color.BLACK
                textSize = 42f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText(it.toString(), width.toFloat()/2, height.toFloat()/2, paintText)
        }

        return BitmapDrawable(context.resources, bitmap)
    }

    fun drawRutaOptimaConExtremos(operaciones: List<Operacion>) {
        // Limpiar polylines previas
        mapView.overlays.removeAll { it is Polyline }

        val polyline = Polyline(mapView).apply {
            width = 8f
            color = Color.BLUE
            isGeodesic = true
        }

        val points = mutableListOf<GeoPoint>()

        myLocationMarker?.let {
            points.add(it.position)
        }

        operaciones.forEach { operacion ->
            val direccion = operacion.direccionNavigation
            val lat = direccion.latitud
            val lon = direccion.longitud
            if (lat != null && lon != null && operacion.estado != EstadoOperacion.FINALIZADA) {
                points.add(GeoPoint(lat, lon))
            }
        }

        polyline.setPoints(points)
        mapView.overlays.add(polyline)

        // Dibujar círculos en los extremos
        if (points.isNotEmpty()) {
            points.firstOrNull()?.let { dibujarCirculoEnPunto(it) }
            points.lastOrNull()?.let { dibujarCirculoEnPunto(it) }
        }

        mapView.invalidate()
    }

    private fun dibujarCirculoEnPunto(punto: GeoPoint) {
        val marker = Marker(mapView).apply {
            position = punto
            icon = crearCirculoPequeno(Color.BLUE, 30) // color y tamaño
            setAnchor(0.5f, 0.5f) // centrado
            isDraggable = false
        }
        mapView.overlays.add(marker)
    }

    private fun crearCirculoPequeno(color: Int, size: Int): BitmapDrawable {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint().apply {
            isAntiAlias = true
            this.color = color
            style = Paint.Style.FILL
        }

        val center = size / 2f
        val radius = size / 2f
        canvas.drawCircle(center, center, radius, paint)

        return BitmapDrawable(context.resources, bitmap)
    }

    fun drawRutaOptima(points: List<GeoPoint>) {
        if (points.size < 2) {
            Log.e("MapService", "Se necesitan al menos 2 puntos para trazar una ruta.")
            return
        }

        val coordinates = points.joinToString(";") { "${it.longitude},${it.latitude}" }

        val url =
            "https://router.project-osrm.org/route/v1/driving/$coordinates?overview=full&geometries=geojson"

        val queue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    if (json.optString("code") != "Ok") {
                        Log.e("MapService", "Error en respuesta OSRM: ${json.optString("code")}")
                        return@StringRequest
                    }

                    val routes = json.getJSONArray("routes")
                    if (routes.length() == 0) {
                        Log.e("MapService", "No se encontró ruta entre los puntos.")
                        return@StringRequest
                    }

                    val geometry = routes.getJSONObject(0).getJSONObject("geometry")
                    val coordinatesArray = geometry.getJSONArray("coordinates")

                    val geoPoints = mutableListOf<GeoPoint>()
                    for (i in 0 until coordinatesArray.length()) {
                        val coord = coordinatesArray.getJSONArray(i)
                        val lon = coord.getDouble(0)
                        val lat = coord.getDouble(1)
                        geoPoints.add(GeoPoint(lat, lon))
                    }

                    drawPolyline(geoPoints)

                } catch (e: Exception) {
                    Log.e("MapService", "Error al procesar respuesta: ${e.message}", e)
                }
            },
            { error ->
                Log.e("MapService", "Error en la petición OSRM: ${error.message}", error)
            }
        )

        queue.add(stringRequest)
    }

    fun drawPolyline(puntos: List<GeoPoint>) {
        if (puntos.size < 2) return

        mapView.overlays.removeAll { it is Polyline }

        val colores = listOf(
            ContextCompat.getColor(context, R.color.route_1),
            ContextCompat.getColor(context, R.color.route_2),
            ContextCompat.getColor(context, R.color.route_3),
            ContextCompat.getColor(context, R.color.route_4),
        )

        for (i in 0 until puntos.size - 1) {
            val inicio = puntos[i]
            val fin = puntos[i + 1]

            val polyline = Polyline().apply {
                addPoint(inicio)
                addPoint(fin)
                outlinePaint.color = colores[i % colores.size]
                outlinePaint.strokeWidth = 10f
            }

            mapView.overlays.add(0, polyline)
        }

        mapView.invalidate()
    }
}
