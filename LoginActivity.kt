package com.example.appsemana6


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    // Declaración de Firebase Auth
    private lateinit var mAuth: FirebaseAuth

    // Declaración de las vistas
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerRedirectButton: Button

    // Código de solicitud de permiso de ubicación
    private val REQUEST_LOCATION_PERMISSION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicialización de Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Vinculación de las vistas
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerRedirectButton = findViewById(R.id.registerRedirectButton)

        // Asignación de escuchadores
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                signIn(email, password)
            } else {
                Toast.makeText(this, "Por Favor ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        registerRedirectButton.setOnClickListener {
            // Redirigir al usuario a la pantalla de registro
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Solicita el permiso de ubicación al crear la actividad
        requestLocationPermission()
    }

    private fun signIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso, navega al MenuActivity
                    startActivity(Intent(this, MenuActivity::class.java))
                    finish()
                } else {
                    // Si el inicio de sesión falla, muestra un mensaje al usuario.
                    Toast.makeText(this, "Error de Ingreso", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                // Aquí puedes verificar si el permiso fue concedido o no.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso de ubicación concedido", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
                }
                // Tras comprobar el permiso, verificamos si el usuario ya está autenticado
                val currentUser = mAuth.currentUser
                if (currentUser != null) {
                    // Usuario ya autenticado, navega al MenuActivity
                    startActivity(Intent(this, MenuActivity::class.java))
                    finish()
                }
            }
        }
    }
}
