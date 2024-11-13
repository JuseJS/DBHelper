package org.iesharia.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.iesharia.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    MainScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier) {
    val context = LocalContext.current
    val db = DBHelper(context)

    var nameValue by remember { mutableStateOf("") }
    var ageValue by remember { mutableStateOf("") }
    var personas by remember { mutableStateOf(db.getAllPersonas()) }
    var isEditMode by remember { mutableStateOf(false) }
    var selectedPersonaId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .padding(10.dp)
            .wrapContentWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Base de Datos",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
        Text(
            text = "Muuuuuy simple\nNombre/Edad",
            fontSize = 10.sp
        )
        // Nombre
        OutlinedTextField(
            value = nameValue,
            onValueChange = {
                nameValue = it
            },
            modifier = Modifier,
            textStyle = TextStyle(color = Color.DarkGray),
            label = { Text(text = "Nombre") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )
        // Edad
        OutlinedTextField(
            value = ageValue,
            onValueChange = {
                ageValue = it
            },
            modifier = Modifier,
            textStyle = TextStyle(color = Color.DarkGray),
            label = { Text(text = "Edad") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )
        val bModifier: Modifier = Modifier.padding(10.dp)
        Row {
            Button(
                modifier = bModifier,
                onClick = {
                    val name = nameValue
                    val age = ageValue.toIntOrNull()

                    if (name.isNotEmpty() && age != null) {
                        if (isEditMode && selectedPersonaId != null) {
                            db.updatePersona(
                                Persona(
                                    id = selectedPersonaId!!,
                                    nombre = name,
                                    edad = age
                                )
                            )
                            Toast.makeText(
                                context,
                                "$name actualizado en la base de datos",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            db.addPersona(Persona(nombre = name, edad = age))
                            Toast.makeText(
                                context,
                                "$name adjuntado a la base de datos",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        personas = db.getAllPersonas()
                        nameValue = ""
                        ageValue = ""
                        isEditMode = false
                        selectedPersonaId = null
                    } else {
                        Toast.makeText(
                            context,
                            "Por favor, ingrese un nombre y una edad válida",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            ) {
                Text(text = if (isEditMode) "Actualizar" else "Añadir")
            }
            if (isEditMode) {
                Button(
                    modifier = bModifier,
                    onClick = {
                        if (selectedPersonaId != null) {
                            val personaToDelete = personas.find { it.id == selectedPersonaId }
                            if (personaToDelete != null) {
                                db.delPersona(personaToDelete)
                                personas = db.getAllPersonas()
                                Toast.makeText(context, "Persona eliminada", Toast.LENGTH_SHORT)
                                    .show()
                                nameValue = ""
                                ageValue = ""
                                isEditMode = false
                                selectedPersonaId = null
                            }
                        }
                    }
                ) {
                    Text(text = "Eliminar")
                }
                Button(
                    modifier = bModifier,
                    onClick = {
                        nameValue = ""
                        ageValue = ""
                        isEditMode = false
                        selectedPersonaId = null
                    }
                ) {
                    Text(text = "Cancelar")
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .padding(10.dp)
        ) {
            // Fila de encabezado
            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Nombre",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Edad",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            items(personas) { persona ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {
                            nameValue = persona.nombre
                            ageValue = persona.edad.toString()
                            selectedPersonaId = persona.id
                            isEditMode = true
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = persona.nombre,
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${persona.edad} años",
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
