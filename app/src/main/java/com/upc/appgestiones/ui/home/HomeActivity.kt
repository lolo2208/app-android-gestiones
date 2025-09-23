package com.upc.appgestiones.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.Cliente
import com.upc.appgestiones.core.data.model.Direccion
import com.upc.appgestiones.core.data.model.Operacion
import com.upc.appgestiones.core.data.model.TipoOperacion
import com.upc.appgestiones.ui.cartera.CarteraFragment
import com.upc.appgestiones.ui.lista_completa.ListaCompletaFragment
import com.upc.appgestiones.ui.map.MapFragment
import com.upc.appgestiones.ui.map.MapViewModel
import com.upc.appgestiones.ui.operaciones.OperacionesFragment

class HomeActivity : AppCompatActivity() {

    private val mapViewModel: MapViewModel by viewModels()
    private val bienvenidaViewModel: BienvenidaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Cargar operaciones
        val lista = Operacion.fetchOperaciones()
        mapViewModel.setOperaciones(lista)
        bienvenidaViewModel.setOperaciones(lista)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        if (savedInstanceState == null) {
            loadFragment(BienvenidaFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> loadFragment(BienvenidaFragment())
                R.id.map -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, MapFragment())
                        .commit()
                }
                R.id.done -> loadFragment(ListaCompletaFragment())
                R.id.tasks -> loadFragment(CarteraFragment())
                R.id.listado -> loadFragment(OperacionesFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }
}
