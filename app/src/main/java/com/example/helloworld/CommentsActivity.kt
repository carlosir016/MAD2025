package com.example.helloworld

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class CommentsActivity : AppCompatActivity() {

    private lateinit var commentsRef: DatabaseReference
    private lateinit var commentList: MutableList<Comment>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listView: ListView
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        postId = intent.getStringExtra("postId") ?: return
        val postContent = intent.getStringExtra("postContent") ?: "Sin contenido"

        val postContentTextView = findViewById<TextView>(R.id.postContentTextView)
        postContentTextView.text = postContent
        commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(postId)

        listView = findViewById(R.id.commentListView)
        commentList = mutableListOf()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        findViewById<Button>(R.id.addCommentButton).setOnClickListener {
            showNewCommentDialog()
        }

        findViewById<Button>(R.id.backToPostsButton).setOnClickListener {
            val intent = Intent(this, PostsActivity::class.java)
            startActivity(intent)
            finish()
        }

        loadComments()
    }

    private fun loadComments() {
        commentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val contents = mutableListOf<String>()
                commentList.clear()
                for (commentSnap in snapshot.children) {
                    val comment = commentSnap.getValue(Comment::class.java)
                    comment?.let {
                        commentList.add(it)
                        contents.add("${it.author}: ${it.content}")
                    }
                }
                adapter.clear()
                adapter.addAll(contents)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CommentsActivity", "Error: ${error.message}")
            }
        })
    }

    private fun showNewCommentDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_comment, null)
        val inputField = dialogView.findViewById<EditText>(R.id.editCommentText)

        AlertDialog.Builder(this)
            .setTitle("Nuevo comentario")
            .setView(dialogView)
            .setPositiveButton("Comentar") { _, _ ->
                val content = inputField.text.toString().trim()
                if (content.isNotEmpty()) {
                    val id = commentsRef.push().key ?: return@setPositiveButton
                    val comment = Comment(id, postId, content, "Anónimo")
                    commentsRef.child(id).setValue(comment)
                } else {
                    Toast.makeText(this, "El comentario está vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
