package com.upc.appgestiones.core.data.model

data class Cliente(
    val idCliente: Int,
    val nombres: String,
    val apellidos: String,
    val documento: String,
    val direcciones: List<Direccion>,
    val contactos: List<Contacto>
)
