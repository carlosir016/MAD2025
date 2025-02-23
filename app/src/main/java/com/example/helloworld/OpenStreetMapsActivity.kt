package com.example.helloworld

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline


@Suppress("DEPRECATION")
class OpenStreetMapsActivity : AppCompatActivity() {

    val coordinatesMarks = listOf(
        GeoPoint(40.39496359772603, -3.663151487104123), // IES Vallecas I
        GeoPoint(40.39258752846945, -3.6589599484801845), // Rayo's Boxing Gym
        GeoPoint(40.39047239582602, -3.6549692838344363), // Azorin Park
        GeoPoint(40.38975267467859, -3.6456830179722557), // Gala's Drive School
        GeoPoint(40.38777426507525, -3.6397332185383453), // Primary Attention Federica Montseny
        GeoPoint(40.38292912121118, -3.625833448588706), // Mercadona
        GeoPoint(40.390103594659664, -3.6278874301310378) // ETSISI
    )

    val coordinatesNames = listOf(
        "IES Vallecas I",
        "Rayo's Boxing Gym",
        "Azorin Park",
        "Gala Drive School",
        "Primary Attention Federica Montseny",
        "Mercadona",
        "ETSISI"
    )

    private lateinit var map : MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MAP","Welcome to MapActivity")

        enableEdgeToEdge()
        setContentView(R.layout.activity_open_street_maps)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Configuration.getInstance().userAgentValue = "helloWorld"
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osm", MODE_PRIVATE))

        val bundle = intent.getBundleExtra("locationBundle")
        val location: Location? = bundle?.getParcelable("location")

        val startPoint = if(location != null) {
            Log.d("MAP","Location:[${location.altitude}][${location.latitude},${location.longitude}]")
            GeoPoint(location.latitude,location.longitude)
        } else {
            Log.d("MAP","Location is null")
            GeoPoint(40.39593229478562, -3.66441886496911)
        }

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.controller.setZoom(17.0)
        map.controller.setCenter(startPoint)

        val marker = Marker(map)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.icon = ContextCompat.getDrawable(this, android.R.drawable.ic_delete) as BitmapDrawable
        marker.title = "initial point"
        map.overlays.add(marker)

        addMarkers(map, coordinatesMarks, coordinatesNames, this)
    }

    fun addMarkers(map:MapView, coordinates:List<GeoPoint>, placesNames:List<String>, context:Context) {
        val polyline = Polyline()
        polyline.setPoints(coordinates)

        for(i in coordinates.indices) {
            val marker = Marker(map)
            marker.position = coordinates[i]
            marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM)
            marker.icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_compass) as BitmapDrawable
            marker.title =placesNames[i]
            map.overlays.add(marker)
        }
        map.overlays.add(polyline)
    }


}