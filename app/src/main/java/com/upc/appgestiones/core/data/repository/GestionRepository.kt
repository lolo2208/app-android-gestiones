package com.upc.appgestiones.core.data.repository

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.upc.appgestiones.core.data.model.Gestion
import gestionFromJson
import gestionToJson
import org.json.JSONArray
import org.json.JSONObject

class GestionRepository(private val context: Context) {

    private val baseUrl = "https://plzzgdork5.execute-api.us-east-1.amazonaws.com/Stage1"

    fun getGestiones(onSuccess: (List<Gestion>) -> Unit, onError: (Exception) -> Unit) {
        val url = "$baseUrl/Gestiones"
        val requestQueue = Volley.newRequestQueue(context)

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    val bodyString = response.getString("body")

                    val bodyArray = JSONArray(bodyString)

                    val gestiones = (0 until bodyArray.length()).map {
                        gestionFromJson(bodyArray.getJSONObject(it))
                    }

                    onSuccess(gestiones)
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


    fun postGestion(gestion: Gestion, onSuccess: (Gestion) -> Unit, onError: (Exception) -> Unit) {
        val url = "$baseUrl/Gestion"
        val body = gestionToJson(gestion)
        val requestQueue = Volley.newRequestQueue(context)

        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            body,
            { response ->
                try {
                    val gestionCreada = gestionFromJson(response)
                    onSuccess(gestionCreada)
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
