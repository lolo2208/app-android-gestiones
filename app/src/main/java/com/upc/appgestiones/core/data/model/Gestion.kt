package com.upc.appgestiones.core.data.model

import android.R
import java.io.Serializable

data class Gestion(
    val idGestion: Int,
    val idOperacion: Int,
    val fechaRegistro: String,
    val respuesta: String,
    val formularioJson: String,
    val urlGrabacionVoz: String? = null,
    val urlFotoEvidencia: String? = null,
    val observacion: String? = null,
    val operacionNavigation: Operacion
) : Serializable {

    companion object {
        fun fetchGestionesFinalizadas(): List<Gestion> {
            return listOf(
                Gestion(
                    idGestion = 1,
                    idOperacion = 4,
                    fechaRegistro = "2025-09-17T09:00:00",
                    respuesta = "PAGPAR",
                    formularioJson = """
                    {
                      "montoCobrable": 2000.0,
                      "estadoPago": "PENDIENTE",
                      "fotoComprobante": "https://i.imgur.com/DvpvklR.png",
                      "fechaPago": "2025-09-17",
                      "observaciones": "Se recibió pago parcial, faltante por cobrar"
                    }
                """.trimIndent(),
                    urlGrabacionVoz = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                    urlFotoEvidencia = "https://i.imgur.com/DvpvklR.png",
                    operacionNavigation = Operacion(
                        idOperacion = 4,
                        idDireccion = 4,
                        idCliente = 4,
                        idUsuario = 1,
                        asunto = "Cobranza atrasada",
                        tipo = TipoOperacion.COBRANZA,
                        monto = 2000.0,
                        fechaVencimiento = "2025-09-17",
                        estado = EstadoOperacion.FINALIZADA,
                        clienteNavigation = Cliente(
                            idCliente = 4,
                            nombres = "Lucía",
                            apellidos = "Fernández Castro",
                            documento = "78912345",
                            direcciones = emptyList(),
                            contactos = emptyList()
                        ),
                        direccionNavigation = Direccion(
                            idDireccion = 4,
                            idCliente = 4,
                            calle = "Av. Brasil",
                            numero = "789",
                            ciudad = "Jesús María",
                            provincia = "Lima",
                            referencia = "Frente a Candy",
                            latitud = -12.073,
                            longitud = -77.055
                        )
                    )
                ),
                Gestion(
                    idGestion = 2,
                    idOperacion = 8,
                    respuesta = "PAGCOM",
                    fechaRegistro = "2025-09-24T14:30:00",
                    formularioJson = """
                    {
                      "montoCobrable": 3500.0,
                      "estadoPago": "PAGADO",
                      "fotoComprobante": "https://i.imgur.com/DvpvklR.png",
                      "fechaPago": "2025-09-24",
                      "observaciones": "Cliente canceló el total"
                    }
                """.trimIndent(),
                    urlGrabacionVoz = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                    urlFotoEvidencia = "https://i.imgur.com/DvpvklR.png",
                    operacionNavigation = Operacion(
                        idOperacion = 8,
                        idDireccion = 8,
                        idCliente = 8,
                        idUsuario = 1,
                        asunto = "Cobranza final",
                        tipo = TipoOperacion.COBRANZA,
                        monto = 3500.0,
                        fechaVencimiento = "2025-09-24",
                        estado = EstadoOperacion.FINALIZADA,
                        clienteNavigation = Cliente(
                            idCliente = 8,
                            nombres = "Carlos",
                            apellidos = "Ramírez López",
                            documento = "98765432",
                            direcciones = emptyList(),
                            contactos = emptyList()
                        ),
                        direccionNavigation = Direccion(
                            idDireccion = 8,
                            idCliente = 8,
                            calle = "Av. La Marina",
                            numero = "456",
                            ciudad = "San Miguel",
                            provincia = "Lima",
                            referencia = "Frente a Plaza San Miguel",
                            latitud = -12.089,
                            longitud = -77.078
                        )
                    )
                )
            )
        }
    }

}
