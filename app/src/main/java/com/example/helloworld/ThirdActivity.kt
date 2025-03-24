package com.example.helloworld
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.helloworld.room.CoordinateEntity
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.example.helloworld.room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ThirdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("THIRD", "Welcome to the third activity")

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_third)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        CoroutineScope(Dispatchers.Main).launch {
            val coords = getCoordinates()

            createListView(coords)
        }

        createNav()

    }

    suspend fun getCoordinates():List<CoordinateEntity>{
        return withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            db.coordDao().getAll()
        }
    }

    fun createListView(coords: List<CoordinateEntity>){
        val names = coords.map { it.name }

        val listView = findViewById<ListView>(R.id.coordinatesList)
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,names)
        listView.adapter = adapter
        listView.setOnItemClickListener  { _,_,position,_ ->
            val item = coords[position]
            showCoordinatePopUp(item)

        }
    }

    fun showCoordinatePopUp(coordinate: CoordinateEntity){
        val message = "Latitude:${coordinate.latitude}\n" +
                "Longitude:${coordinate.longitude}\n" +
                "Ground:${coordinate.ground}"
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setTitle(coordinate.name)
        dialogBuilder.setMessage(message)

        dialogBuilder.setPositiveButton ("Close"){ dialog,_->
            dialog.dismiss()
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    fun createNav(){
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, SecondActivity::class.java))
                    true
                }

                R.id.navigation_map -> {
                    startActivity(Intent(this, OpenStreetMapsActivity::class.java))
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
}
