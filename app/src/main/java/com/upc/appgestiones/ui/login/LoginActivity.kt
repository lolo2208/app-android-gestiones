package com.upc.appgestiones.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.upc.appgestiones.R
import com.upc.appgestiones.ui.home.HomeActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var tilUsuario: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var edtUsuario: TextInputEditText
    private lateinit var edtPassword: TextInputEditText
    private lateinit var btnAcceder: Button


    private val apiUrl = "https://zwgxy2kgo4.execute-api.us-east-1.amazonaws.com/v1/login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        tilUsuario = findViewById(R.id.tilUsuario)
        tilPassword = findViewById(R.id.tilPassword)
        edtUsuario = findViewById(R.id.edtUsuario)
        edtPassword = findViewById(R.id.edtPassword)
        btnAcceder = findViewById(R.id.btnAcceder)

        btnAcceder.isEnabled = false

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val user = edtUsuario.text.toString().trim()
                val pass = edtPassword.text.toString().trim()
                btnAcceder.isEnabled = user.isNotEmpty() && pass.isNotEmpty()
                tilUsuario.error = null
                tilPassword.error = null
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        edtUsuario.addTextChangedListener(watcher)
        edtPassword.addTextChangedListener(watcher)

        btnAcceder.setOnClickListener {
            validarCredenciales()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun validarCredenciales() {
        val user = edtUsuario.text.toString().trim()
        val pass = edtPassword.text.toString().trim()

        val urlWithParams = "$apiUrl?username=$user&password=$pass"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(urlWithParams)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    tilPassword.error = "Error de conexión: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful && body != null) {
                        val json = JSONObject(body)
                        if (json.has("usuario")) {

                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            tilPassword.error = json.optString("message", "Credenciales inválidas")
                        }
                    } else {
                        tilPassword.error = "Error interno del servidor"
                    }
                }
            }
        })
    }

    fun goToHome(view: View) {
        validarCredenciales()
    }
}
