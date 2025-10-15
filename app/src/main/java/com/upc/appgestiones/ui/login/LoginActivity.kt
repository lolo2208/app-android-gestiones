package com.upc.appgestiones.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.Usuario
import com.upc.appgestiones.core.data.repository.UsuarioRepository
import com.upc.appgestiones.ui.home.HomeActivity
import com.google.gson.Gson
import usuarioToJson

class LoginActivity : AppCompatActivity() {

    private lateinit var tilUsuario: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var edtUsuario: TextInputEditText
    private lateinit var edtPassword: TextInputEditText
    private lateinit var btnAcceder: Button

    private lateinit var usuarioRepository: UsuarioRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        usuarioRepository = UsuarioRepository(this)

        tilUsuario = findViewById(R.id.tilUsuario)
        tilPassword = findViewById(R.id.tilPassword)
        edtUsuario = findViewById(R.id.edtUsuario)
        edtPassword = findViewById(R.id.edtPassword)
        btnAcceder = findViewById(R.id.btnAcceder)

        btnAcceder.isEnabled = false

        // 🔹 Valida si ambos campos tienen texto
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

        usuarioRepository.login(user, pass) { usuario, error ->
            runOnUiThread {
                if (error != null) {
                    tilPassword.error = error.message
                } else if (usuario != null) {
                    guardarUsuarioEnPreferencias(usuario)
                    irAlHome()
                } else {
                    tilPassword.error = "Credenciales inválidas"
                }
            }
        }
    }

    private fun guardarUsuarioEnPreferencias(usuario: Usuario) {
        val prefs = getSharedPreferences("AppGestionesPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val jsonUsuario = usuarioToJson(usuario)
        editor.putString("usuario", jsonUsuario.toString())
        editor.apply()
    }

    private fun irAlHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
