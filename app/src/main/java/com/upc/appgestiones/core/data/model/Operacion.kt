package com.upc.appgestiones.core.data.model
import java.io.Serializable
data class Operacion(
    val id: Int,
    val idDireccion: Int,
    val idCliente: Int,
    val asunto: String,
    val tipo: TipoOperacion,
    val monto: Double? = null,
    val fecha: String,
    val estado: EstadoOperacion,
    val clienteNavigation: Cliente,
    val direccionNavigation: Direccion
) : Serializable


{

    companion object {
        fun fetchOperaciones(): List<Operacion> {
            return listOf(
                Operacion(
                    id = 1,
                    idDireccion = 1,
                    idCliente = 1,
                    asunto = "Cobranza pendiente",
                    tipo = TipoOperacion.COBRANZA,
                    monto = 1500.0,
                    fecha = "2025-09-20",
                    estado = EstadoOperacion.PENDIENTE,
                    clienteNavigation = Cliente(
                        idCliente = 1,
                        nombres = "Juan",
                        apellidos = "Pérez López",
                        documento = "12345678",
                        direcciones = emptyList(),
                        contactos = emptyList()
                    ),
                    direccionNavigation = Direccion(
                        idDireccion = 1,
                        idCliente = 1,
                        calle = "Av. Arequipa",
                        numero = "1234",
                        ciudad = "Lima",
                        provincia = "Lima",
                        referencia = "Cerca al KFC",
                        latitud = -12.076,
                        longitud = -77.036
                    )
                ),
                Operacion(
                    id = 2,
                    idDireccion = 2,
                    idCliente = 2,
                    asunto = "Verificación negocio",
                    tipo = TipoOperacion.VERIFICACION,
                    estado = EstadoOperacion.PENDIENTE,
                    monto = 250.0,
                    fecha = "2025-09-19",
                    clienteNavigation = Cliente(
                        idCliente = 2,
                        nombres = "María",
                        apellidos = "Gómez Ruiz",
                        documento = "87654321",
                        direcciones = emptyList(),
                        contactos = emptyList()
                    ),
                    direccionNavigation = Direccion(
                        idDireccion = 2,
                        idCliente = 2,
                        calle = "Jr. de la Unión",
                        numero = "850",
                        ciudad = "Lima",
                        provincia = "Lima",
                        referencia = "Por el parque",
                        latitud = -12.0464,
                        longitud = -77.0428
                    )
                ),
                Operacion(
                    id = 3,
                    idDireccion = 3,
                    idCliente = 3,
                    asunto = "Verificación domicilio",
                    tipo = TipoOperacion.VERIFICACION,
                    estado = EstadoOperacion.PENDIENTE,
                    monto = null,
                    fecha = "2025-09-18",
                    clienteNavigation = Cliente(
                        idCliente = 3,
                        nombres = "Carlos",
                        apellidos = "Ramírez Soto",
                        documento = "45678912",
                        direcciones = emptyList(),
                        contactos = emptyList()
                    ),
                    direccionNavigation = Direccion(
                        idDireccion = 3,
                        idCliente = 3,
                        calle = "Av. Javier Prado Este",
                        numero = "456",
                        ciudad = "San Isidro",
                        provincia = "Lima",
                        referencia = "Frente al Metro",
                        latitud = -12.089,
                        longitud = -77.032
                    )
                ),
                Operacion(
                    id = 4,
                    idDireccion = 4,
                    idCliente = 4,
                    asunto = "Cobranza atrasada",
                    tipo = TipoOperacion.COBRANZA,
                    estado = EstadoOperacion.FINALIZADA,
                    monto = 2000.00,
                    fecha = "2025-09-17",
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
                ),
                Operacion(
                    id = 5,
                    idDireccion = 5,
                    idCliente = 5,
                    asunto = "Cobranza de equipo",
                    tipo = TipoOperacion.COBRANZA,
                    estado = EstadoOperacion.EN_RUTA,
                    monto = 1500.00,
                    fecha = "2025-09-21",
                    clienteNavigation = Cliente(
                        idCliente = 5,
                        nombres = "Ana",
                        apellidos = "Martínez Solis",
                        documento = "23456789",
                        direcciones = emptyList(),
                        contactos = emptyList()
                    ),
                    direccionNavigation = Direccion(
                        idDireccion = 5,
                        idCliente = 5,
                        calle = "Av. Petit Thouars",
                        numero = "1010",
                        ciudad = "Lima",
                        provincia = "Lima",
                        referencia = "Cerca de Wong",
                        latitud = -12.058,
                        longitud = -77.050
                    )
                ),
                Operacion(
                    id = 6,
                    idDireccion = 6,
                    idCliente = 6,
                    asunto = "Revisión de edificio",
                    tipo = TipoOperacion.VERIFICACION,
                    estado = EstadoOperacion.PENDIENTE,
                    monto = null,
                    fecha = "2025-09-22",
                    clienteNavigation = Cliente(
                        idCliente = 6,
                        nombres = "Pedro",
                        apellidos = "González M.",
                        documento = "34567891",
                        direcciones = emptyList(),
                        contactos = emptyList()
                    ),
                    direccionNavigation = Direccion(
                        idDireccion = 6,
                        idCliente = 6,
                        calle = "Av. Universitaria",
                        numero = "500",
                        ciudad = "Lima",
                        provincia = "Lima",
                        referencia = "Frente a la universidad",
                        latitud = -12.072,
                        longitud = -77.025
                    )
                ),
                Operacion(
                    id = 7,
                    idDireccion = 7,
                    idCliente = 7,
                    asunto = "Copro empeño",
                    tipo = TipoOperacion.COBRANZA,
                    estado = EstadoOperacion.PENDIENTE,
                    monto = 1800.00,
                    fecha = "2025-09-23",
                    clienteNavigation = Cliente(
                        idCliente = 7,
                        nombres = "Sofía",
                        apellidos = "Vega Torres",
                        documento = "56789123",
                        direcciones = emptyList(),
                        contactos = emptyList()
                    ),
                    direccionNavigation = Direccion(
                        idDireccion = 7,
                        idCliente = 7,
                        calle = "Calle Los Pinos",
                        numero = "220",
                        ciudad = "Lima",
                        provincia = "Lima",
                        referencia = "Cerca al supermercado",
                        latitud = -12.062,
                        longitud = -77.040
                    )
                ),
                Operacion(
                    id = 8,
                    idDireccion = 8,
                    idCliente = 8,
                    asunto = "Cobranza final",
                    tipo = TipoOperacion.COBRANZA,
                    estado = EstadoOperacion.FINALIZADA,
                    monto = 3000.0,
                    fecha = "2025-09-24",
                    clienteNavigation = Cliente(
                        idCliente = 8,
                        nombres = "Miguel",
                        apellidos = "Torres Peña",
                        documento = "67891234",
                        direcciones = emptyList(),
                        contactos = emptyList()
                    ),
                    direccionNavigation = Direccion(
                        idDireccion = 8,
                        idCliente = 8,
                        calle = "Av. Tacna",
                        numero = "150",
                        ciudad = "Lima",
                        provincia = "Lima",
                        referencia = "Frente al banco",
                        latitud = -12.050,
                        longitud = -77.038
                    )
                )
            )
        }
    }

}

