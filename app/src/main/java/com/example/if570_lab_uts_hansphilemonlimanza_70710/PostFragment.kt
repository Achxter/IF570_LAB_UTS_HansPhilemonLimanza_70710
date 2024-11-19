package com.example.if570_lab_uts_hansphilemonlimanza_70710

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class PostFragment : Fragment() {
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null
    private lateinit var ivSelectedImage: ImageView
    private lateinit var etPostContent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSelectImage = view.findViewById<Button>(R.id.buttonUploadImage)
        val btnPost = view.findViewById<Button>(R.id.buttonPost)
        ivSelectedImage = view.findViewById<ImageView>(R.id.imageViewPreview)
        etPostContent = view.findViewById<EditText>(R.id.editTextPost)

        // Select Image
        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Handle post action
        btnPost.setOnClickListener {
            val content = etPostContent.text.toString().trim()
            if (content.isNotEmpty() || selectedImageUri != null) {
                uploadPost(content)
            } else {
                Toast.makeText(context, "Please add text or select an image", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            ivSelectedImage.setImageURI(selectedImageUri)
            Log.d("PostFragment", "Image selected: $selectedImageUri")
        } else {
            Toast.makeText(
                context,
                "Failed to get image: " + (data?.dataString ?: "No data returned"),
                Toast.LENGTH_SHORT
            ).show()
            Log.d(
                "PostFragment",
                "Failed to get image: " + (data?.dataString ?: "No data returned")
            )
        }
    }

    private fun uploadPost(content: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("PostFragment", "Authenticated User ID: $userId")

        val timestamp = System.currentTimeMillis()

        // If an image is selected, upload it to Firebase Storage
        if (selectedImageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference
                .child("post_images/${userId}_${timestamp}.jpg")

            storageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        savePostToFirestore(content, uri.toString(), timestamp)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    Log.d("PostFragment", "Failed to upload image")
                }
        } else {
            // If no image is selected, save only the text post
            savePostToFirestore(content, null, timestamp)
        }
    }

    private fun savePostToFirestore(content: String, imageUrl: String?, timestamp: Long) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Create the post object with likes and isPinned fields
        val post = hashMapOf(
            "id" to "", // Placeholder for the post ID
            "userId" to userId,
            "content" to content,
            "imageUrl" to imageUrl,  // This will be null if no image is uploaded
            "timestamp" to timestamp,
            "likes" to 0, // Initialize likes to 0
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("posts")
            .add(post)
            .addOnSuccessListener { documentReference ->
                val postId = documentReference.id
                db.collection("posts").document(postId).update("id", postId)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Post uploaded successfully", Toast.LENGTH_SHORT)
                            .show()
                        Log.d("PostFragment", "Post added with ID: $postId")

                        // Clear the form or navigate back
                        findNavController().navigate(R.id.action_post_to_home)
                        etPostContent.text.clear()
                        ivSelectedImage.setImageURI(null)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to update post ID", Toast.LENGTH_SHORT)
                            .show()
                        Log.w("PostFragment", "Failed to update post ID", e)
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to save post", Toast.LENGTH_SHORT).show()
                Log.w("PostFragment", "Failed to save post", e)
            }
    }
}