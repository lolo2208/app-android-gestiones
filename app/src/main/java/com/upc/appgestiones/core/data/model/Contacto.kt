package com.upc.appgestiones.core.data.model

data class Contacto(
    val idContacto: Int,
    val idCliente: Int,
    val tipo: String,
    val valor: String,
    val clienteNavigation: Cliente
)
