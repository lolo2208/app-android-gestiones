package com.upc.appgestiones.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upc.appgestiones.core.data.model.Operacion

class BienvenidaViewModel : ViewModel() {

    private val _operaciones = MutableLiveData<List<Operacion>>()
    val operaciones: LiveData<List<Operacion>> = _operaciones

    fun setOperaciones(lista: List<Operacion>) {
        _operaciones.value = lista
    }

}