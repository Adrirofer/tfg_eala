
//<<<<<< Clase que define la lógica de nuestra FirebaseFunction, que se encarga de publicar y gestionar nuestro endpoint >>>>>>

// Importamos los módulos necesarios.
const admin = require("firebase-admin"); // Módulo que nos sirve para gestionar el servicio.
const fetch = require("node-fetch"); // Módulo para hacer la consulta de LogIn antes de enviar los datos
const functions = require("firebase-functions");

// Inicializamos el servicio.
admin.initializeApp();

// Insertamos nuestra key pública para conectar con nuestra app.
const API_KEY = "AIzaSyCjJHv3PpeM-c_0y_he3XvvHbCjyM7TkNU";

// Utilizando la librería exports. Declaramos la función siguiente, llamada 'senData'. Esta función autentifica al usuario
// en primer lugar. Y si esta es exitosa, realiza un post de los datos a nuestra aplicación. Que será insertado a la BBDD.
exports.sendData = functions.https.onRequest(async (req, res) => {
  // Extraemos los datos del body de nuestra petición. Identificando donde alojaremos cada atributo.
  const { email, password, returnSecureToken, sensorId, fecha, hora, humedad, temperatura, variacion } = req.body;

  // Controlamos que no falte ningún atributo. Antes de hacer nada.
  if (!email || !password || returnSecureToken == null ||
      !sensorId || !fecha || !hora || humedad == null || temperatura == null || variacion == null) {
    return res.status(400).send("Falta algún parámetro de la medición"); //Enviamoss 'Bad request' en este caso.
  }

  try {
    // Realizamos la autenticación. Llamando al endpoint publicado en la documentación de Firebase.
    const authRes = await fetch(`https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=${API_KEY}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      // Pasando tres parámetros que irán junto con los demas atributos.
      body: JSON.stringify({ email, password, returnSecureToken })
    });

    // Extraemos el cuerpo de la respuesta. A
    const authData = await authRes.json();

    // Si la autenticación ha sido fallida. Devolvemos error. 'Unauthorised'
    if (!authData.idToken || !authData.localId) {
      return res.status(401).send("Error en la autenticación del usuario");
    }

    // Extraemos el id de autenticación del usuario del body de la respuesta.
    const uid = authData.localId;

    // Llamamos al árbol correcto de la estructura de nuestra BBDD.
    const path = `/usuarios/${uid}/${sensorId}/mediciones/${fecha}/${hora}`;
    
    // Llamamos a la BBDD.
    const ref = admin.database().ref(path);

    // Fijamos nuestros valores en la llamada. Que vienen del cuerpo de nuestra petición.
    await ref.set({ fecha, hora, humedad, temperatura, variacion });

    // Si consigue enviar, devolvemos un '200 OK'
    return res.status(200).send("Datos guardados para el usuario: " + uid);
  } catch (error) {
    // Sino, error.
    console.error("Error interno:", error);
    return res.status(500).send("Error interno del servidor");
  }
});