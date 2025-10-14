package com.upc.appgestiones.core.sqlite.dao

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.example.upcmov.DAO.DbHelper
import com.example.upcmov.Util.Tools
import com.upc.appgestiones.core.sqlite.model.Respaldo

class RespaldoDAO(myContext: Context) {
    private var dbHelper: DbHelper = DbHelper(myContext)

    fun insertar(idOperacion: Int, campo: String, path: String): Long {
        Log.i(Tools.Companion.LOGTAG, "Inicio del metodo Insertar")
        val indice: Long
        val values = ContentValues().apply {
            put("idOperacion", idOperacion)
            put("campo", campo)
            put("path", path)
        }
        val db = dbHelper.writableDatabase
        try {
            indice = db.insert(Tools.Companion.MITABLA, null, values)
            return indice
        } catch (e: Exception) {
            throw DAOException("RespaldoDAO: Error al insertar - " + e.message)
        } finally {
            db.close()
        }
    }

    @SuppressLint("Range")
    fun obtener(): Respaldo {
        Log.i(Tools.Companion.LOGTAG, "Ingresó al método obtener()")
        val db = dbHelper.readableDatabase
        val modelo = Respaldo()
        try {
            val c: Cursor = db.rawQuery("select id, idOperacion, campo, path from " + Tools.Companion.MITABLA, null)
            if (c.count > 0) {
                c.moveToFirst()
                do {
                    val id: Int = c.getInt(c.getColumnIndex("id"))
                    val idOperacion: Int = c.getInt(c.getColumnIndex("idOperacion"))
                    val campo: String = c.getString(c.getColumnIndex("campo"))
                    val path: String = c.getString(c.getColumnIndex("path"))
                    modelo.id = id
                    modelo.idOperacion = idOperacion
                    modelo.campo = campo
                    modelo.path = path
                } while (c.moveToNext())
            }
            c.close()
        } catch (e: Exception) {
            throw DAOException("RespaldoDAO: Error al obtener: " + e.message)
        } finally {
            db.close()
        }
        return modelo
    }

    fun eliminar(id: Int) {
        Log.i(Tools.Companion.LOGTAG, "Ingresó al método eliminar()")
        val db = dbHelper.writableDatabase
        try {
            val args = arrayOf(id.toString())
            db.execSQL("DELETE FROM " + Tools.Companion.MITABLA + " WHERE id=?", args)
        } catch (e: Exception) {
            throw DAOException("RespaldoDAO: Error al eliminar: " + e.message)
        } finally {
            db.close()
        }
    }
}