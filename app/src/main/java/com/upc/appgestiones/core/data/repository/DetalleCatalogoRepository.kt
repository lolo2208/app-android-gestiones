package com.upc.appgestiones.core.data.repository

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.upc.appgestiones.core.data.model.DetalleCatalogo
import detalleCatalogoFromJson
import org.json.JSONArray
import org.json.JSONObject

class DetalleCatalogoRepository(private val context: Context) {

    private val baseUrl = "https://plzzgdork5.execute-api.us-east-1.amazonaws.com/Stage3"

    fun getDetalleCatalogos(
        onSuccess: (List<DetalleCatalogo>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val url = "$baseUrl/DetalleCatalogo"
        val requestQueue = Volley.newRequestQueue(context)

        // Usamos JsonObjectRequest porque el body envuelto viene en un objeto
        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response: JSONObject ->
                try {
                    val bodyStr = response.optString("body")
                    if (bodyStr.isNullOrEmpty()) {
                        throw Exception("Respuesta sin cuerpo válido")
                    }

                    val jsonArray = JSONArray(bodyStr)

                    val detalles = (0 until jsonArray.length()).map {
                        detalleCatalogoFromJson(jsonArray.getJSONObject(it))
                    }
                    onSuccess(detalles)
                } catch (e: Exception) {
                    onError(e)
                }
            },
            { error ->
                onError(Exception(error.message ?: "Error desconocido"))
            }
        )

        requestQueue.add(request)
    }
}
