package com.upc.appgestiones.core.data.model

data class Direccion(
    val idDireccion: Int,
    val idCliente: Int,
    val calle: String,
    val numero: String?,
    val ciudad: String,
    val provincia: String,
    val referencia: String,
    val latitud: Double?,
    val longitud: Double?
)
