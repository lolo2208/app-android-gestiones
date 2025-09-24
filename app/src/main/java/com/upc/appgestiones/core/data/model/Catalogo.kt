package com.upc.appgestiones.core.data.model

data class Catalogo(
    val idCatalogo: Int,
    val codigoCatalogo: String,
    val nombreCatalogo: String,
    val detallesCatalogo: List<DetalleCatalogo>
) {
    companion object {
        fun fetchCatalogos(): List<Catalogo> {

            val estadosNegocioDetalles = listOf(
                DetalleCatalogo(1, "Abierto", "ABIERTO"),
                DetalleCatalogo(2, "Cerrado", "CERRADO"),
                DetalleCatalogo(3, "Temporalmente cerrado", "TEMPORAL")
            )

            val tiposNegocioDetalles = listOf(
                DetalleCatalogo(4, "Restaurante", "RESTAURANTE"),
                DetalleCatalogo(5, "Tienda de ropa", "ROPA"),
                DetalleCatalogo(6, "Supermercado", "SUPERMERCADO"),
                DetalleCatalogo(7, "Cafetería", "CAFETERIA")
            )

            val estadosPagoDetalles = listOf(
                DetalleCatalogo(8, "Pagado", "PAGADO"),
                DetalleCatalogo(9, "Pendiente", "PENDIENTE"),
                DetalleCatalogo(10, "Vencido", "VENCIDO")
            )

            return listOf(
                Catalogo(1, "ESTADOS_NEGOCIO", "EstadosNegocio", estadosNegocioDetalles),
                Catalogo(2, "TIPOS_NEGOCIO", "TiposNegocio", tiposNegocioDetalles),
                Catalogo(3, "ESTADOS_PAGO", "EstadosPago", estadosPagoDetalles)
            )
        }
    }
}
