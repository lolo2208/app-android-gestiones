package com.upc.appgestiones.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upc.appgestiones.core.data.model.EstadoOperacion
import com.upc.appgestiones.core.data.model.Gestion
import com.upc.appgestiones.core.data.model.Operacion
import org.osmdroid.util.GeoPoint

class MapViewModel : ViewModel() {
    private val _operaciones = MutableLiveData<List<Operacion>>()
    val operaciones: LiveData<List<Operacion>> = _operaciones

    private val _miUbicacion = MutableLiveData<GeoPoint>()
    val miUbicacion: LiveData<GeoPoint> = _miUbicacion

    fun setOperaciones(lista: List<Operacion>) {
        _operaciones.value = lista
    }

    fun setUbicacion(point: GeoPoint) {
        _miUbicacion.value = point
    }

    fun manejarGestion(gestion: Gestion) : List<Operacion> {
        val listaActual = _operaciones.value?.toMutableList() ?: mutableListOf()

        val index = listaActual.indexOfFirst { it.idOperacion == gestion.idOperacion }
        if (index != -1) {
            val operacion = listaActual[index]
            val operacionFinalizada = operacion.copy(
                estado = EstadoOperacion.FINALIZADA
            )

            listaActual[index] = operacionFinalizada
            _operaciones.value = listaActual
        }

        return  listaActual
    }
}