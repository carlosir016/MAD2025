package com.example.helloworld

import android.app.Activity
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
import android.widget.Toast
import android.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter


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

    private val fileName = "coordinates.csv"

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

        saveCoordinates()

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

        readFile()
        onLocationChanged()
    }

    fun addMarkers(map:MapView, coordinate:GeoPoint, placeName:String, context:Context) {
        val marker = Marker(map)
        marker.position = coordinate
        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM)
        marker.icon = ContextCompat.getDrawable(context,android.R.drawable.ic_menu_compass) as BitmapDrawable
        marker.title = placeName
        map.overlays.add(marker)

    }

    fun trackRute(){
        val polyline = Polyline()
        polyline.setPoints(coordinatesMarks)
        map.overlays.add(polyline)

    }

    private fun findFile():Boolean{
        val fileList = fileList()
        var exists = false
        fileList.forEach {
            if(fileName == it)
                exists = true
        }
        return exists
    }

    fun saveCoordinates (){
        var content = ""
        for ((coordNames,coordinates) in coordinatesNames.zip(coordinatesMarks))
            content += "${coordNames},${coordinates.latitude},${coordinates.longitude}\n"
        Log.d("FILE",content)
        writeFile(content)
    }

    fun writeFile(content: String) {
        val file = OutputStreamWriter(openFileOutput(fileName, Activity.MODE_PRIVATE))
        file.write(content)
        file.flush()
        file.close()
    }

     fun readFile(){
        if(!findFile()) {
            Log.d("FILE","file do not exists")
            return
        }
        val file = InputStreamReader(openFileInput(fileName))
        val br = BufferedReader(file)
        var line = br.readLine()
        Log.d("FILE","file opened")
        while(line != null){
            Log.d("FILE",line)
            val (name,latitude,longitude) = line.split(",").map{it.trim()}
            addMarkers(map,GeoPoint(latitude.toDouble(),longitude.toDouble()),name,this)
            line = br.readLine()
        }
         trackRute()
    }

    fun onLocationChanged() {
        val toastText = "MENSAJE DE BIENVENIDA"
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
    }


}