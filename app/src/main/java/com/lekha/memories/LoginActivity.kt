package com.lekha.memories

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.lekha.memories.R.id.email



class LoginActivity : AppCompatActivity() {

    lateinit var nLoginBtn: Button
    lateinit var nAddAccount: TextView
    lateinit var nEmail: EditText
    lateinit var nPasswd: EditText
    lateinit var mAuth: FirebaseAuth
    lateinit var nProgressBar: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        nEmail = findViewById(R.id.email) as EditText
        nPasswd = findViewById(R.id.password) as EditText
        nLoginBtn = findViewById(R.id.btn_sign_in) as Button
        nAddAccount = findViewById(R.id.add_acc) as TextView
        mAuth = FirebaseAuth.getInstance()
        nProgressBar = ProgressDialog(this)

        nLoginBtn.setOnClickListener {

            val email = nEmail.text.toString().trim()
            val password = nPasswd.text.toString().trim()

            if(TextUtils.isEmpty(email)) {
                nEmail.error = "Enter Email"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(password)) {
                nPasswd.error = "Enter Password"
                return@setOnClickListener
            }

            loginUser(email, password)

        }

        nAddAccount.setOnClickListener {
            val intent = Intent(applicationContext, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun loginUser(email: String, password: String) {
        nProgressBar.setMessage("Please Wait...")
        nProgressBar.show()
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        nProgressBar.dismiss()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    else {
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        nProgressBar.dismiss()
                    }
                }
    }
}
