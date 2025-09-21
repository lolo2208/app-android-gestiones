package com.upc.appgestiones.core.data.model

data class CampoFormulario(
    val idCampoFormulario: Int,
    val nombreCampo: String,
    val etiqueta: String,
    val tipoCampo: TipoCampo,
    val esObligatorio: Boolean,
    val nombreCatalogo: String? = null
)