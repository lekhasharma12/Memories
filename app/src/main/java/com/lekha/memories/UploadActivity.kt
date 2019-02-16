package com.lekha.memories

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_upload.*
import com.squareup.picasso.Picasso
import android.widget.Toast
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.OnProgressListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import android.webkit.MimeTypeMap
import android.content.ContentResolver
import android.os.Handler
import android.util.Log
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.StorageTask








class UploadActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1

    lateinit var mButtonUpload: Button
    lateinit var mCaption: EditText
    lateinit var mImageView: ImageView
    lateinit var mToolBar : android.support.v7.widget.Toolbar
    lateinit var mDatabaseRef: DatabaseReference
    lateinit var mDatabaseRef1: DatabaseReference
    lateinit var mDatabaseRef2: DatabaseReference
    lateinit var mStorageRef: StorageReference
    lateinit var mProgressBar: ProgressBar
    lateinit var mAuth: FirebaseAuth
    lateinit var mUserName: String
    lateinit var link: String

    private var mImageUri: Uri?= null


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val intent = Intent(applicationContext, MainActivity::class.java)
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
        setContentView(R.layout.activity_upload)

        mButtonUpload = findViewById(R.id.btn_upload) as Button
        mCaption = findViewById(R.id.caption) as EditText
        mImageView = findViewById(R.id.addimg) as ImageView
        mProgressBar = findViewById(R.id.progressBar) as ProgressBar
        mToolBar = findViewById(R.id.toolbar) as android.support.v7.widget.Toolbar
        mAuth = FirebaseAuth.getInstance()
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads")
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads")
        mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("likes")


        val currentUser = mAuth.currentUser!!.uid
        mDatabaseRef1 = FirebaseDatabase.getInstance().getReference("Users").child(currentUser)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        mDatabaseRef1.child("name").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mUserName = snapshot.getValue(String::class.java).toString()
                Log.d("nameee",mUserName)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        mImageView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                openFileChooser()
            }
        })
        mButtonUpload.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                uploadFile()
            }
        })

    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.data != null) {
            mImageUri = data.data

            Picasso.get().load(mImageUri).into(mImageView)
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun uploadFile() {


        if (mImageUri != null) {
            val fileReference = mStorageRef.child(System.currentTimeMillis().toString()
                    + "." + getFileExtension(mImageUri as Uri))
            Log.d("whatt", fileReference.toString())
            var mUploadTask = fileReference.putFile(mImageUri as Uri)

            val task1 = mUploadTask
            val urlTask = task1.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileReference.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    link = task.result.toString()
                    Log.d("linkk", link)
                } else {
                    // Handle failures
                    // ...
                }

            }
            task1.addOnFailureListener { e -> Toast.makeText(this@UploadActivity, e.message, Toast.LENGTH_SHORT).show() }
                    .addOnProgressListener { taskSnapshot ->
                        val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                        mProgressBar.setProgress(progress.toInt())
                    }
            urlTask.addOnSuccessListener { taskSnapshot ->
                val handler = Handler()
                handler.postDelayed(Runnable { mProgressBar.setProgress(0) }, 500)
                Toast.makeText(this@UploadActivity, "Upload successful", Toast.LENGTH_LONG).show()
                val upload = Upload(mCaption.getText().toString().trim(),
                        link, mUserName, mAuth.currentUser!!.uid, 0)
                val uploadId = mDatabaseRef.push().key
                upload.id = uploadId

                mDatabaseRef.child(uploadId.toString()).setValue(upload)
                mDatabaseRef2.child(uploadId.toString()).child(mAuth.currentUser!!.uid).child("like").setValue(false)
                returnToMain()
            }
        }

            else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }

    }

    private fun returnToMain() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
