package com.example.chatapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class profile : AppCompatActivity() {

    private lateinit var profilePic: CircleImageView
    private lateinit var profileName: TextView
    private lateinit var profileId: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mDbRef: DatabaseReference
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var pic: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profilePic = findViewById(R.id.profile_pic)
        profileName = findViewById(R.id.profile_name1)
        profileId = findViewById(R.id.profile_id1)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        userList = ArrayList()
        adapter = UserAdapter(this,userList)



        mDbRef.child("user").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                for (postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (mAuth.currentUser?.uid == currentUser?.uid){
                        profileName.text = currentUser?.name
                        name = currentUser?.name.toString()
                        profileId.text = currentUser?.email
                        email = currentUser?.email.toString()
                        pic = currentUser?.profileImageUrl.toString()
                        if(currentUser?.profileImageUrl != "null"){
                            Picasso.get().load(currentUser?.profileImageUrl).into(profilePic)
                        }
                    break
                    }


                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        profilePic.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }



    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==0 && resultCode == Activity.RESULT_OK && data != null){

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            profilePic.setImageBitmap(bitmap)
            uploadImageTofirebaseStorage(name,email)

            //val bitmapDrawable = BitmapDrawable(bitmap)
            //btnPhoto.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String, profileImageUri: String){
        mDbRef = FirebaseDatabase.getInstance().getReference()
        pic = profileImageUri

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