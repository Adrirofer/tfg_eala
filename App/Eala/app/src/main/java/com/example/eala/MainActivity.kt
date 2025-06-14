package com.example.eala

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import com.example.eala.databinding.ActivityMainBinding
import com.example.eala.ui.LogInActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    // Declaración de variables que usaremos en el fragmento
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    // Aquí en el OnCreate revisamos en primer lugar si el usuario está registrado.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Si no hay usuario registrado, nos lleva a la pantalla de login.
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, LogInActivity::class.java)) //<< LogIn
            finish()
            return
        }

        // Inicializamos e inflamos la pantalla
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializamos la barra flotante que aloja el botón de cerrar sesión.
        setSupportActionBar(binding.appBarMain.toolbar)

        // Configuramos la navegación.
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?)!!
        val navController = navHostFragment.navController

        // Configuramos la navegación. En las tres pantallas que manejamos en nuestra app.
        binding.navView?.let {
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_mediciones_dendometro, R.id.nav_mediciones_ta_hdad,
                    R.id.nav_asistente
                ),
                binding.drawerLayout
            )
            // Y la barra que aloja el botón.
            setupActionBarWithNavController(navController, appBarConfiguration)
            it.setupWithNavController(navController)
        }

        // La configuramos igualmente para el resto de pantallas.
        binding.appBarMain.contentMain.bottomNavView?.let {
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_mediciones_dendometro, R.id.nav_mediciones_ta_hdad,
                    R.id.nav_asistente
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            it.setupWithNavController(navController)
        }
    }

    // Esto crea las opciones dentro del menú de navegación.
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        // Using findViewById because NavigationView exists in different layout files
        // between w600dp and w1240dp
        val navView: NavigationView? = findViewById(R.id.nav_view)
        if (navView == null) {
            // The navigation drawer already has the items including the items in the overflow menu
            // We only inflate the overflow menu if the navigation drawer isn't visible
            menuInflater.inflate(R.menu.overflow, menu)
        }
        return result
    }

    // Esto es para el botón de cerrar sesión. Relizando la acción si pulsamos el botón, de redi
    // rigirnos a la pantalla de LogIn.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.btn_cerrar_sesion -> {

                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LogInActivity::class.java))
                finish()
                true
            }

            else -> {super.onOptionsItemSelected(item)}
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}