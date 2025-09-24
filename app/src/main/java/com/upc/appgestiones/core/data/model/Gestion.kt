package com.upc.appgestiones.core.data.model

import android.R
import java.io.Serializable

data class Gestion(
    val idGestion: Int,
    val idOperacion: Int,
    val fechaRegistro: String,
    val formularioJson: String,
    val urlGrabacionVoz: String? = null,
    val urlFotoEvidencia: String? = null,
    val operacionNavigation: Operacion
) : Serializable {

    companion object {
        fun fetchGestionesFinalizadas(): List<Gestion> {
            return listOf(
                Gestion(
                    idGestion = 1,
                    idOperacion = 4,
                    fechaRegistro = "2025-09-17T09:00:00",
                    formularioJson = """{"respuesta": "Pago realizado"}""",
                    urlGrabacionVoz = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                    urlFotoEvidencia = "https://i.imgur.com/DvpvklR.png",
                    operacionNavigation = Operacion(
                        id = 4,
                        idDireccion = 4,
                        idCliente = 4,
                        asunto = "Cobranza atrasada",
                        tipo = TipoOperacion.COBRANZA,
                        monto = 2000.0,
                        fecha = "2025-09-17",
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
                    fechaRegistro = "2025-09-24T14:30:00",
                    formularioJson = """{"respuesta": "Cliente canceló"}""",
                    urlGrabacionVoz = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                    urlFotoEvidencia = "https://i.imgur.com/DvpvklR.png",
                    operacionNavigation = Operacion(
                        id = 8,
                        idDireccion = 8,
                        idCliente = 8,
                        asunto = "Cobranza final",
                        tipo = TipoOperacion.COBRANZA,
                        monto = 3500.0,
                        fecha = "2025-09-24",
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
