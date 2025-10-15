import com.upc.appgestiones.core.data.model.*
import org.json.JSONArray
import org.json.JSONObject


fun direccionFromJson(json: JSONObject): Direccion {
    return Direccion(
        idDireccion = json.optInt("idDireccion"),
        idCliente = json.optInt("idCliente"),
        calle = json.optString("calle"),
        numero = json.optString("numero", null),
        ciudad = json.optString("ciudad"),
        provincia = json.optString("provincia"),
        referencia = json.optString("referencia"),
        latitud = json.optDouble("latitud", Double.NaN).takeIf { !it.isNaN() },
        longitud = json.optDouble("longitud", Double.NaN).takeIf { !it.isNaN() }
    )
}

fun direccionToJson(d: Direccion): JSONObject {
    return JSONObject().apply {
        put("idDireccion", d.idDireccion)
        put("idCliente", d.idCliente)
        put("calle", d.calle)
        put("numero", d.numero)
        put("ciudad", d.ciudad)
        put("provincia", d.provincia)
        put("referencia", d.referencia)
        put("latitud", d.latitud)
        put("longitud", d.longitud)
    }
}


fun clienteFromJson(json: JSONObject): Cliente {
    val direccionesArray = json.optJSONArray("direcciones") ?: JSONArray()
    val contactosArray = json.optJSONArray("contactos") ?: JSONArray()

    val direcciones = (0 until direccionesArray.length()).map {
        direccionFromJson(direccionesArray.getJSONObject(it))
    }

    val contactos = (0 until contactosArray.length()).map {
        contactoFromJson(contactosArray.getJSONObject(it))
    }

    return Cliente(
        idCliente = json.optInt("idCliente"),
        nombres = json.optString("nombres"),
        apellidos = json.optString("apellidos"),
        documento = json.optString("documento"),
        direcciones = direcciones,
        contactos = contactos
    )
}

fun clienteToJson(c: Cliente): JSONObject {
    val direccionesArray = JSONArray(c.direcciones.map { direccionToJson(it) })
    val contactosArray = JSONArray(c.contactos.map { contactoToJson(it) })

    return JSONObject().apply {
        put("idCliente", c.idCliente)
        put("nombres", c.nombres)
        put("apellidos", c.apellidos)
        put("documento", c.documento)
        put("direcciones", direccionesArray)
        put("contactos", contactosArray)
    }
}


fun contactoFromJson(json: JSONObject): Contacto {
    return Contacto(
        idContacto = json.optInt("idContacto"),
        idCliente = json.optInt("idCliente"),
        tipo = json.optString("tipo"),
        valor = json.optString("valor"),
        clienteNavigation = clienteFromJson(json.optJSONObject("clienteNavigation") ?: JSONObject())
    )
}

fun contactoToJson(c: Contacto): JSONObject {
    return JSONObject().apply {
        put("idContacto", c.idContacto)
        put("idCliente", c.idCliente)
        put("tipo", c.tipo)
        put("valor", c.valor)
        put("clienteNavigation", clienteToJson(c.clienteNavigation))
    }
}


fun operacionFromJson(json: JSONObject): Operacion {
    return Operacion(
        idOperacion = json.optInt("idOperacion"),
        idDireccion = json.optInt("idDireccion"),
        idCliente = json.optInt("idCliente"),
        idUsuario = json.optInt("idUsuario"),
        asunto = json.optString("asunto"),
        tipo = TipoOperacion.valueOf(json.optString("tipo")),
        monto = json.optDouble("monto", Double.NaN).takeIf { !it.isNaN() },
        fechaVencimiento = json.optString("fechaVencimiento"),
        estado = EstadoOperacion.valueOf(json.optString("estado")),
        clienteNavigation = clienteFromJson(json.optJSONObject("clienteNavigation") ?: JSONObject()),
        direccionNavigation = direccionFromJson(json.optJSONObject("direccionNavigation") ?: JSONObject())
    )
}

fun operacionToJson(o: Operacion): JSONObject {
    return JSONObject().apply {
        put("idOperacion", o.idOperacion)
        put("idDireccion", o.idDireccion)
        put("idCliente", o.idCliente)
        put("idUsuario", o.idUsuario)
        put("asunto", o.asunto)
        put("tipo", o.tipo.name)
        put("monto", o.monto)
        put("fechaVencimiento", o.fechaVencimiento)
        put("estado", o.estado.name)
        put("clienteNavigation", clienteToJson(o.clienteNavigation))
        put("direccionNavigation", direccionToJson(o.direccionNavigation))
    }
}


fun gestionFromJson(json: JSONObject): Gestion {
    val operacionNavJson = json.optJSONObject("operacionNavigation")

    return Gestion(
        idGestion = json.optInt("idGestion"),
        idOperacion = json.optInt("idOperacion"),
        fechaRegistro = json.optString("fechaRegistro"),
        respuesta = json.optString("respuesta"),
        formularioJson = json.optString("formularioJson"),
        urlGrabacionVoz = json.optString("urlGrabacionVoz", null),
        urlFotoEvidencia = json.optString("urlFotoEvidencia", null),
        observacion = json.optString("observacion", null),
        operacionNavigation = operacionNavJson?.let { operacionFromJson(it) }
    )
}

fun gestionToJson(g: Gestion): JSONObject {
    return JSONObject().apply {
        put("idGestion", g.idGestion)
        put("idOperacion", g.idOperacion)
        put("fechaRegistro", g.fechaRegistro)
        put("respuesta", g.respuesta)
        put("formularioJson", g.formularioJson)
        put("urlGrabacionVoz", g.urlGrabacionVoz)
        put("urlFotoEvidencia", g.urlFotoEvidencia)
        put("observacion", g.observacion)
        put("operacionNavigation", g.operacionNavigation?.let { operacionToJson(it) })
    }
}

fun detalleCatalogoFromJson(json: JSONObject): DetalleCatalogo {
    return DetalleCatalogo(
        idDetalleCatalogo = json.optInt("idDetalleCatalogo"),
        codigoDetalle = json.optString("codigoDetalle"),
        descripcion = json.optString("descripcion")
    )
}

fun detalleCatalogoToJson(d: DetalleCatalogo): JSONObject {
    return JSONObject().apply {
        put("idDetalleCatalogo", d.idDetalleCatalogo)
        put("codigoDetalle", d.codigoDetalle)
        put("descripcion", d.descripcion)
    }
}
