package com.lekha.memories

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.squareup.picasso.Picasso
import com.google.android.gms.tasks.OnSuccessListener




class ImageAdapter(private val mContext: Context, private val mUploads: List<Upload>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabase: DatabaseReference
    lateinit var databaseRef: DatabaseReference
    lateinit var ref: DatabaseReference
    var likeval: Boolean? = false
    var like: Boolean? = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(v)

    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uploadCurrent = mUploads[position]
        var newLike = 0
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().getReference("likes")
        ref = mDatabase.child(uploadCurrent.id.toString()).child(mAuth.currentUser!!.uid)
        ref.child("like").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                likeval = snapshot.getValue(Boolean::class.java)
                Log.d("likess",likeval.toString())

                if (likeval!= null && likeval!!.equals(true)) {
                    holder.likeBtn.setBackgroundResource(R.drawable.ic_like)
                }
                else
                {
                    holder.likeBtn.setBackgroundResource(R.drawable.ic_heart_outline)
                }
                holder.userName.text = uploadCurrent.uname
                holder.textCaption.text = uploadCurrent.caption
                holder.likeCount.text = uploadCurrent.likes.toString()
                Picasso.get()
                        .load(uploadCurrent.imageUrl)
                        .placeholder(R.drawable.ic_placeholder)
                        .fit()
                        .centerCrop()
                        .into(holder.imageView)

            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //ref1 = mDatabase.child(mAuth.currentUser!!.uid)
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads")
        holder.likeBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                Log.d("likecount",likeval.toString())
                var ref1 = mDatabase.child(uploadCurrent.id.toString()).child(mAuth.currentUser!!.uid).child("like")
                ref1.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        like = snapshot.getValue(Boolean::class.java)
                        Log.d("likes1", like.toString())
                        if (like==null)
                        {
                            ref1.setValue(true).addOnSuccessListener {
                                newLike = uploadCurrent.likes!!.plus(1)
                                holder.likeBtn.setBackgroundResource(R.drawable.ic_like)
                                holder.likeCount.text = newLike.toString()
                                databaseRef.child(uploadCurrent.id.toString()).child("likes").setValue(newLike)
                            }
                        }
                        else if (like!!.equals(false))
                        {
                            ref1.setValue(true).addOnSuccessListener {
                                newLike = uploadCurrent.likes!!.plus(1)
                                holder.likeBtn.setBackgroundResource(R.drawable.ic_like)
                                holder.likeCount.text = newLike.toString()
                                databaseRef.child(uploadCurrent.id.toString()).child("likes").setValue(newLike)
                                Log.d("likecount1", likeval.toString())
                            }
                        }
                        else if (like!!.equals(true))
                        {
                            //temp = false
                            ref1.setValue(false).addOnSuccessListener {

                                newLike = uploadCurrent.likes!!.minus(1)
                                holder.likeBtn.setBackgroundResource(R.drawable.ic_heart_outline)
                                holder.likeCount.text = newLike.toString()
                                databaseRef.child(uploadCurrent.id.toString()).child("likes").setValue(newLike)
                                Log.d("likecount2", likeval.toString())
                            }
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        })
    }

    override fun getItemCount(): Int {
        return mUploads.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName: TextView
        var textCaption: TextView
        var imageView: ImageView
        var likeCount: TextView
        var likeBtn: Button

        init {

            userName = itemView.findViewById(R.id.user_name)
            imageView = itemView.findViewById(R.id.image_view_upload)
            textCaption = itemView.findViewById(R.id.caption)
            likeCount = itemView.findViewById(R.id.like_count)
            likeBtn = itemView.findViewById(R.id.like)
        }
    }
}
