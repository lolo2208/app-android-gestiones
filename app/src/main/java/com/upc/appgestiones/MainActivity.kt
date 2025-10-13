package com.upc.appgestiones

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.upc.appgestiones.ui.login.LoginActivity
import com.upc.appgestiones.ui.home.DrawerHomeActivity

class MainActivity : AppCompatActivity() {

    private val permisosRequeridos = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.RECORD_AUDIO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Ajuste de padding por barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        solicitarPermisos()
    }

    private val requestMultiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permisos ->
            window.decorView.postDelayed({
                val permisosVerificados = permisosRequeridos.all {
                    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
                }

                if (permisosVerificados) {
                    navegarSegunLogin()
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Permisos denegados")
                        .setMessage("La aplicación necesita permisos de ubicación y micrófono para funcionar correctamente.")
                        .setPositiveButton("Aceptar") { _, _ ->
                            navegarSegunLogin()
                        }
                        .show()
                }
            }, 300)
        }

    private fun solicitarPermisos() {
        val permisosNoConcedidos = permisosRequeridos.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permisosNoConcedidos.isEmpty()) {
            navegarSegunLogin()
        } else {
            requestMultiplePermissionsLauncher.launch(permisosNoConcedidos.toTypedArray())
        }
    }

    private fun navegarSegunLogin() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            startActivity(Intent(this, DrawerHomeActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}
