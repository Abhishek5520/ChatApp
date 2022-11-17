package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.transition.Visibility
import com.google.firebase.auth.FirebaseAuth

class LogIn : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var progressBar : ProgressBar
    private lateinit var mAuth: FirebaseAuth
    private lateinit var loginView: RelativeLayout
    private lateinit var sharedpref: SharedPreferences




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)


        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btnSignUp)
        progressBar = findViewById(R.id.progressBar)
        loginView = findViewById(R.id.login)


        sharedpref = getSharedPreferences("login", MODE_PRIVATE)

           // if(sharedpref.contains("userName")){

              //  val intent = Intent(this@LogIn,MainActivity::class.java)
             //   finish()
             //   startActivity(intent)
           // }




        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()


            if(email == ""){
                Toast.makeText(this@LogIn, "Please fill Email id", Toast.LENGTH_SHORT).show()
            }
            else if (password == ""){
                Toast.makeText(this@LogIn, "Please fill password", Toast.LENGTH_SHORT).show()
        }
            else{
                loginView.alpha = 0.5f
                progressBar.alpha = 1f
                login(email,password);
            }


        }



    }

    private fun login(email: String, password: String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressBar.alpha = 0f
                    loginView.alpha = 1f
                    var editor: SharedPreferences.Editor = sharedpref.edit()
                    editor.putString("userName",email)
                    editor.putString("passcode",password)
                    editor.apply()

                    val intent = Intent(this@LogIn,MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    progressBar.alpha = 0f
                    loginView.alpha = 1f
                    Toast.makeText(this@LogIn, "User does not exist", Toast.LENGTH_SHORT).show()
                }
            }

    }



}