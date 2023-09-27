package com.example.appsemana6

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MenuActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var coordinatesTextView: TextView
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0

    private val AUTOCOMPLETE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "") // DEJA ACÁ TU API DE GOOGLE PARA ABIR EL MAPA
        }

        val searchAddressButton: Button = findViewById(R.id.searchAddressButton)
        searchAddressButton.setOnClickListener {
            startPlacePicker()
        }

        mAuth = FirebaseAuth.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        coordinatesTextView = findViewById(R.id.coordinatesTextView)

        // Botón para restablecer la ubicación
        val resetLocationButton: Button = findViewById(R.id.resetLocationButton)
        resetLocationButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fetchLocation()
            } else {
                coordinatesTextView.text = "Permiso de ubicación denegado"
            }
        }

        // Obtener ubicación actual
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchLocation()
        } else {
            coordinatesTextView.text = "Permiso de ubicación denegado"
        }

        val calculateButton: Button = findViewById(R.id.calculateButton)
        calculateButton.setOnClickListener {
            val userValue = findViewById<EditText>(R.id.amountEditText).text.toString().toDoubleOrNull()
            userValue?.let {
                val distance = haversineDistance(currentLatitude, currentLongitude, -33.4372, -70.6506)
                val shippingCost = calculateShippingCost(it, distance)

                val formattedShippingCost = String.format("$%,.0f", shippingCost) // Formatea el valor a pesos y sin decimales
                findViewById<TextView>(R.id.shippingCostTextView).text = "Costo de envío: $formattedShippingCost"

                val totalCost = it + shippingCost
                val formattedTotalCost = String.format("$%,.0f", totalCost) // Formatea el valor a pesos y sin decimales
                findViewById<TextView>(R.id.totalCostTextView).text = "Costo total: $formattedTotalCost"
            }
        }

        val logoutButton: Button = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }


    private fun startPlacePicker() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                currentLatitude = place.latLng?.latitude ?: 0.0
                currentLongitude = place.latLng?.longitude ?: 0.0
                coordinatesTextView.text = "Coordenadas: Lat: $currentLatitude, Long: $currentLongitude"

                // Recalculate the shipping and total costs
                val userValue = findViewById<EditText>(R.id.amountEditText).text.toString().toDoubleOrNull()
                userValue?.let {
                    val distance = haversineDistance(currentLatitude, currentLongitude, -33.4372, -70.6506)
                    val shippingCost = calculateShippingCost(it, distance)

                    findViewById<TextView>(R.id.shippingCostTextView).text = "Costo de envío: $$shippingCost"
                    findViewById<TextView>(R.id.totalCostTextView).text = "Costo total: $${it + shippingCost}"
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
                // Handle the error here, if needed.
            }
        }
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    private fun calculateShippingCost(total: Double, distance: Double): Double {
        return when {
            total >= 50000 && distance <= 20 -> 0.0
            total in 25000.0..49999.0 -> 150 * distance
            else -> 300 * distance
        }
    }

    private fun fetchLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLatitude = it.latitude
                    currentLongitude = it.longitude
                    coordinatesTextView.text = "Coordenadas: Lat: $currentLatitude, Long: $currentLongitude"
                } ?: run {
                    coordinatesTextView.text = "Ubicación no disponible"
                }
            }
    }
}
