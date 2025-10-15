package com.upc.appgestiones.core.data.repository

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.upc.appgestiones.core.data.model.Direccion
import direccionFromJson
import direccionToJson
import org.json.JSONObject

class DireccionRepository(private val context: Context) {

    private val baseUrl = "https://plzzgdork5.execute-api.us-east-1.amazonaws.com/Stage3"

    fun updateDireccion(direccion: Direccion, onSuccess: (Direccion) -> Unit, onError: (Exception) -> Unit) {
        val url = "$baseUrl/Direccion"
        val body = direccionToJson(direccion)
        val requestQueue = Volley.newRequestQueue(context)

        val request = JsonObjectRequest(
            Request.Method.PUT,
            url,
            body,
            { response ->
                try {
                    val direccionActualizada = direccionFromJson(response)
                    onSuccess(direccionActualizada)
                } catch (e: Exception) {
                    onError(e)
                }
            },
            { error ->
                onError(Exception(error.message))
            }
        )

        requestQueue.add(request)
    }
}
