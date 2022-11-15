package com.vimdevs.nannyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Comment
import org.w3c.dom.Text
import java.util.*

class MenuActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseDb: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        firestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseDatabase.getInstance()

        val c = Calendar.getInstance()

        var bbyNameMenu = findViewById<TextView>(R.id.bbyNameMenu)
        var logOutBtn = findViewById<FloatingActionButton>(R.id.logOutBtn)
        var lastFoodTime = findViewById<TextView>(R.id.lastFoodTime)

        var registerLastFood = findViewById<CardView>(R.id.registerLastFood)
        var lastFoodRef = firebaseDb.getReference("timesByUser").child(mAuth.currentUser!!.uid);

        firestore.collection("USERS").document(mAuth.currentUser!!.uid).get().addOnSuccessListener { res ->
            bbyNameMenu.setText(res.get("babyName").toString())
        }

        logOutBtn.setOnClickListener{
            finish()
            var goToLogin = Intent(this,MainActivity::class.java)
            startActivity(goToLogin)
            mAuth.signOut()
        }


        lastFoodRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                lastFoodTime.setText(dataSnapshot.value.toString())
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                lastFoodTime.setText(dataSnapshot.value.toString())
            }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            }
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        });

        registerLastFood.setOnClickListener{
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val ampm = if(c.get(Calendar.AM_PM)==0) "AM " else "PM "

            lastFoodRef.child("lastFood").setValue(hour.toString().plus(":").plus(minute.toString()).plus(" ").plus(ampm)).addOnSuccessListener {
                Toast.makeText(applicationContext,"Se registro correctamente la ultima comida",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(applicationContext,": 0",Toast.LENGTH_SHORT).show()
                Log.e("MENUACTIVITY",it.message.toString())
            }
        }

    }
}