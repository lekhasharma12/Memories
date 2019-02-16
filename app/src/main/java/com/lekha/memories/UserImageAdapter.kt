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
import com.google.firebase.database.*

import com.squareup.picasso.Picasso


class UserImageAdapter(private val mContext: Context, private val mUploads: List<Upload>) : RecyclerView.Adapter<UserImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.user_grid_item, parent, false)
        return ImageViewHolder(v)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uploadCurrent = mUploads[position]
        var count = 1
        var plus = 0
        var databaseRef: DatabaseReference
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads")
        Picasso.get()
                .load(uploadCurrent.imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .fit()
                .centerCrop()
                .into(holder.imageView)

                }

    override fun getItemCount(): Int {
        return mUploads.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView

        init {
            imageView = itemView.findViewById(R.id.image_view_upload)
        }
    }
}
