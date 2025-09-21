package com.upc.appgestiones.core.data.model

data class Catalogo (
    val idCatalogo:Int,
    val codigoCatalogo:String,
    val nombreCatalogo:String,
    val detallesCatalogo:List<DetalleCatalogo>
)