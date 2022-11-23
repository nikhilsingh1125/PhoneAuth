package com.phoneauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {


    lateinit var activity: RegisterActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        activity = this

        val number = findViewById<EditText>(R.id.edtMobile)
        val code = findViewById<EditText>(R.id.edtCode)


        findViewById<AppCompatButton>(R.id.btnRegister).setOnClickListener {

            val mobile = number.text.toString().trim()
            val mobileCode = code.text.toString().trim()


            if(mobileCode.isEmpty()) {
                Toast.makeText(activity, "Please enter your Country Code", Toast.LENGTH_SHORT).show()
            }
            else if (mobile.isEmpty() && mobile.length<10){
                Toast.makeText(activity, "Valid number is required", Toast.LENGTH_SHORT).show()
            }
            else{

                val phoneNumber =number.text.toString().trim()

                val intent = Intent(activity,OtpActivity::class.java)
                intent.putExtra("phoneNumber",phoneNumber)
                startActivity(intent)
            }


        }

    }

    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null){
            startActivity(Intent(activity,MainActivity::class.java))
        }
    }
}