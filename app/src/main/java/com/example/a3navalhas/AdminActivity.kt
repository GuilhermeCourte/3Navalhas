package com.example.a3navalhas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AdminActivity : AppCompatActivity() {

    private lateinit var buttonManageServices: MaterialButton
    private lateinit var buttonManageUnits: MaterialButton
    private lateinit var buttonManageBarbers: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        buttonManageServices = findViewById(R.id.buttonManageServices)
        buttonManageUnits = findViewById(R.id.buttonManageUnits)
        buttonManageBarbers = findViewById(R.id.buttonManageBarbers)

        buttonManageServices.setOnClickListener {
            val intent = Intent(this, ManageServicesActivity::class.java)
            startActivity(intent)
        }

        buttonManageUnits.setOnClickListener {
            val intent = Intent(this, ManageUnitsActivity::class.java)
            startActivity(intent)
        }

        buttonManageBarbers.setOnClickListener {
            val intent = Intent(this, ManageBarbersActivity::class.java)
            startActivity(intent)
        }
    }
}