package com.upc.appgestiones.core.data.model

import java.time.LocalDateTime

data class Gestion(
    val idGestion: Int,
    val idOperacion: Int,
    val fechaRegistro: LocalDateTime,
    val formularioJson: String,
    val urlGrabacionVoz: String? = null,
    val urlFotoEvidencia: String? = null,
    val operacionNavigation: Operacion,
)

