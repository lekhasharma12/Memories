package com.lekha.memories

import android.annotation.TargetApi
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Adapter
import android.widget.ProgressBar
import android.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_upload.*
import java.util.ArrayList


class MainActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mToolBar : android.support.v7.widget.Toolbar
    lateinit var mDatabaseRef: DatabaseReference
    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: ImageAdapter
    lateinit var mUploads: ArrayList<Upload>
    lateinit var mProgressCircle: ProgressBar

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_dashboard -> {
                val intent = Intent(applicationContext, UploadActivity::class.java)
                startActivity(intent)
                finish()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                val intent = Intent(applicationContext, UserProfileActivity::class.java)
                startActivity(intent)
                finish()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads")

        mToolBar = findViewById(R.id.toolbar) as android.support.v7.widget.Toolbar
        mProgressCircle = findViewById(R.id.progress_circle) as ProgressBar
        mRecyclerView = findViewById(R.id.recycler_view) as RecyclerView
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(this)

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
                        if(upload != null)
                            mUploads.add(upload)
                }
                    mAdapter = ImageAdapter(this@MainActivity, mUploads)
                    mRecyclerView.adapter = mAdapter
                }




                mProgressCircle.visibility = View.INVISIBLE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity, databaseError.message, Toast.LENGTH_SHORT).show()
                mProgressCircle.visibility = View.INVISIBLE
            }
        })

        val currentUser = FirebaseAuth.getInstance().currentUser
        navigation1.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onStart() {
        super.onStart()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser == null) {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if(item?.itemId == R.id.signout) {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return true
    }

}
