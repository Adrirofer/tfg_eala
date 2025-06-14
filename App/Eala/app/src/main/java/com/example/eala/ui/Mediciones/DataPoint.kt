package com.example.eala.ui.Mediciones

import java.time.LocalDate
import java.time.LocalTime

// Clase que hemos creado para simular la estructura de nuestros datos.
data class DataPoint(val date: LocalDate, val timestamp: LocalTime, val humidity: Double,
    val temperature: Double, val variation: Double)