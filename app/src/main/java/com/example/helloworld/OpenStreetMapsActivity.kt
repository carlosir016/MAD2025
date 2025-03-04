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
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter


@Suppress("DEPRECATION")
class OpenStreetMapsActivity : AppCompatActivity() {

    val coordinatesMarks = mutableListOf<GeoPoint>()

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

        Configuration.getInstance().userAgentValue = "helloWorld"
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osm", MODE_PRIVATE))

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.controller.setZoom(17.0)
        map.controller.setCenter(GeoPoint(40.39593229478562, -3.66441886496911))

        readFile()

        onLocationChanged()

        val addMarkerButton: Button = findViewById(R.id.addMarkerButton)
        addMarkerButton.setOnClickListener {
            showAddMarkerDialog()
        }
    }

    private fun showAddMarkerDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Añadir nuevo marcador")

        val layout = layoutInflater.inflate(R.layout.dialog_add_marker, null)
        val nameInput = layout.findViewById<EditText>(R.id.markerNameInput)
        val latInput = layout.findViewById<EditText>(R.id.markerLatInput)
        val lonInput = layout.findViewById<EditText>(R.id.markerLonInput)
        latInput.inputType =InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or
                InputType.TYPE_NUMBER_FLAG_SIGNED
        lonInput.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or
                InputType.TYPE_NUMBER_FLAG_SIGNED
        builder.setView(layout)
        builder.setPositiveButton("Añadir") { _, _ ->
            val name = nameInput.text.toString()
            val lat = latInput.text.toString().toDoubleOrNull()
            val lon = lonInput.text.toString().toDoubleOrNull()

            if (name.isNotBlank() && lat != null && lon != null) {
                val newPoint = GeoPoint(lat, lon)
                addMarker(map, newPoint, name, this)
                coordinatesMarks.add(newPoint)
                saveNewCoordinate(name, lat, lon)
            } else {
                Toast.makeText(this, "Datos inválidos", Toast.LENGTH_LONG).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun saveNewCoordinate(name: String, lat: Double, lon: Double) {
        val content = "$name,$lat,$lon\n"
        val file = OutputStreamWriter(openFileOutput(fileName, Activity.MODE_APPEND))
        file.write(content)
        file.flush()
        file.close()
    }

    fun addMarker(map:MapView, coordinate:GeoPoint, placeName:String, context:Context) {
        val marker = Marker(map)
        marker.position = coordinate
        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM)
        marker.icon = ContextCompat.getDrawable(context,android.R.drawable.ic_menu_compass) as BitmapDrawable
        marker.title = placeName
        map.overlays.add(marker)
        trackRute()
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
            val newPoint = GeoPoint(latitude.toDouble(),longitude.toDouble())
            addMarker(map,newPoint,name,this)
            coordinatesMarks.add(newPoint)
            line = br.readLine()
        }
         trackRute()
    }

    fun onLocationChanged() {
        val toastText = "MENSAJE DE BIENVENIDA"
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
    }


}