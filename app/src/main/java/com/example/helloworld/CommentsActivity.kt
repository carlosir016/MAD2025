package com.example.helloworld


import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
        commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(postId)

        listView = findViewById(R.id.commentListView)
        commentList = mutableListOf()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        findViewById<Button>(R.id.addCommentButton).setOnClickListener {
            showNewCommentDialog()
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
                        contents.add(it.content)
                    }
                }
                adapter.clear()
                adapter.addAll(contents)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CommentsActivity", "Error: ${error.message}")
            }
        })
    }

    private fun showNewCommentDialog() {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Nuevo comentario")
            .setView(input)
            .setPositiveButton("Comentar") { _, _ ->
                val content = input.text.toString()
                val id = commentsRef.push().key ?: return@setPositiveButton
                val comment = Comment(id, postId, content, "Anónimo")
                commentsRef.child(id).setValue(comment)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
