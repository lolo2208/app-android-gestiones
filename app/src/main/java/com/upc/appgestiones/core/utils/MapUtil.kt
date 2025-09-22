package com.upc.appgestiones.core.utils

import com.upc.appgestiones.core.data.model.EstadoOperacion
import com.upc.appgestiones.core.data.model.Operacion
import org.osmdroid.util.GeoPoint

class MapUtil {

    companion object {

        fun obtenerDistanciaEulidiana(p1: GeoPoint, p2: GeoPoint): Double {
            val dLat = p2.latitude - p1.latitude
            val dLon = p2.longitude - p1.longitude

            val latMeters = dLat * 111_000
            val lonMeters = dLon * 111_000 * Math.cos(Math.toRadians((p1.latitude + p2.latitude) / 2))

            return Math.sqrt(latMeters * latMeters + lonMeters * lonMeters)
        }

        fun ordenarOperacionesPorRuta(operaciones: List<Operacion>, ubicacionActual: GeoPoint): List<Operacion> {
            if (operaciones.isEmpty()) return emptyList()

            val finalizadas = operaciones.filter { it.estado == EstadoOperacion.FINALIZADA }
            val pendientesYRuta = operaciones.filter { it.estado != EstadoOperacion.FINALIZADA }.toMutableList()

            val ordenadas = mutableListOf<Operacion>()
            var puntoActual = ubicacionActual

            while (pendientesYRuta.isNotEmpty()) {
                val siguiente = pendientesYRuta.minByOrNull { operacion ->
                    val lat = operacion.direccionNavigation.latitud ?: 0.0
                    val lon = operacion.direccionNavigation.longitud ?: 0.0
                    val puntoOperacion = GeoPoint(lat, lon)
                    obtenerDistanciaEulidiana(puntoActual, puntoOperacion)
                }!!

                ordenadas.add(siguiente)
                pendientesYRuta.remove(siguiente)

                val lat = siguiente.direccionNavigation.latitud ?: 0.0
                val lon = siguiente.direccionNavigation.longitud ?: 0.0
                puntoActual = GeoPoint(lat, lon)
            }

            ordenadas.addAll(finalizadas)

            return ordenadas
        }

    }

}