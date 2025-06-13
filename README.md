# TFG - Sistema de monitorización para el cultivo del olivar

Este repositorio contiene el desarrollo completo del Trabajo de Fin de Grado, cuyo objetivo es facilitar la gestión hídrica del olivar mediante una solución tecnológica compuesta por:

- Una aplicación móvil Android para la visualización de datos agronómicos.
- Un backend basado en Firebase Functions que actúa como receptor de datos vía API REST.
- Una arquitectura modular que asienta una futura integración con sensores IoT reales.

---

## 📁 Estructura del repositorio

TFG_App_Back_Real/
├── app/
│ └── eala/ # Proyecto Android Studio
│ └── ... # Código fuente de la app móvil
│
├── backend/
│ └── firebase-functions/ # Proyecto Node.js para Firebase Functions
│ └── ... # Código del endpoint POST

---

## 📱 Aplicación móvil (`/app/eala`)

Aplicación Android desarrollada en Kotlin usando Android Studio. Permite:

- Consultar datos de sensores agrícolas (humedad, temperatura, variación del dendrómetro).
- Visualizar series temporales con gráficos interactivos.
- Integración con un asistente conversacional (Gemini AI) para recomendaciones adaptadas.
- Estructura preparada para integración con Firebase Realtime Database.

### 🔧 Tecnologías

- Android Studio (Kotlin)
- Firebase Realtime Database
- Gemini AI
- MPAndroidChart

---

## 🌐 Backend - Firebase Functions (`/backend/firebase-functions`)

Contiene una función `POST` que permite enviar datos al sistema desde sensores IoT o simulaciones. Este endpoint representa la capa de entrada de datos al sistema, y su estructura garantiza la separación entre la lógica IoT y el visor.

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
