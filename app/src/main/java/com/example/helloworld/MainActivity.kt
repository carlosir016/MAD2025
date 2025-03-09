package com.example.helloworld


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {

    val loginFile = "login.txt"
    var users : MutableList<String> = mutableListOf()
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

        readFile()

        val signInButton : Button = findViewById(R.id.SignIn)
        signInButton.setOnClickListener{
            signIn()
        }

        val logInButton : Button = findViewById(R.id.LogIn)
        logInButton.setOnClickListener {
            logIn()
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_map -> {
                    val intent = Intent(this, OpenStreetMapsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_list -> {
                    val intent = Intent(this, SecondActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        }

    fun signIn() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Register")

        val registerLayout = layoutInflater.inflate(R.layout.dialog_register,null)

        val userName = registerLayout.findViewById<EditText>(R.id.userName).text
        val password = registerLayout.findViewById<EditText>(R.id.password).text

        builder.setView(registerLayout)
        builder.setPositiveButton("SignIn"){ _ , _ ->
            if(userName.isNotBlank() && password.isNotBlank()) {
                saveUser(userName.toString(),password.toString())
                val intent = Intent(this,SecondActivity::class.java)
                startActivity(intent)
            } else
                Toast.makeText(this, "Invalid data", Toast.LENGTH_LONG).show()
        }
        builder.setNegativeButton("Cancel") { dialog , _ ->
            dialog.cancel()
        }
        builder.show()
    }

    @SuppressLint("UnsafeIntentLaunch")
    fun logIn() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Log-In")

        val logInLayout = layoutInflater.inflate(R.layout.dialog_login,null)

        val userName = logInLayout.findViewById<EditText>(R.id.userName).text
        val userPassword = logInLayout.findViewById<EditText>(R.id.password).text

        builder.setView(logInLayout)
        builder.setPositiveButton("Log In"){ dialog , _ ->
            if(users.size == 0)
                Toast.makeText(this, "File do not exists", Toast.LENGTH_LONG).show()
            else if(!findUser(users,userName.toString()))
                Toast.makeText(this, "user do not exists", Toast.LENGTH_LONG).show()
            else if(!findPassword(users,userPassword.toString(),userName.toString()))
                Toast.makeText(this,"password dismatch",Toast.LENGTH_LONG).show()
            else {
                intent = Intent(this, SecondActivity::class.java)
                startActivity(intent)
            }

        }
        builder.setNeutralButton("Sign In"){ dialog , _ ->
            dialog.cancel()
            signIn()
        }

        builder.setNegativeButton("Cancel"){ dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun findPassword(users:List<String>,password: String, userName:String):Boolean{
        var exists = false
        users.forEach{
            if(userName in it)
                if(password == it.split(",")[1])
                    exists = true
        }
        return exists
    }

    private fun findUser(users:List<String>, user:String): Boolean {
        var exists = false
        users.forEach {
            if(user == it.split(",")[0])
                exists = true
        }
        return exists
    }

    private fun findFile():Boolean{
        val fileList = fileList()
        var exists = false
        fileList.forEach {
            if(loginFile == it)
                exists = true
        }
        return exists
    }

    fun readFile(){
        if(!findFile()) {
            return
        }
        val file = InputStreamReader(openFileInput(loginFile))
        val br = BufferedReader(file)
        var line = br.readLine()
        while(line != null){
            users.add(line)
            line = br.readLine()
        }
    }

    fun saveUser(user:String,password:String){
        users.add("${user},${password}")
        saveFile()
    }

    fun saveFile(){
        val file = OutputStreamWriter(openFileOutput(loginFile, Activity.MODE_PRIVATE))
        users.forEach{
            file.write(it + "\n")
        }
        file.flush()
        file.close()
    }
}