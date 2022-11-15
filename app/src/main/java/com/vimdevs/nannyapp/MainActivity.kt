package com.vimdevs.nannyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Instances
        mAuth = FirebaseAuth.getInstance()

        var initSesionBtn = findViewById<CardView>(R.id.initSesionBtn)
        var registerBtn = findViewById<TextView>(R.id.registerBtn)

        var emailEditText = findViewById<EditText>(R.id.emailLogin)
        var passwordEditText = findViewById<EditText>(R.id.passwordLogin)

        initSesionBtn.setOnClickListener{
            //Redirigir a pagina principal
            if(emailEditText.text.isBlank() && passwordEditText.text.isBlank()){
                Toast.makeText(applicationContext,"Favor de llenar ambos campos",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth.signInWithEmailAndPassword(emailEditText.text.toString(),passwordEditText.text.toString()).addOnSuccessListener {
                Toast.makeText(applicationContext,"Hooooola - ".plus(it.user!!.email.toString()),Toast.LENGTH_SHORT).show()
                if (it.user != null) {
                    Toast.makeText(applicationContext,"Sesión Iniciada",Toast.LENGTH_SHORT).show()
                    var goToMenu = Intent(this, MenuActivity::class.java)
                    startActivity(goToMenu)
                }else{
                    Toast.makeText(applicationContext,"Usuario no encontrado",Toast.LENGTH_SHORT).show()
                }
            }
        }
        registerBtn.setOnClickListener{
            //Redirigir a register
            var goToRegister = Intent(this,RegisterActivity::class.java)
            startActivity(goToRegister)
        }

    }

    override fun onStart() {
        super.onStart()
        if(mAuth.currentUser != null){
            var goToLogin = Intent(this,MenuActivity::class.java)
            startActivity(goToLogin)
        }
    }
}