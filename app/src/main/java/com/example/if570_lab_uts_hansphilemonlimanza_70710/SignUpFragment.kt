package com.example.if570_lab_uts_hansphilemonlimanza_70710

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SignUpFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate back to Login when the user clicks a "Login" button
        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        // Example of successful signup logic:
        view.findViewById<Button>(R.id.signUpButton).setOnClickListener {
            val email = view.findViewById<EditText>(R.id.editTextEmail).text.toString().trim()
            val password = view.findViewById<EditText>(R.id.editTextPassword).text.toString().trim()
            val name = view.findViewById<EditText>(R.id.editTextName).text.toString().trim()
            val nim = view.findViewById<EditText>(R.id.editTextNIM).text.toString().trim()

            // Input validation
            if (name.isEmpty()) {
                Toast.makeText(activity, "Name is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (nim.isEmpty()) {
                Toast.makeText(activity, "NIM is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                Toast.makeText(activity, "Email is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(activity, "Password is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(
                    activity,
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            // Perform sign-up logic (e.g., Firebase Authentication)
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Get the newly created user
                        val user = FirebaseAuth.getInstance().currentUser

                        // Save additional user information to the database
                        val userId = user?.uid
                        val userMap = hashMapOf(
                            "name" to name,
                            "nim" to nim,
                            "email" to email
                        )

                        val db = FirebaseFirestore.getInstance()
                        userId?.let {
                            db.collection("users").document(it).set(userMap)
                                .addOnSuccessListener {
                                    // If sign-up is successful, redirect to MainActivity
                                    val intent = Intent(activity, MainActivity::class.java)
                                    startActivity(intent)
                                    requireActivity().finish() // Close AuthActivity
                                }
                                .addOnFailureListener { e ->
                                    // Handle failure
                                    Toast.makeText(
                                        activity,
                                        "Error saving user info: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Log.d("SignUpFragment", "Error saving user info: ${e.message}")
                                }
                        }
                    } else {
                        // Handle sign-up failure (e.g., show error message)
                        Toast.makeText(
                            activity,
                            "Sign-up failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}