package com.example.helloworld

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.io.IOException
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {

    val loginFile = "login.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Log.d("MAIN","Welcome to the first Activity")
        val logIn : Button = findViewById(R.id.LogIn)
        logIn.setOnClickListener{
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showUserIdentifierDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Introducir nombre de usuario")
        val input = EditText(this)
        builder.setView(input)
        builder.setPositiveButton("OK") { dialog, which ->
            val userInput = input.text.toString()
            if (userInput.isNotBlank()) {
                Toast.makeText(this, "User ID saved: $userInput", Toast.LENGTH_LONG).show()
                saveUser(userInput,"1234")

                startActivity(Intent(this, ThirdActivity::class.java))
            } else {
                Toast.makeText(this, "User ID cannot be blank", Toast.LENGTH_LONG).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(this, "Thanks and goodbye!", Toast.LENGTH_LONG).show()
            dialog.cancel()
        }
        builder.show()
    }

    fun saveUser(user:String,password:String){
        val file = OutputStreamWriter(openFileOutput(loginFile, Activity.MODE_PRIVATE))
        file.write("${user},${password}\n")
        file.flush()
        file.close()
    }

}