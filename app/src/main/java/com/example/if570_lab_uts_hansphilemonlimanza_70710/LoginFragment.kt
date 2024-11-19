package com.example.if570_lab_uts_hansphilemonlimanza_70710

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
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

class LoginFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to Sign Up when the user clicks a "Sign Up" button
        view.findViewById<Button>(R.id.signUpButton).setOnClickListener {
            Log.d("LoginFragment", "Sign Up button clicked")
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        // Example of successful login logic:
        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            val email = view.findViewById<EditText>(R.id.emailEditText).text.toString()
            val password = view.findViewById<EditText>(R.id.passwordEditText).text.toString()

            if (email.isEmpty()) {
                Toast.makeText(activity, "Email is required", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(activity, "Password is required", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Perform login logic (e.g., Firebase Authentication)
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // If login is successful, redirect to MainActivity
                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish() // Close AuthActivity
                    } else {
                        Toast.makeText(
                            activity,
                            "Login failed: " + task.exception?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}