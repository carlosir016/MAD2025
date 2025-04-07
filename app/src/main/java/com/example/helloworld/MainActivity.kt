package com.example.helloworld


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
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.helloworld.room.AppDatabase
import com.example.helloworld.room.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.ImageView
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    private lateinit var imagen: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        imagen = findViewById(R.id.Imagen)
        val mainLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Log.d("MAIN","Welcome to the first Activity")


        Glide.with(this).load("https://concepto.de/wp-content/uploads/2021/11/tipos-de-suelos-e1637359333414.jpg").into(imagen)

        val signInButton : Button = findViewById(R.id.SignIn)
        signInButton.setOnClickListener{
            signIn()
        }

        val logInButton : Button = findViewById(R.id.LogIn)
        logInButton.setOnClickListener {
            logIn()
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

    fun logIn() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Log-In")

        val logInLayout = layoutInflater.inflate(R.layout.dialog_login,null)
        builder.setView(logInLayout)

        builder.setPositiveButton("Log In"){ _ , _ ->
            val userDB = AppDatabase.getDatabase(this)
            val userName = logInLayout.findViewById<EditText>(R.id.userName).text.toString()
            val userPassword = logInLayout.findViewById<EditText>(R.id.password).text.toString()

            CoroutineScope(Dispatchers.Main).launch{
                if(!findUser(userDB,userName)) {
                    Toast.makeText(this@MainActivity, "user do not exists", Toast.LENGTH_LONG).show()
                }
                else if(!findPassword(userDB,userName,userPassword))
                    Toast.makeText(this@MainActivity,"password dismatch",Toast.LENGTH_LONG).show()
                else {
                    intent = Intent(this@MainActivity, SecondActivity::class.java)
                    startActivity(intent)
                }
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

    private suspend fun findPassword(userDB: AppDatabase, userName:String, password: String):Boolean{
        return withContext (Dispatchers.IO){
            val dbpasswd = userDB.userDao().getPassword(userName)
            password == dbpasswd
        }

    }

    private suspend fun findUser(userDB: AppDatabase, userName:String): Boolean {
        return withContext (Dispatchers.IO){
            val names = userDB.userDao().getNames()
            names.contains(userName)
        }
    }

    fun saveUser(user:String,password:String){
        val user = UserEntity(
            name= user,
            password = password
        )
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            db.userDao().insert(user)
        }
    }
}