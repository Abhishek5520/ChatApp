package com.example.chatapp

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class SignUp : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtName: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var btnPhoto: Button
    private lateinit var mDbRef: DatabaseReference
    private lateinit var profile: CircleImageView
    private lateinit var signupView: RelativeLayout
    private lateinit var progressBar1 : ProgressBar


    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        edtName = findViewById(R.id.edt_name)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnSignUp = findViewById(R.id.btnSignUp)
        btnPhoto = findViewById(R.id.photo_Button)
        profile = findViewById(R.id.profile_image)
        signupView = findViewById(R.id.signup)
        progressBar1 = findViewById(R.id.progressBar1)

        btnPhoto.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }



        btnSignUp.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            if(name == ""){
                Toast.makeText(this@SignUp, "Please fill Name", Toast.LENGTH_SHORT).show()
            }
            else if(email == ""){
                Toast.makeText(this@SignUp, "Please fill Email id", Toast.LENGTH_SHORT).show()
            }
            else if (password == ""){
                Toast.makeText(this@SignUp, "Please fill password", Toast.LENGTH_SHORT).show()
            }

            else{
                signupView.alpha = 0.5f
                progressBar1.alpha = 1f
                signUp(name,email,password)
            }

        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==0 && resultCode == Activity.RESULT_OK && data != null){

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            profile.setImageBitmap(bitmap)
            btnPhoto.alpha = 0f
            //val bitmapDrawable = BitmapDrawable(bitmap)
            //btnPhoto.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun signUp(name:String, email: String, password: String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressBar1.alpha = 0f
                    signupView.alpha = 1f
                    var sharedPref: SharedPreferences = getSharedPreferences("login",MODE_PRIVATE)
                    var editor: SharedPreferences.Editor = sharedPref.edit()
                    editor.putString("userName",email)
                    editor.putString("passcode",password)
                    editor.apply()

                    var profileImageUri = selectedPhotoUri.toString()
                    addUserToDatabase(name,email,mAuth.currentUser?.uid!!,profileImageUri)
                    uploadImageTofirebaseStorage(name,email)



                    val intent = Intent(this@SignUp,MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    progressBar1.alpha = 0f
                    signupView.alpha = 1f
                    Toast.makeText(this@SignUp,"Invalid Email or Password",Toast.LENGTH_SHORT).show()

                }
            }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String, profileImageUri: String){
        mDbRef = FirebaseDatabase.getInstance().getReference()

        mDbRef.child("user").child(uid).setValue(User(name,email,uid,profileImageUri))

    }


    private fun uploadImageTofirebaseStorage(name: String, email: String) {
        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()

        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                   var profileImageUri = it.toString()
                    addUserToDatabase(name,email,mAuth.currentUser?.uid!!, profileImageUri)
                }
            }


    }






}