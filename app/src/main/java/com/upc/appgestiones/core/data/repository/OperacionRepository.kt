package com.upc.appgestiones.core.data.repository

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.upc.appgestiones.core.data.model.Operacion
import operacionFromJson
import org.json.JSONArray
import org.json.JSONObject

class OperacionRepository(private val context: Context) {

    private val gson = Gson()
    private val baseUrl = "https://plzzgdork5.execute-api.us-east-1.amazonaws.com/Stage3"

    fun listarOperaciones(onResult: (List<Operacion>?, Exception?) -> Unit) {
        val url = "$baseUrl/Operaciones"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response: JSONObject ->
                try {
                    val bodyString = response.getString("body")
                    val listType = object : TypeToken<List<Operacion>>() {}.type
                    val operaciones: List<Operacion> = gson.fromJson(bodyString, listType)
                    onResult(operaciones, null)
                } catch (e: Exception) {
                    onResult(null, e)
                }
            },
            { error ->
                onResult(null, Exception(error.message))
            }
        )

        Volley.newRequestQueue(context).add(request)
    }

    fun listarOperacionesPorUsuario(onResult: (List<Operacion>?, Exception?) -> Unit) {
        try {
            val prefs = context.getSharedPreferences("AppGestionesPrefs", Context.MODE_PRIVATE)
            val jsonUsuario = prefs.getString("usuario", null)

            if (jsonUsuario == null) {
                onResult(null, Exception("Usuario no encontrado en preferencias"))
                return
            }

            val jsonObj = JSONObject(jsonUsuario)
            val idUsuario = jsonObj.optInt("idUsuario", -1)
            if (idUsuario == -1) {
                onResult(null, Exception("idUsuario inválido"))
                return
            }

            val url = "$baseUrl/Operaciones?idUsuario=$idUsuario"

            val request = JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                { response ->
                    try {
                        val bodyString = response.getString("body")
                        val listType = object : TypeToken<List<Operacion>>() {}.type
                        val operaciones: List<Operacion> = gson.fromJson(bodyString, listType)
                        onResult(operaciones, null)
                    } catch (e: Exception) {
                        onResult(null, e)
                    }
                },
                { error ->
                    onResult(null, Exception(error.message))
                }
            )

            Volley.newRequestQueue(context).add(request)
        } catch (e: Exception) {
            onResult(null, e)
        }
    }

    fun updateOperacion(
        operacion: Operacion,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val url = "$baseUrl/Operacion"
        val body = JSONObject().apply {
            put("idOperacion", operacion.idOperacion)
            put("asunto", operacion.asunto)
            put("tipo", operacion.tipo)
            put("monto", operacion.monto)
            put("fechaVencimiento", operacion.fechaVencimiento)
            put("estado", operacion.estado.name)
        }

        val request = JsonObjectRequest(
            Request.Method.PUT,
            url,
            body,
            {
                onSuccess()
            },
            { error ->
                onError(Exception(error.message))
            }
        )

        Volley.newRequestQueue(context).add(request)
    }

}
