package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

private lateinit var scaleUp: Animation
private lateinit var scaleDown: Animation
private lateinit var Signicon: Button


class UserAdapter(val context: Context, val userList: ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        scaleUp = AnimationUtils.loadAnimation(context,R.anim.scale_up)
        scaleDown = AnimationUtils.loadAnimation(context,R.anim.scale_down)

        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]

        if(currentUser.profileImageUrl != "null"){
            Picasso.get().load(currentUser.profileImageUrl).into(holder.user_image)
        }
        else{

            holder.user_image.setImageResource(R.drawable.icon)
        }
        holder.textName.text = currentUser.name




        holder.itemView.setOnClickListener {
            val intent = Intent(context,ChatActivity::class.java)

            intent.putExtra("name",currentUser.name)
            intent.putExtra("uid",currentUser.uid)

            context.startActivity(intent)
        }

    }


    override fun getItemCount(): Int {
        return userList.size

    }




    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
        val user_image = itemView.findViewById<ImageView>(R.id.user_img)



    }

}