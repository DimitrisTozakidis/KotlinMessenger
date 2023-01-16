package com.example.kotlinmessenger


import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        Register_button.setOnClickListener {
            val email = EmailAddress_edittext_register.text.toString()
            val password = Password_edittext_register.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "The Email/Password can't be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("RegisterActivity", "Email is " + email)
            Log.d("RegisterActivity", "Password is $password")
            //firebase authentication to create a user with email and password

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
//                    else if successful
                    Log.d("RegisterActivity", "Succesfully created user with uid: ${it.result.user?.uid}")
                    uploadImageToFirebaseStorage()
                }
                .addOnFailureListener {
                    Log.d("RegisterActivity", "Failed to create user: ${it.message}")
                    Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }
        already_have_account_textView.setOnClickListener{
            Log.d("RegisterActivity", "Show login activity")
//      launch the login activity
            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        select_photo_button_register.setOnClickListener{
            Log.d("RegisterActivity", "Try to show photo")
            val intent= Intent(Intent.ACTION_PICK)
            intent.type= "image/*"
            startActivityForResult(intent, 0)
        }
    }

    val selectedPhotoUri: Uri?= null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== 0 && resultCode== RESULT_OK && data != null){
            Log.d("RegisterActivity", "A Photo was selected ")
            val selectedPhotoUri= data.data
            val bitmap= MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            val bitmapDrawable= BitmapDrawable(bitmap)
            select_photo_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }
    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri== null) return
        val filename = UUID.randomUUID().toString()
        val ref =FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Photo saved: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d("RegisterActivity", "File location: $it")
                    saveUserToFirebaseDatabase(it.toString())

                }
            }
            .addOnFailureListener{
                Log.d("RegisterActivity", " Failed to save picture")
                saveUserToFirebaseDatabase(it.toString())
            }
    }
    private fun saveUserToFirebaseDatabase(profileImageurl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref =FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user= User(uid, username_edittext_register.text.toString(), profileImageurl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Finally we saved our user to the Firebase")

                val intent= Intent(this, LatestMessagesActivity::class.java)
                intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

                startActivity()
            }

    }
}

class User(val uid: String, val username: String, val profileImageurl: String)