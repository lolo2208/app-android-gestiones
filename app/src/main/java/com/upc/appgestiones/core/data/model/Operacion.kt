package com.upc.appgestiones.core.data.model

data class Operacion(
    val id: Int,
    val idDireccion: Int,
    val idCliente: Int,
    val asunto: String,
    val tipo: TipoOperacion,
    val monto: Double? = null,
    val fecha: String,
    val clienteNavigation: Cliente,
    val direccionNavigation: Direccion,
)