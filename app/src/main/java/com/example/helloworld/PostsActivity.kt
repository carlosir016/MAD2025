package com.example.helloworld


import android.content.Intent

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity



import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class PostsActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val postList = mutableListOf<Post>()
    private val database = FirebaseDatabase.getInstance().getReference("posts")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        listView = findViewById(R.id.postListView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        findViewById<Button>(R.id.newPostButton).setOnClickListener {
            showNewPostDialog()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val post = postList[position]
            val intent = Intent(this, CommentsActivity::class.java)
            intent.putExtra("postId", post.id)
            startActivity(intent)

            loadPosts()

        }
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

                R.id.navigation_post -> {
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

    private fun loadPosts() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()
                val titles = mutableListOf<String>()
                for (postSnap in snapshot.children) {
                    val post = postSnap.getValue(Post::class.java)
                    post?.let {
                        postList.add(it)
                        titles.add(it.title)
                    }
                }
                adapter.clear()
                adapter.addAll(titles)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PostsActivity", "Error cargando posts: ${error.message}")
            }
        })
    }

    private fun showNewPostDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_post, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Nuevo Post")
            .setView(dialogView)
            .setPositiveButton("Publicar") { _, _ ->
                val title = dialogView.findViewById<EditText>(R.id.titleEditText).text.toString()
                val content = dialogView.findViewById<EditText>(R.id.contentEditText).text.toString()
                val id = database.push().key ?: return@setPositiveButton
                val post = Post(id, title, content, "Anónimo")
                database.child(id).setValue(post)
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }
}
