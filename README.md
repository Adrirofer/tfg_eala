# TFG - Sistema de monitorización para el cultivo del olivar

Este repositorio contiene el desarrollo completo del Trabajo de Fin de Grado, cuyo objetivo es facilitar la gestión hídrica del olivar mediante una solución tecnológica compuesta por:

- Una aplicación móvil Android para la visualización de datos agronómicos.
- Un backend basado en Firebase Functions que actúa como receptor de datos vía API REST.
- Una arquitectura modular que asienta una futura integración con sensores IoT reales.

---

## 📁 Estructura del repositorio

tfg_eala/

  app
  - Eala/ # Proyecto android **# Código de la app móvil**

  backend
  - firebase-functions/ # Proyecto Node.js para Firebase Functions **# Código del endpoint POST**

---

## 📱 Aplicación móvil (`/app/eala`)

Aplicación Android desarrollada en Kotlin usando Android Studio. Permite:

- Consultar datos de sensores higrómetros y dendrómetros (humedad, temperatura, variación). Mediante la visualización de series temporales con gráficos.
- Integración con un asistente conversacional (Gemini AI) para recomendaciones adaptadas.
- Estructura adaptada para datos en tiempo por integración con Firebase Realtime Database.

### 🔧 Tecnologías

- Android Studio (Kotlin)
- Firebase Realtime Database
- Gemini Vertex AI Generative Language v1 - Modelo gemini 2.0 Flash (Kotlin)
- Firebase Functions (JavaScript)

### 📚 Librerías
- MPAndroidChart (Gráficas)
- LiveData (Cambios en tiempo real de los datos)
- Retrofit2 (Generación de llamadas a la API de Gemini)
- Firebase (auth, acceso a la bbdd...)
- Java util, Java Time (Tratamiento de los datos)

---

## 🌐 Backend - Firebase Functions (`/backend/firebase-functions/index.js`)

Contiene una función `POST` que permite enviar datos al sistema desde sensores IoT o simulaciones. Este endpoint representa la capa de entrada de datos al sistema, siguiendo la estructura se pueden enviar datos al visor siempre que el usuario esté autenticado.

### ✉️ Estructura del JSON esperado

```json
{
  "email": "usuario@ejemplo.com",
  "password": "clave",
  "returnSecureToken": true,
  "sensorId": "sensor_001",
  "fecha": "2025-06-13",
  "hora": "15:00",
  "humedad": 56.3,
  "temperatura": 29.7,
  "variacion": 0.03
}
