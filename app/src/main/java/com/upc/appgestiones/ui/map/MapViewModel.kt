package com.upc.appgestiones.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
}