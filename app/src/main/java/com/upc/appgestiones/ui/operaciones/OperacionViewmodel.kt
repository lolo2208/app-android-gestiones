package com.upc.appgestiones.ui.operaciones

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upc.appgestiones.core.data.model.Operacion

class OperacionViewmodel : ViewModel() {

    private val _operaciones = MutableLiveData<List<Operacion>>()
    val operaciones: LiveData<List<Operacion>> = _operaciones

    init {
        refreshOperaciones()
    }

    fun refreshOperaciones() {
        val lista = Operacion.fetchOperaciones()
        _operaciones.value = lista
    }

    fun setOperaciones(lista:List<Operacion>) {
        _operaciones.value = lista
    }

    fun actualizarOperacion(operacionActualizada: Operacion) {
        val listaActual = _operaciones.value?.toMutableList() ?: mutableListOf()
        val index = listaActual.indexOfFirst { it.id == operacionActualizada.id }
        if (index != -1) {
            listaActual[index] = operacionActualizada
            _operaciones.value = listaActual
        }
    }
}
