package com.example.eala.ui.Mediciones


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.LocalTime

import java.time.format.DateTimeFormatter

/*
Esta clase define las funciones de las mediciones de nuestra aplicación. De manera que varias pan
tallas puedan acceder a ellas.

Contiene:
    - Variables para usar en la clase.
    - Función que lee los valores.

*/
class MedicionesViewModelCompartido : ViewModel() {

    // <<< Declaramos las variables que necesitaremos para cargar las mediciones.>>>

    // Declaramos la conexión a nuestra BBDD facilitando la URL.
    private val database =
        FirebaseDatabase.getInstance("https://eala-8869e-default-rtdb.europe-west1.firebasedatabase.app")

    // Declaramos la lista de DataPoints. Es decir, una lista de cada medida que tomamos. Y además
    // esa lista la pasamos a LiveData para que podamos observar la lista en tiempo real.
    private val _dataPoints = MutableLiveData<List<DataPoint>>()
    val dataPoints: LiveData<List<DataPoint>> = _dataPoints

    // Declaramos un formatter para formatear las horas.
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    // Esta función nos permite cargar las mediciones de una fecha concreta.
    // Desde nuestra bbdd en firebase.
    fun cargarMedicionesDeFecha(fecha: LocalDate) {

        // Conectamos con nuestra BBDD y nuestro usuario previamente logueado.
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        // Apuntamos al nodo correcto del usuario. Dentro de nuestra BBDD.
        val ref = database.getReference("usuarios/$uid")

        // Formateamos la fecha para que sea compatible con la BBDD. Y poder buscar.
        val fechaString = fecha.format(DateTimeFormatter.ISO_DATE)

        // A través de un listener, estamos atentos a cambios en la BBDD.
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { // Si ocurre el evento, ejecutamos.

                // Declaramos un Map en donde contendremos las mediciones por hora.
                // Independientemente de los sensores. Ya que realizaremos una ponderación de las
                // mediciones de cada sensor pero por hora. El map sigue la siguiente estructura
                // Map<Hora, Lista<TriTupla<Hora, Humedad, Temp, Var>>>.

                // Se realiza aquí el cálculo de la media ponderada para que todos los fragment
                // y la IA puedan beber de esta función. Y mantener aquí la lógica en el ViewModel.
                val medicionesPorHora: MutableMap<String,
                        MutableList<Triple<Double, Double, Double>>> = mutableMapOf()

                // Por cada sensor dentro de cada usuario, es decir, los sensores que el usuario
                // tenga en su explotación.
                for (sensor in snapshot.children) {

                    // Entramos en las mediciones del día concreto que queramos.
                    val medicionesDia = sensor.child("mediciones").child(fechaString)

                    // Y por cada hora dentro de ese día, obtenemos las mediciones. Y guardamos en
                    // el map
                    for (medicionHora in medicionesDia.children) {
                        val hora = medicionHora.key ?: continue
                        val humedad = medicionHora.child("humedad")
                            .getValue(Double::class.java) ?: continue
                        val temperatura = medicionHora.child("temperatura")
                            .getValue(Double::class.java) ?: continue
                        val variacion = medicionHora.child("variacion")
                            .getValue(Double::class.java) ?: continue

                        // Guardamos las mediciones en el map. Añadiendo hora nueva o actualizando.
                        val listaHora = medicionesPorHora.getOrPut(hora) { mutableListOf() }
                        listaHora.add(Triple(humedad, temperatura, variacion))
                    }
                }
                // Creamos la variable interna de datapoints para devolver.
                val listaDataPoints = mutableListOf<DataPoint>()

                // Y hacemos la media ponderada de cada hora dentro de las mediciones que hemos
                // recogido en el map.
                for ((hora, valores) in medicionesPorHora) { // Por cada hora y sus 3 valores...
                    try {
                        // Realizamos la media y formateamos la hora.
                        val marcaTiempo = LocalTime.parse(hora, formatter)
                        val mediaHumedad = valores.map { it.first }.average()
                        val mediaTemperatura = valores.map { it.second }.average()
                        val mediaVariacion = valores.map { it.third }.average()

                        // Añadimos a DataPoints.
                        listaDataPoints.add(
                            DataPoint(fecha, marcaTiempo, mediaHumedad, mediaTemperatura,
                                mediaVariacion)
                        )
                    } catch (e: Exception) {
                        Log.e("ParseHora", "Hora inválida: $hora", e)
                    }
                }
                // Y devolvemos. A DataPoint en tiempo real, dentro de LiveData.
                _dataPoints.value = listaDataPoints
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al leer datos", error.toException())
            }
        })
    }
}