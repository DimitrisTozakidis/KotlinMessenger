package com.example.kotlinmessenger

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val email= editText_EmailAddress_login.text.toString()
        val password= editText_Password_login.text.toString()

        Log.d("Login", "trying to login with email: $email")

        back_to_register_textview.setOnClickListener{
            finish()
        }









    }





}