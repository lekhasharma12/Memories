package com.lekha.memories

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.util.ArrayList

class UserProfileActivity : AppCompatActivity() {


    lateinit var mAuth: FirebaseAuth
    lateinit var mToolBar : android.support.v7.widget.Toolbar
    lateinit var mDatabaseRef: DatabaseReference
    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: UserImageAdapter
    lateinit var mUploads: ArrayList<Upload>
    lateinit var mProgressCircle: ProgressBar
    lateinit var mUserName: TextView
    lateinit var name: String

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                val intent = Intent(applicationContext, UploadActivity::class.java)
                startActivity(intent)
                finish()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)


        mAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads")

        mUserName = findViewById(R.id.user_name) as TextView
        mToolBar = findViewById(R.id.toolbar) as android.support.v7.widget.Toolbar
        mProgressCircle = findViewById(R.id.progress_circle) as ProgressBar
        mRecyclerView = findViewById(R.id.recycler_view) as RecyclerView
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = GridLayoutManager(this, 2)

        mUploads = ArrayList<Upload>()

        setSupportActionBar(mToolBar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        mDatabaseRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if( dataSnapshot.exists())
                {
                    mUploads.clear()
                    for (postSnapshot in dataSnapshot.children) {
                        val upload = postSnapshot.getValue(Upload::class.java)
                        Log.d("uname ", upload!!.uname.toString())
                        if(upload != null && upload.uid!!.equals(mAuth.currentUser!!.uid)) {
                            mUploads.add(upload)
                            name = upload.uname.toString()
                        }
                    }
                    mUserName.text = name
                    mAdapter = UserImageAdapter(this@UserProfileActivity, mUploads)
                    mRecyclerView.adapter = mAdapter
                }

                mProgressCircle.visibility = View.INVISIBLE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@UserProfileActivity, databaseError.message, Toast.LENGTH_SHORT).show()
                mProgressCircle.visibility = View.INVISIBLE
            }
        })




        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
