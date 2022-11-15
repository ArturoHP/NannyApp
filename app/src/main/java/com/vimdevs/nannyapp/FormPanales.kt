package com.vimdevs.nannyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton

class FormPanales : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formpanales)
    }
    //Si quieres BORRA ESTO
    fun RadioButtonClicked(view: View){
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked
            // Check which radio button was clicked
            when (view.getId()) {
                R.id.Group1R1 ->
                    if (checked) {
                        // Pirates are the best
                    }
                R.id.Group1R2 ->
                    if (checked) {
                        // Ninjas rule
                    }
            }
        }
    }
}