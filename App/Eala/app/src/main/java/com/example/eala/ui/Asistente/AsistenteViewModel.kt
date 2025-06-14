package com.example.eala.ui.Asistente

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.eala.api.GeminiContent
import com.example.eala.api.GeminiPart
import com.example.eala.api.GeminiRequest
import com.example.eala.api.GeminiResponse
import com.example.eala.api.InterfazGemini.GeminiService

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Clase que contiene la lógica que necesitamos para el asistente. Tanto
// llamadas como conexión con la API.

class AsistenteViewModel : ViewModel() {

    // Configuramos un historial para simular algo de contexto en la conversación. De cara a ayudar
    // al asistente.
    private val historial = mutableListOf< GeminiContent>()

    // Añadimos un texto guía para situar al asistente. Revisar memoria para explicación más extensa.
    init {
        historial.add(GeminiContent(
            role = "user",
            parts = listOf(GeminiPart(text ="" +
                "Eres un asistente agrícola especializado en olivar tradicional de secano en Andalucía.\n" +
                "Interpretas datos de sacados de unos dendrómetros, e higrómetros y das consejos prácticos" +
                "en lenguaje claro como si hablaras con un agricultor. \n" +
                "Conoces el contexto del cultivo y actúa como si ya supieras del\n" +
                "historial del olivar. Las respuestas tienen que ser medianamente concisas.\n" +
                "Las medidas del dendómetro te las voy a pasar de un día concreto, en el siguiente formato:\n" +
                "Datapoint = Es cada tupla fecha,hora, humedad, temperatura, variacion\n" +
                "Date = Es la fecha en la que tomamos la medida\n" +
                "Timestamp = Es la hora a la que tomamos la medida\n" +
                "humedad = Es la humedad del ambiente en esa hora\n" +
                "variation = Es el diámetro en mm del tronco para esa hora. Los dendómetros están bien. \n" +
                "Finaliza diciendo si tendríamos que regar"))))
    }

    // Declaramos una variable que contendrá la respuesta del asistente.
    private val _respuesta = MutableLiveData<String>()
    val respuesta: LiveData<String> = _respuesta

    // Declaramos la función que servirá para hacer la petición a la API del asistente.
    fun enviarMensaje(mensaje: String) {

        // Añadimos el mensaje del usuario al historial. Para alimentar al contexto.
        historial.add(GeminiContent(role = "user", parts = listOf(GeminiPart(text = mensaje))))

        // Declaramos el cuerpo de la petición.
        val body = GeminiRequest(contents = historial)

        // Creamos la conexión con la API.
        val retrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/v1beta/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Abrimos la conexión.
        val service = retrofit.create(GeminiService::class.java)

        // Mandamos la petición y recibimos respuesta.
        service.generateContent("AIzaSyCfX7CvlaM9Qwkuzr2B1I57Mxw_pBBMLII", body).enqueue(object : Callback<GeminiResponse> {
            override fun onResponse(call: Call<GeminiResponse>, response: Response<GeminiResponse>) {
                if (response.isSuccessful) {
                    val texto = response.body()
                        ?.candidates?.firstOrNull()
                        ?.content?.parts?.firstOrNull()
                        ?.text ?: "Respuesta vacía"

                    // Añadimos la respuesta a al historial.
                    historial.add(GeminiContent("model", parts = listOf(GeminiPart(text = texto))))

                    // Mudamos la respuesta a nuestra variable.
                    _respuesta.postValue(texto)
                } else {
                    _respuesta.postValue("Error: ${response.code()}")
                }
            }

            // Capturamos si no es posible respuesta.
            override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
                _respuesta.postValue("Error: ${t.message}")
            }
        })
    }

}