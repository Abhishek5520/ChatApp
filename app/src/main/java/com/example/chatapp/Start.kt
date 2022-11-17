package com.example.chatapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Start : AppCompatActivity() {

    private lateinit var sharedpref: SharedPreferences
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()





        sharedpref = getSharedPreferences("login", MODE_PRIVATE)


            if(sharedpref.contains("userName")){


                var hand = Handler()
                hand.postDelayed(Runnable {

                val intent = Intent(this@Start,MainActivity::class.java)
                startActivity(intent)
                finish()
                },1000)
            }
            else{
                val intent = Intent(this@Start,LogIn::class.java)
                startActivity(intent)
                finish()
            }



    }
}