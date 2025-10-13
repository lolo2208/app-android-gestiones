package com.upc.appgestiones.ui.operaciones

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.upc.appgestiones.R
import com.upc.appgestiones.core.data.model.Operacion

class DetalleOperacionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_detalle_operacion)

        val operacion = intent.getSerializableExtra("operacion") as? Operacion

        //val txtNombre = findViewById<TextView>(R.id.txtNombre)
        //val txtDireccion = findViewById<TextView>(R.id.txtDireccion)
        //val txtTelefono = findViewById<TextView>(R.id.txtTelefono)
        //val txtEstado = findViewById<TextView>(R.id.txtEstado)

        /*
        operacion?.let {
            txtNombre.text =
                "${it.clienteNavigation.nombres} ${it.clienteNavigation.apellidos}"
            txtDireccion.text =
                "${it.direccionNavigation.calle} ${it.direccionNavigation.numero}, ${it.direccionNavigation.ciudad}"
            txtTelefono.text = "DNI: ${it.clienteNavigation.documento}"
            txtEstado.text = it.estado.name
        }
         */
    }
}