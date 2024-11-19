package com.example.if570_lab_uts_hansphilemonlimanza_70710

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val updateButton = view.findViewById<Button>(R.id.buttonUpdate)
        val nameEditText = view.findViewById<EditText>(R.id.editTextName)
        val nimEditText = view.findViewById<EditText>(R.id.editTextNIM)

        currentUser?.let {
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        nameEditText.setText(document.getString("name"))
                        nimEditText.setText(document.getString("nim"))
                    } else {
                        Log.d("ProfileFragment", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("ProfileFragment", "get failed with ", exception)
                }
        }
        updateButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val nim = nimEditText.text.toString().trim()
            currentUser?.let {
                updateProfile(db, it.uid, name, nim)
            }
        }
    }

    private fun updateProfile(db: FirebaseFirestore, userId: String, name: String, nim: String) {
        val user = hashMapOf(
            "name" to name,
            "nim" to nim
        )

        db.collection("users").document(userId) // Replace "user_id" with the actual user ID
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(activity, "Profile updated successfully", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    activity,
                    "Error updating profile: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("ProfileFragment", "Error updating profile: ${e.message}")
            }
    }
}