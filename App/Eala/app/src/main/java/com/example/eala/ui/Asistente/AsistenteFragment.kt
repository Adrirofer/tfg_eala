package com.example.eala.ui.Asistente;

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.eala.R
import com.example.eala.ui.Mediciones.MedicionesViewModelCompartido
import java.time.LocalDate

// Esta clase captura las iteracciones del usuario y el flujo de ejecución de la aplicación con la
// pantalla 'Asistente'.

class AsistenteFragment : Fragment() {

    // Declaración de variables que usaremos en el fragmento
    private lateinit var viewModelAsistente: AsistenteViewModel
    private lateinit var viewModelMediciones: MedicionesViewModelCompartido
    private lateinit var btnSeleccionarFecha: Button
    private lateinit var textMedidaSeleccionada: TextView
    private lateinit var inputMensaje: EditText
    private lateinit var botonEnviar: Button
    private lateinit var textoRespuesta: TextView
    private lateinit var scrollView: ScrollView

    // Al crear la vista, inflamos el layout y configuramos los elementos de la interfaz de usuario.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_asistente, container, false)
    }

    // Una vez que la vista está creada, configuramos los listeners y observadores.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializamos las variables con las vistas correspondientes.
        btnSeleccionarFecha = view.findViewById(R.id.btnSeleccionarFecha)
        textMedidaSeleccionada = view.findViewById(R.id.textMedidaSeleccionada)
        inputMensaje = view.findViewById(R.id.inputMensaje)
        botonEnviar = view.findViewById(R.id.btnConsultarAsistente)
        textoRespuesta = view.findViewById(R.id.textRespuestaAsistente)
        scrollView = view.findViewById(R.id.scrollView)

        // Inicializamos los ViewModels. Apuntando a los ViewModels correspondientes.
        viewModelAsistente = ViewModelProvider(this).get(AsistenteViewModel::class.java)
        viewModelMediciones = ViewModelProvider(this).get(MedicionesViewModelCompartido::class.java)

        // Configuramos el listener para el botón de seleccionar fecha.
        btnSeleccionarFecha.setOnClickListener {
            // Lógica para seleccionar fecha
            val hoy = LocalDate.now() // Por defecto, la fecha de hoy al seleccionar.

            // Mostrar el diálogo de selección y cargar fecha. En donde leemos los datos según la misma.
            DatePickerDialog(requireContext(), { _, año, mes, día ->
                val fechaSeleccionada = LocalDate.of(año, mes + 1, día)
                viewModelMediciones.cargarMedicionesDeFecha(fechaSeleccionada)
            }, hoy.year, hoy.monthValue - 1, hoy.dayOfMonth).show()
        }

        // Observamos los cambios en los datos de la medida seleccionada. De tal manera que los
        // actualicemos según cambio.
        viewModelMediciones.dataPoints.observe(viewLifecycleOwner) {
                datapoints -> textMedidaSeleccionada.text = datapoints.toString()

        }

        // Configuramos el listener para el botón de enviar mensaje. Que llamará a la función del
        // modelo para enviar las medidas.
        botonEnviar.setOnClickListener {
            val mensaje = inputMensaje.text.toString()
            viewModelAsistente.enviarMensaje(mensaje + textMedidaSeleccionada.text)
        }

        // Observamos los cambios en la respuesta del asistente. De tal manera que los actualicemos
        // según cambio.
        viewModelAsistente.respuesta.observe(viewLifecycleOwner) { respuesta ->
            textoRespuesta.text = respuesta
        }

        // Configuramos el scroll en la respuesta del asistente. Debido a que la respuesta a veces
        // es extensa.
        scrollView.post{
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }
}