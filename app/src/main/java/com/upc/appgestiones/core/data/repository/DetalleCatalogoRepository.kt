package com.upc.appgestiones.core.data.repository

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.upc.appgestiones.core.data.model.DetalleCatalogo
import detalleCatalogoFromJson
import org.json.JSONArray

class DetalleCatalogoRepository(private val context: Context) {

    private val baseUrl = "https://plzzgdork5.execute-api.us-east-1.amazonaws.com/Stage1"

    fun getDetalleCatalogos(onSuccess: (List<DetalleCatalogo>) -> Unit, onError: (Exception) -> Unit) {
        val url = "$baseUrl/DetalleCatalogo"
        val requestQueue = Volley.newRequestQueue(context)

        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response: JSONArray ->
                try {
                    val detalles = (0 until response.length()).map {
                        detalleCatalogoFromJson(response.getJSONObject(it))
                    }
                    onSuccess(detalles)
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
