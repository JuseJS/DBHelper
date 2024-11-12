package org.iesharia.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory? = null) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE $TABLE_NAME ("
                + "$ID_COL INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$NAME_COL TEXT, "
                + "$AGE_COL INTEGER)")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Añadir persona
    fun addPersona(persona: Persona) {
        val values = ContentValues().apply {
            put(NAME_COL, persona.nombre)
            put(AGE_COL, persona.edad)
        }
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    // Actualizar persona
    fun updatePersona(persona: Persona): Int {
        val values = ContentValues().apply {
            put(NAME_COL, persona.nombre)
            put(AGE_COL, persona.edad)
        }
        val db = this.writableDatabase
        val updatedRows =
            db.update(TABLE_NAME, values, "$ID_COL = ?", arrayOf(persona.id.toString()))
        db.close()
        return updatedRows
    }

    // Eliminar persona
    fun delPersona(persona: Persona): Int {
        val db = this.writableDatabase
        val deletedRows = db.delete(TABLE_NAME, "$ID_COL = ?", arrayOf(persona.id.toString()))
        db.close()
        return deletedRows
    }

    // Obtener todos los registros de personas
    fun getAllPersonas(): List<Persona> {
        val personas = mutableListOf<Persona>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(ID_COL))
                val nombre = getString(getColumnIndexOrThrow(NAME_COL))
                val edad = getInt(getColumnIndexOrThrow(AGE_COL))
                personas.add(Persona(id, nombre, edad))
            }
            close()
        }
        return personas
    }

    companion object {
        private const val DATABASE_NAME = "nombres"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "name_table"
        const val ID_COL = "id"
        const val NAME_COL = "nombre"
        const val AGE_COL = "edad"
    }
}
