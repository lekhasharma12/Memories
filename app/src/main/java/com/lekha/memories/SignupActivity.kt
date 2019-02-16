package com.lekha.memories

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.lekha.memories.R.id.email
import kotlinx.android.synthetic.main.activity_signup.*


class SignupActivity : AppCompatActivity() {

    lateinit var nSignupBtn: Button
    lateinit var nUsername: EditText
    lateinit var nPasswd: EditText
    lateinit var nEmail: EditText
    lateinit var nLogin: TextView
    lateinit var nProgressBar: ProgressDialog
    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mAuth = FirebaseAuth.getInstance()
        //mDatabase = FirebaseDatabase.getInstance().getReference("Users")
        nSignupBtn = findViewById(R.id.btn_sign_up) as Button
        nUsername = findViewById(R.id.username) as EditText
        nPasswd = findViewById(R.id.password) as EditText
        nEmail = findViewById(R.id.email) as EditText
        nLogin = findViewById(R.id.sign_in) as TextView
        nProgressBar = ProgressDialog(this)

        nLogin.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        nSignupBtn.setOnClickListener {

            val name = nUsername.text.toString().trim()
            val email = nEmail.text.toString().trim()
            val password = nPasswd.text.toString().trim()

            if(TextUtils.isEmpty(name)) {
                nUsername.error = "Enter Username"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(email)) {
                nEmail.error = "Enter Email"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(password)) {
                nPasswd.error = "Enter Password"
                return@setOnClickListener
            }

            createUser(name, email, password)
        }
    }

    private fun createUser(name: String, email: String, password: String) {

        nProgressBar.setMessage("Please Wait...")
        nProgressBar.show()

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val uid = currentUser!!.uid
                        val userMap = HashMap<String,String>()
                        userMap["name"] = name

                        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(uid)
                        mDatabase.setValue(userMap).addOnCompleteListener(this) { task1 ->
                            if(task1.isSuccessful)
                                nProgressBar.dismiss()
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                finish()


                        }

                    } else {
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        nProgressBar.dismiss()

                    }

                }

    }

}
