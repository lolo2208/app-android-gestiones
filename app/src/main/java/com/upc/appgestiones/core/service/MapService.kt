package com.upc.appgestiones.core.services

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.EstadoOperacion
import com.upc.appgestiones.core.data.model.Operacion
import com.upc.appgestiones.ui.map.MapViewModel
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

            val colorRes = when (operacion.estado) {
                EstadoOperacion.PENDIENTE -> R.color.custom_red
                EstadoOperacion.EN_RUTA -> R.color.custom_orange
                EstadoOperacion.FINALIZADA -> R.color.custom_green
                else -> R.color.black
            }

            val numero = if (conNumeracion) index + 1 else null
            val icono = createNumberedIcon(numero, colorRes)

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
                    val operacionActualizada = operacion.copy(direccionNavigation = nuevaDireccion)

                    onSuccess(operacionActualizada)
                } else {
                    onFailure(Exception("No se pudo obtener la ubicación actual"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    // Detener seguimiento en tiempo real
    fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }


    // Crear icono personalizado
    private fun createCircleMarker(): BitmapDrawable {
        val size = 60
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val center = size / 2f

        // Círculo exterior transparente (halo)
        val haloPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#553399FF")
            style = Paint.Style.FILL
        }
        val haloRadius = size / 2f
        canvas.drawCircle(center, center, haloRadius, haloPaint)

        // Círculo interior sólido
        val solidPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#3399FF")
            style = Paint.Style.FILL
        }
        val solidRadius = size / 3f
        canvas.drawCircle(center, center, solidRadius, solidPaint)

        return BitmapDrawable(context.resources, bitmap)
    }


    private fun createNumberedIcon(number: Int?, @ColorRes colorCode: Int): BitmapDrawable {
        val width = 120
        val height = 120
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val centerX = width / 2f
        val circleRadius = width / 3f
        val circleCenterY = circleRadius + 10f
        val pointY = height.toFloat()

        val paintFill = Paint().apply {
            isAntiAlias = true
            color = ContextCompat.getColor(context, colorCode)
            style = Paint.Style.FILL
        }

        // Dibuja el círculo superior
        canvas.drawCircle(centerX, circleCenterY, circleRadius, paintFill)

        // Dibuja la punta (más estrecha y corta)
        val path = android.graphics.Path().apply {
            moveTo(centerX - circleRadius * 0.6f, circleCenterY) // más cerca del centro
            lineTo(centerX + circleRadius * 0.6f, circleCenterY)
            lineTo(centerX, pointY)
            close()
        }
        canvas.drawPath(path, paintFill)

        // Texto en el centro del círculo
        number?.let {
            val paintText = Paint().apply {
                color = Color.WHITE
                textSize = 42f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            val textY = circleCenterY - (paintText.descent() + paintText.ascent()) / 2
            canvas.drawText(it.toString(), centerX, textY, paintText)
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

}
