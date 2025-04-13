package com.example.helloworld

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class SecondActivity : AppCompatActivity(), LocationListener {

    private val TAG = "SecondActivity"
    private lateinit var locationManager: LocationManager
    private lateinit var weatherTextView: TextView
    private val locationPermissionCode = 2
    private val API_KEY = "953c5c7cb82273bcb415b9e06e8415e2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Log.d(TAG, "Welcome to the second Activity")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        weatherTextView = findViewById(R.id.weatherTextView)

        requestLocationUpdates()

        val mainActivityButton: Button = findViewById(R.id.close)
        mainActivityButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    true
                }

                R.id.navigation_map -> {
                    startActivity(Intent(this, OpenStreetMapsActivity::class.java))
                    true
                }

                R.id.navigation_post -> {
                    startActivity(Intent(this, PostsActivity::class.java))
                    true
                }

                R.id.navigation_list -> {
                    startActivity(Intent(this, ThirdActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                locationPermissionCode
            )
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1f, this)
            Log.d(TAG, "Iniciando actualizaciones de ubicación en tiempo real...")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onLocationChanged(location: Location) {
        Log.d(TAG, "Nueva ubicación: lat=${location.latitude}, lon=${location.longitude}")
        val locationTextView: TextView = findViewById(R.id.mainTextView)
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        val cityName = addresses?.firstOrNull()?.locality ?: "Ubicación desconocida"

        val locationText = "📍 $cityName"
        locationTextView.text = locationText

        getWeatherForecast(location.latitude, location.longitude)
    }

    private fun getWeatherForecast(lat: Double, lon: Double) {
        Log.d(TAG, "Obteniendo clima para lat: $lat, lon: $lon")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherApiService::class.java)
        val call = service.getWeatherForecast(lat, lon, 1, API_KEY)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    weatherResponse?.let { showWeatherInfo(it) }
                } else {
                    Log.e(TAG, "Error en la respuesta: ${response.code()}")
                    Toast.makeText(this@SecondActivity, "Error al obtener el clima", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e(TAG, "Error en la petición: ${t.message}")
                Toast.makeText(this@SecondActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun showWeatherInfo(weatherResponse: WeatherResponse) {
        val item = weatherResponse.list.firstOrNull()
        if (item != null) {
            val tempCelsius = item.main.temp - 273.15
            val tempFormatted = String.format("%.1f", tempCelsius)
            val weatherText = """
                🌡 Temp: $tempFormatted°C
                🌬 Viento: ${item.weather[0].description}
                💧 Humedad: ${item.main.humidity}%
            """.trimIndent()

            weatherTextView.text = weatherText
            Log.d(TAG, "Clima actualizado: $weatherText")
        }
    }
}
