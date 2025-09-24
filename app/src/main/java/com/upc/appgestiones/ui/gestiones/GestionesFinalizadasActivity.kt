package com.upc.appgestiones.ui.gestiones

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.Gestion

class GestionesFinalizadasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GestionesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_realizada)


        recyclerView.layoutManager = LinearLayoutManager(this)

        // 🔹 Cargar lista de gestiones finalizadas
        val gestiones = Gestion.fetchGestionesFinalizadas()

        // 🔹 Configurar adapter con clic para abrir detalle
        adapter = GestionesAdapter(gestiones) { gestion ->
            val intent = Intent(this, DetalleGestionActivity::class.java)
            intent.putExtra("gestion", gestion)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
    }
}
