package com.vimdevs.nannyapp

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        var returnRegistrerBtn = findViewById<ImageButton>(R.id.returnRegistrerBtn)
        var registerUserBtn = findViewById<CardView>(R.id.registerUserBtn)

        var emailRegisterET = findViewById<EditText>(R.id.emailRegister)
        var passwordRegisterET = findViewById<EditText>(R.id.passwordRegister)
        var passwordRegisterConfirmET = findViewById<EditText>(R.id.confirmPasswordRegister)

        var nameParentET = findViewById<EditText>(R.id.nameRegister)
        var nameBBET = findViewById<EditText>(R.id.nameBBRegister)

        returnRegistrerBtn.setOnClickListener {
            finish()
        }

        registerUserBtn.setOnClickListener {

            if (!passwordRegisterET.text.toString().equals(passwordRegisterConfirmET.text.toString())) {
                Toast.makeText(applicationContext, "Favor de que las contraseñas coincidan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (emailRegisterET.text.toString().isEmpty()) {
                Toast.makeText(applicationContext, "Favor de llenar el email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth.createUserWithEmailAndPassword(emailRegisterET.text.toString(), passwordRegisterET.text.toString()).addOnSuccessListener { createdUser ->

                var userInfoToInsert = hashMapOf(
                    "parentName" to nameParentET.text.toString(),
                    "babyName" to nameBBET.text.toString(),
                    "email" to emailRegisterET.text.toString(),
                )

                firestore.collection("USERS").document(createdUser.user!!.uid).set(userInfoToInsert)
                    .addOnSuccessListener { res ->
                        Toast.makeText(applicationContext, "Usuario registrado con exito", Toast.LENGTH_SHORT).show()
                        var goToMenu = Intent(this,MainActivity::class.java)
                        startActivity(goToMenu)
                    }.addOnFailureListener {
                        Log.e("REGISTER USER",it.message.toString())
                        Toast.makeText(applicationContext, "Ocurrio un error registrando al usuario - \n".plus(it.message), Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Log.e("REGISTER CREDENTIALS",it.message.toString())
                Toast.makeText(applicationContext, "Ocurrio un error con las crendeciales ingresadas", Toast.LENGTH_SHORT).show()
            }
        }


    }
}