package com.example.if570_lab_uts_hansphilemonlimanza_70710

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private val postsList = mutableListOf<Post>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Fetch posts from Firestore
        fetchPosts()
    }

    private fun fetchPosts() {
        val db = FirebaseFirestore.getInstance()
        db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val post = document.toObject(Post::class.java)
                    postsList.add(post)
                }
                if (postsList.isEmpty()) {
                    // Show a message when the list is empty
                    Toast.makeText(context, "List is empty, add posts", Toast.LENGTH_SHORT).show()
                } else {
                    setupRecyclerView()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(
            postsList,
            onLikeClick = { post -> handleLike(post) },
        )
        recyclerView.adapter = postAdapter
    }

    private fun handleLike(post: Post) {
        Log.d("HomeFragment", "Post ID: $post.id")

        val db = FirebaseFirestore.getInstance()
        val postRef = db.collection("posts").document(post.id)

        postRef.update("likes", post.likes + 1)
            .addOnSuccessListener {
                Log.d(TAG, "Post liked successfully")
                postsList.clear() // Clear the list
                fetchPosts() // Fetch updated posts
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error liking post", e)
            }
    }
}
