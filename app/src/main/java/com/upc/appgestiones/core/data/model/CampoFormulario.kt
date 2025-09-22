package com.upc.appgestiones.core.data.model

data class CampoFormulario(
    val idCampoFormulario: Int,
    val tipoFormulario: String,
    val nombreCampo: String,
    val etiqueta: String,
    val tipoCampo: TipoCampo,
    val esObligatorio: Boolean,
    val nombreCatalogo: String? = null
) {
    companion object {

        fun fetchCampos() : List<CampoFormulario> {
            return listOf(
                // Campos para VERIFICACION
                CampoFormulario(
                    idCampoFormulario = 1,
                    tipoFormulario = "VERIFICACION",
                    nombreCampo = "estadoNegocio",
                    etiqueta = "Estado del negocio",
                    tipoCampo = TipoCampo.SELECT,
                    esObligatorio = true,
                    nombreCatalogo = "EstadosNegocio"
                ),
                CampoFormulario(
                    idCampoFormulario = 2,
                    tipoFormulario = "VERIFICACION",
                    nombreCampo = "nombrePropietario",
                    etiqueta = "Nombre del propietario",
                    tipoCampo = TipoCampo.TEXT,
                    esObligatorio = true
                ),
                CampoFormulario(
                    idCampoFormulario = 3,
                    tipoFormulario = "VERIFICACION",
                    nombreCampo = "fotoFrente",
                    etiqueta = "Foto del frente del negocio",
                    tipoCampo = TipoCampo.FOTO,
                    esObligatorio = true
                ),
                CampoFormulario(
                    idCampoFormulario = 4,
                    tipoFormulario = "VERIFICACION",
                    nombreCampo = "observaciones",
                    etiqueta = "Observaciones",
                    tipoCampo = TipoCampo.TEXT,
                    esObligatorio = false
                ),
                CampoFormulario(
                    idCampoFormulario = 5,
                    tipoFormulario = "VERIFICACION",
                    nombreCampo = "tipoNegocio",
                    etiqueta = "Tipo de negocio",
                    tipoCampo = TipoCampo.SELECT,
                    esObligatorio = true,
                    nombreCatalogo = "TiposNegocio"
                ),

                // Campos para COBRANZA
                CampoFormulario(
                    idCampoFormulario = 6,
                    tipoFormulario = "COBRANZA",
                    nombreCampo = "montoCobrable",
                    etiqueta = "Monto a cobrar",
                    tipoCampo = TipoCampo.TEXT,
                    esObligatorio = true
                ),
                CampoFormulario(
                    idCampoFormulario = 7,
                    tipoFormulario = "COBRANZA",
                    nombreCampo = "estadoPago",
                    etiqueta = "Estado del pago",
                    tipoCampo = TipoCampo.SELECT,
                    esObligatorio = true,
                    nombreCatalogo = "EstadosPago"
                ),
                CampoFormulario(
                    idCampoFormulario = 8,
                    tipoFormulario = "COBRANZA",
                    nombreCampo = "fotoComprobante",
                    etiqueta = "Foto del comprobante",
                    tipoCampo = TipoCampo.FOTO,
                    esObligatorio = false
                ),
                CampoFormulario(
                    idCampoFormulario = 9,
                    tipoFormulario = "COBRANZA",
                    nombreCampo = "fechaPago",
                    etiqueta = "Fecha del pago",
                    tipoCampo = TipoCampo.TEXT,
                    esObligatorio = true
                ),
                CampoFormulario(
                    idCampoFormulario = 10,
                    tipoFormulario = "COBRANZA",
                    nombreCampo = "observaciones",
                    etiqueta = "Observaciones",
                    tipoCampo = TipoCampo.TEXT,
                    esObligatorio = false
                )
            )
        }

    }
}