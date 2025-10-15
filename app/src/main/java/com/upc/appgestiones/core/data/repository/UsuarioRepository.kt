package com.upc.appgestiones.core.data.repository

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.upc.appgestiones.core.data.model.Usuario
import org.json.JSONObject

class UsuarioRepository(private val context: Context) {

    private val gson = Gson()
    private val baseUrl = "https://plzzgdork5.execute-api.us-east-1.amazonaws.com/Stage3"

    fun login(
        username: String,
        password: String,
        onResult: (Usuario?, Exception?) -> Unit
    ) {
        val url = "$baseUrl/Usuario?username=$username&password=$password"

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    val bodyString = response.getString("body")
                    val usuario: Usuario = gson.fromJson(bodyString, Usuario::class.java)
                    onResult(usuario, null)
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
}
