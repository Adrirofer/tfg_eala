package com.example.eala.ui.Mediciones

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import androidx.fragment.app.Fragment

import androidx.lifecycle.ViewModelProvider

import com.example.eala.R
import com.example.eala.databinding.FragmentHftBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

import java.time.LocalDate
import java.time.format.DateTimeFormatter


// Esta clase captura las iteracciones del usuario y el flujo de ejecución de la aplicación con la
// pantalla de las mediciones del higrómetro.

class MedicionesHTFFragment : Fragment() {

    // Creamos una variable para el binding. Por si al entrar en la vista no se crea. Y manejar mejor
    // la memoria.
    private var _binding: FragmentHftBinding? = null
    private val binding get() = _binding!!

    // Declaramos las variables que usaremos en el fragmento.
    private lateinit var chartView: LineChart
    private lateinit var fechaButton: Button

    private lateinit var viewModelMediciones: MedicionesViewModelCompartido

    // Al crear la vista, inflamos el layout y configuramos los elementos de la interfaz de usuario.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflamos el lyout
        _binding = FragmentHftBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Una vez que la vista está creada, configuramos los listeners y observadores.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Inicializamos las variables con las vistas correspondientes.
        fechaButton = view.findViewById(R.id.fechaButton)
        chartView = view.findViewById(R.id.chartView)

        // Inicializamos los ViewModels. Apuntando a los ViewModels correspondientes.
        viewModelMediciones =
            ViewModelProvider(this).get(MedicionesViewModelCompartido::class.java)

        // Cargar datos de hoy al iniciar
        val hoy = LocalDate.now()
        viewModelMediciones.cargarMedicionesDeFecha(hoy)

        // Configuramos el listener para el botón de seleccionar fecha.
        fechaButton.setOnClickListener {
            // Lógica para seleccionar fecha
            val hoy = LocalDate.now() // Por defecto, la fecha de hoy al seleccionar.

            // Mostrar el diálogo de selección y cargar fecha. En donde leemos los datos según la misma.
            // A través de la función cargarMedicionesDeFecha en nuestro viewmodel.
            DatePickerDialog(requireContext(), { _, año, mes, día ->
                val fechaSeleccionada = LocalDate.of(año, mes + 1, día)
                viewModelMediciones.cargarMedicionesDeFecha(fechaSeleccionada)
            }, hoy.year, hoy.monthValue - 1, hoy.dayOfMonth).show()
        }

        // Observamos los cambios en los datos de la medida seleccionada. De tal manera que los
        // actualicemos según cambio la gráfica de dendómetros.
        viewModelMediciones.dataPoints.observe(viewLifecycleOwner) { datos ->
            setDataToHTFChart(datos)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Función para pintar los datos en la gráfica.
    fun setDataToHTFChart(dataPoints: List<DataPoint>) {
        val entries_hdad = mutableListOf<Entry>() // Aquí alojamos las mediciones de Hdad.
        val entries_Ta = mutableListOf<Entry>() // Aquí alojamos las mediciones de Tª.
        val labels = mutableListOf<String>() // Aquí alojamos las horas.
        val formatter = DateTimeFormatter.ofPattern("HH:mm")  // Para formatear las horas.

        // Por cada elemento datapoint (medida nuestra) "dp". Añadimos a las listas.
        dataPoints.forEachIndexed { index, dp ->
            labels.add(dp.timestamp.format(formatter))

            entries_hdad.add(Entry(index.toFloat(), dp.humidity.toFloat(), dp.temperature.toFloat()))
            entries_Ta.add(Entry(index.toFloat(), dp.temperature.toFloat()))
        }

        // En este caso tenemos dos líneas en la gráfica. Cada una con un color. Por tanto tenemos
        // dos dataSets.

        // Humedad.
        val dataSet_Hdad = LineDataSet(entries_hdad, "Humedad (%)").apply {
            color = resources.getColor(R.color.azul_fuerte, null)
            setDrawCircles(false)
            lineWidth = 2f
        }

        // Temperatura.
        val dataSet_Ta = LineDataSet(entries_Ta, "Temperatura (ºC)").apply {
            color = resources.getColor(R.color.black, null)
            setDrawCircles(false)
            lineWidth = 2f
        }

        // Cargamos el dataset a la gráfica. Fusionando primero ambos.
        val lineData = LineData(dataSet_Hdad, dataSet_Ta)
        chartView.data = lineData

        //Configuramos ejes. Y apariencia.
        chartView.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(labels)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            labelRotationAngle = -45f
            setDrawGridLines(false)
        }

        chartView.axisLeft.setDrawGridLines(false)
        chartView.axisRight.isEnabled = false
        chartView.legend.isEnabled = false
        chartView.invalidate()
    }
}