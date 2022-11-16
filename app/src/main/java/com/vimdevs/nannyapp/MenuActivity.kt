package com.vimdevs.nannyapp

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.text.format.DateFormat
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
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



        var bbyNameMenu = findViewById<TextView>(R.id.bbyNameMenu)
        var logOutBtn = findViewById<CardView>(R.id.logOutBtn)
        var lastFoodTime = findViewById<TextView>(R.id.lastFoodTime)
        var lastDiaperTime = findViewById<TextView>(R.id.lastDiaperTime)


        var openAnalyticsLastFoods = findViewById<CardView>(R.id.goToStatisticsFood)
        var openAnalyticsDiapers = findViewById<CardView>(R.id.goToStatisticsDiapers)
        var registerLastDiaper = findViewById<CardView>(R.id.registerLastDiaper)

        var registerLastFood = findViewById<CardView>(R.id.registerLastFood)
        var lastFoodRef = firebaseDb.getReference("timesByUser").child(mAuth.currentUser!!.uid);
        var lastDiaperRef = firebaseDb.getReference("lastDiaperByUser").child(mAuth.currentUser!!.uid);
        var foodHistory = firebaseDb.getReference("historyByUser").child(mAuth.currentUser!!.uid);
        var diaperHistory = firebaseDb.getReference("diaperHistoryByUser").child(mAuth.currentUser!!.uid);

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
        })

        lastDiaperRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                lastDiaperTime.setText(dataSnapshot.value.toString())
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                lastDiaperTime.setText(dataSnapshot.value.toString())
            }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            }
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        });



        registerLastFood.setOnClickListener{
            val c = Calendar.getInstance()

            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val ampm = if(c.get(Calendar.AM_PM)==0) "AM " else "PM "

            var lastFoodFullTime = year.toString().plus("-").plus(month.toString()).plus("-").plus(day.toString()).plus("/").plus(hour.toString()).plus(":").plus(minute.toString()).plus(" ").plus(ampm)
            var lastFoodLastTime = hour.toString().plus(":").plus(minute.toString()).plus(" ").plus(ampm);

            //Dialog

            val dialogAddLastFood = Dialog(this@MenuActivity)
            dialogAddLastFood.setContentView(R.layout.add_last_food_dialog)
            dialogAddLastFood.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            var registerLastFoodBtn = dialogAddLastFood.findViewById<CardView>(R.id.add_food_dialog)
            var closeLastFoodDialogBtn = dialogAddLastFood.findViewById<CardView>(R.id.close_dialog_food)

            var typeFoodET = dialogAddLastFood.findViewById<EditText>(R.id.tipo_leche)
            var quantityFoodET = dialogAddLastFood.findViewById<EditText>(R.id.cantidad_leche)

            closeLastFoodDialogBtn.setOnClickListener { dialogAddLastFood.cancel() }

            registerLastFoodBtn.setOnClickListener {

                if(typeFoodET.text.isNotEmpty() && quantityFoodET.text.isNotEmpty()){
                    lastFoodRef.child("lastFood").setValue(lastFoodLastTime).addOnFailureListener {
                        Log.e("MENUACTIVITY",it.message.toString())
                    }

                    var fullFoodData = hashMapOf(
                        "date" to lastFoodFullTime,
                        "milkType" to typeFoodET.text.toString(),
                        "quantity" to quantityFoodET.text.toString(),
                    )

                    foodHistory.child(getSaltString()).setValue(fullFoodData).addOnSuccessListener {
                        Toast.makeText(applicationContext,"Se registro correctamente la ultima comida",Toast.LENGTH_SHORT).show()
                        typeFoodET.text.clear()
                        quantityFoodET.text.clear()
                        dialogAddLastFood.cancel()
                    }
                }else{
                    Toast.makeText(applicationContext,"Favor de llenar ambos campos",Toast.LENGTH_SHORT).show()
                }

            }

            dialogAddLastFood.show()

        }

        openAnalyticsLastFoods.setOnClickListener{
            val dialogFoods = Dialog(this@MenuActivity)
            dialogFoods.setContentView(R.layout.foods_recycler)
            dialogFoods.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            var recyclerFoods = dialogFoods.findViewById<RecyclerView>(R.id.recyclerFoods)

            recyclerFoods.layoutManager = LinearLayoutManager(dialogFoods.context)

            var closeFoodsDialogBtn = dialogFoods.findViewById<CardView>(R.id.close_dialog_foods)

            closeFoodsDialogBtn.setOnClickListener{ dialogFoods.dismiss() }

            firebaseDb.getReference("historyByUser").child(mAuth.currentUser!!.uid).addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var FoodsList = arrayListOf<FoodsDataModule>()

                    if(snapshot.exists()){
                        for (food in snapshot.children){
                            var quantity = food.child("quantity").value.toString()
                            var milkType = food.child("milkType").value.toString()
                            var date = food.child("date").value.toString().replace("/"," ")
                            val fd = FoodsDataModule(quantity, date, milkType)
                            FoodsList?.add(fd!!)
                        }
                        val adapter = FoodsAdapterRv(dialogFoods.context!!,FoodsList)
                        recyclerFoods?.setAdapter(adapter)
                    }

                    //Toast.makeText(dialogFoods.context,snapshot.value.toString(),Toast.LENGTH_SHORT).show()

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

            dialogFoods.show()
        }


        registerLastDiaper.setOnClickListener{


            val dialogAddDiaper = Dialog(this@MenuActivity)
            dialogAddDiaper.setContentView(R.layout.add_change_diaper_dialog)
            dialogAddDiaper.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            var cancel_change_diaper = dialogAddDiaper.findViewById<CardView>(R.id.cancel_change_diaper)
            cancel_change_diaper.setOnClickListener{dialogAddDiaper.dismiss()}
            var add_change_diaper = dialogAddDiaper.findViewById<CardView>(R.id.add_change_diaper)


            val color_heces = resources.getStringArray(R.array.color_heces)
            // create an array adapter and pass the required parameter
            // in our case pass the context, drop down layout , and array.
            val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, color_heces)
            // get reference to the autocomplete text view
            val autocompleteTV = dialogAddDiaper.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
            // set adapter to the autocomplete tv to the arrayAdapter
            autocompleteTV.setAdapter(arrayAdapter)


            add_change_diaper.setOnClickListener{
                val c = Calendar.getInstance()

                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val hour = c.get(Calendar.HOUR_OF_DAY)
                val minute = c.get(Calendar.MINUTE)
                val ampm = if(c.get(Calendar.AM_PM)==0) "AM " else "PM"

                var changeType1 = dialogAddDiaper.findViewById<RadioButton>(R.id.changeType1)
                var changeType2 = dialogAddDiaper.findViewById<RadioButton>(R.id.changeType2)
                var changeType3 = dialogAddDiaper.findViewById<RadioButton>(R.id.changeType3)

                var autoCompleteTextView = dialogAddDiaper.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)

                var lastDiaperFullTime = year.toString().plus("-").plus(month.toString()).plus("-").plus(day.toString()).plus(" ").plus(hour.toString()).plus(":").plus(minute.toString()).plus(" ").plus(ampm)
                var lastDiaperLastTime = hour.toString().plus(":").plus(minute.toString()).plus(" ").plus(ampm);

                lastDiaperRef.child("lastDiaper").setValue(lastDiaperLastTime).addOnFailureListener {
                    Log.e("MENUACTIVITY",it.message.toString())
                }

                var typeBath:String
                if(changeType1.isChecked){
                    typeBath = "Pipi"
                }else if(changeType2.isChecked){
                    typeBath = "Popo"
                }else{
                    typeBath = "Ambos"
                }

                var bath = hashMapOf(
                    "date" to lastDiaperFullTime,
                    "type" to typeBath,
                    "color" to autoCompleteTextView.text.toString(),
                )

                diaperHistory.child(getSaltString()).setValue(bath).addOnSuccessListener {
                    Toast.makeText(applicationContext,"Se registro correctamente el cambio de pañal",Toast.LENGTH_SHORT).show()
                    dialogAddDiaper.cancel()
                }

            }

            dialogAddDiaper.show()
        }

        openAnalyticsDiapers.setOnClickListener{
            val dialogDiapers = Dialog(this@MenuActivity)
            dialogDiapers.setContentView(R.layout.diapers_recycler)
            dialogDiapers.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            var recyclerDiaper = dialogDiapers.findViewById<RecyclerView>(R.id.recyclerDiaper)

            recyclerDiaper.layoutManager = LinearLayoutManager(dialogDiapers.context)

            var closeDiapersDialogBtn = dialogDiapers.findViewById<CardView>(R.id.close_dialog_diapers)

            closeDiapersDialogBtn.setOnClickListener{ dialogDiapers.dismiss() }

            firebaseDb.getReference("diaperHistoryByUser").child(mAuth.currentUser!!.uid).addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var DiapersList = arrayListOf<DiapersDataModule>()

                    if(snapshot.exists()){
                        for (diaper in snapshot.children){
                            var date = diaper.child("date").value.toString()
                            var type = diaper.child("type").value.toString()
                            var color = diaper.child("color").value.toString()
                            val dp = DiapersDataModule(date, type, color)
                            DiapersList?.add(dp!!)
                        }
                        val adapter = DiapersAdapterRv(dialogDiapers.context!!,DiapersList)
                        recyclerDiaper?.setAdapter(adapter)
                    }

                    //Toast.makeText(dialogFoods.context,snapshot.value.toString(),Toast.LENGTH_SHORT).show()

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

            dialogDiapers.show()
        }

    }

    protected fun getSaltString(): String {
        val SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
        val salt = StringBuilder()
        val rnd = Random()
        while (salt.length < 10) { // length of the random string.
            val index = (rnd.nextFloat() * SALTCHARS.length).toInt()
            salt.append(SALTCHARS[index])
        }
        return salt.toString()
    }
}