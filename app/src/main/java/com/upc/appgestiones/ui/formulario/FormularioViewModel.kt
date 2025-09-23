package com.upc.appgestiones.ui.formulario

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upc.appgestiones.core.data.model.CampoFormulario
import com.upc.appgestiones.core.data.model.Operacion

class FormularioViewModel : ViewModel() {
    private val _operacion = MutableLiveData<Operacion>()
    val operacion: LiveData<Operacion> = _operacion

    private val _plantillas = MutableLiveData<List<CampoFormulario>>()
    val plantillas : LiveData<List<CampoFormulario>> = _plantillas

    private val _formulario = MutableLiveData<Map<String, String>>()
    val formulario: LiveData<Map<String, String>> = _formulario


    fun setOperacion(operacion: Operacion) {
        _operacion.value = operacion
    }

    fun construirFormulario() {

    }
}