package com.upc.appgestiones

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.upc.appgestiones.ui.home.HomeActivity
import com.upc.appgestiones.ui.login.LoginActivity
import com.upc.appgestiones.ui.home.DrawerHomeActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        solicitarPermisos()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                navegarSegunLogin()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Permiso denegado")
                    .setMessage("La aplicación necesita permisos de ubicación para funcionar correctamente.")
                    .setPositiveButton("Aceptar") { _, _ ->
                        navegarSegunLogin()
                    }
                    .show()
            }
        }
    private fun solicitarPermisos() {
        requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun navegarSegunLogin() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            startActivity(Intent(this, DrawerHomeActivity::class.java)) // Se cambio
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}