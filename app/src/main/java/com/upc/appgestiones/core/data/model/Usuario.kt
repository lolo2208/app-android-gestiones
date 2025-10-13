package com.upc.appgestiones.core.data.model

data class Usuario(
    val idUsuario: Int,
    val username: String,
    val password: String,
    val nombres: String,
    val apellidos: String
)
