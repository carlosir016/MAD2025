package com.example.helloworld

import android.Manifest

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.BufferedReader
import java.io.InputStreamReader

class ThirdActivity : AppCompatActivity(), LocationListener{
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var lastLocation : Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("THIRD", "Welcome to the third activity")

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION),
                locationPermissionCode)
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_third)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val secondActivityButton: Button = findViewById(R.id.secondButton)
        secondActivityButton.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        val mapButtonActivity: Button = findViewById(R.id.map)
        mapButtonActivity.setOnClickListener {
            if(lastLocation != null) {
                val intent = Intent(this, OpenStreetMapsActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("location",null)
                intent.putExtra("locationBundle",bundle)
                startActivity(intent)
            } else
                Log.e("THIRD","Waiting...")
        }
    }



    override fun onLocationChanged(location: Location) {
        lastLocation = location
        val textView: TextView = findViewById(R.id.mainTextView)
        textView.text = "Latitude: ${location.latitude}\n Longitude: ${location.longitude}"
    }

}