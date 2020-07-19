package com.alfanshter.aplikasiiska.auth

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.alfanshter.aplikasiiska.R
import com.alfanshter.aplikasiiska.home.bottonNavigation
import com.alfanshter.aplikasiiska.home.dashboardActivity
import com.alfanshter.udinlelangfix.Session.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {
    lateinit var progressdialog: ProgressDialog
    lateinit var userID: String
    lateinit var user: FirebaseUser
    lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var sessionManager: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sessionManager = SessionManager(this)

        progressdialog = ProgressDialog(this)

        login.setOnClickListener {
            login()
        }

        if (sessionManager.getLogin()!!) {
            startActivity<bottonNavigation>()
            finish()
        }

        signup.setOnClickListener {
            startActivity<RegisterActivity>()
        }
    }


    fun login() {

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Sedang Login .....")
        progressDialog.show()
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Udang").child("Users")
        var userss = users.text.toString()
        var password = pass.text.toString()


        if (!TextUtils.isEmpty(userss) && !TextUtils.isEmpty(password)) {
            auth.signInWithEmailAndPassword(userss, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        sessionManager.setLogin(true)
                        startActivity<bottonNavigation>()
                        finish()
                        progressDialog.dismiss()
                    } else {
                        toast("gagal login")
                    }
                }
        } else {
            toast("masukkan username dan password")

        }

    }
}
