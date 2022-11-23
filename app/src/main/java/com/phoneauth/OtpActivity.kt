package com.phoneauth

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {

    lateinit var activity: OtpActivity
    lateinit var verificationId :String
    lateinit var mAuth:FirebaseAuth
    lateinit var edt1 : EditText
    lateinit var edt2: EditText
    lateinit var edt3: EditText
    lateinit var edt4: EditText
    lateinit var edt5: EditText
    lateinit var edt6: EditText
    lateinit var phoneNumber : String
    lateinit var loader: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        activity = this



        verificationId = ""

        mAuth= FirebaseAuth.getInstance()

        edt1 = findViewById(R.id.edt1)
        edt2 = findViewById(R.id.edt2)
        edt3 = findViewById(R.id.edt3)
        edt4 = findViewById(R.id.edt4)
        edt5 = findViewById(R.id.edt5)
        edt6 = findViewById(R.id.edt6)


        phoneNumber = intent.getStringExtra("phoneNumber").toString()
        val number = findViewById<TextView>(R.id.txtNumber)
        val anim = findViewById<LottieAnimationView>(R.id.animationView)
        loader = findViewById(R.id.lottieLoader)
        loader.setAnimation(R.raw.loader)
        anim.setAnimation(R.raw.secure_login)
        anim.playAnimation()

        number.text ="Enter the OTP sent to +91"+phoneNumber

        sendVerificationCode()

        findViewById<AppCompatButton>(R.id.btnSubmit).setOnClickListener {

            val otp = (edt1.text.toString() + edt2.text.toString() + edt3.text.toString() + edt4.text.toString() +edt5.text.toString() +edt6.text.toString())

            if (otp.isEmpty() || otp.length<6){
                Toast.makeText(activity, "Enter Code...", Toast.LENGTH_SHORT).show()
            }
            else{
                loader.visibility = View.VISIBLE
                loader.playAnimation()
                println("test123"+otp)
                verifyCode(otp)
            }

        }

        addTextChangeListener()


    }

    private fun addTextChangeListener(){

        edt1.addTextChangedListener(editTextWatcher(edt1))
        edt2.addTextChangedListener(editTextWatcher(edt2))
        edt3.addTextChangedListener(editTextWatcher(edt3))
        edt4.addTextChangedListener(editTextWatcher(edt4))
        edt5.addTextChangedListener(editTextWatcher(edt5))
        edt6.addTextChangedListener(editTextWatcher(edt6))

    }

    inner class editTextWatcher(var view: View) : TextWatcher {


        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            val text :String =s.toString()
            when(view.id){
                R.id.edt1 -> if (text.length==1) edt2.requestFocus()
                R.id.edt2 -> if (text.length==1) edt3.requestFocus() else if (text.isEmpty()) edt1.requestFocus()
                R.id.edt3 -> if (text.length==1) edt4.requestFocus() else if (text.isEmpty()) edt2.requestFocus()
                R.id.edt4 -> if (text.length==1) edt5.requestFocus() else if (text.isEmpty()) edt3.requestFocus()
                R.id.edt5 -> if (text.length==1) edt6.requestFocus() else if (text.isEmpty()) edt4.requestFocus()
                R.id.edt6 -> if (text.isEmpty()) edt5.requestFocus()
            }
        }

    }



    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    startActivity(Intent(this,MainActivity::class.java))
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    fun sendVerificationCode() {

        loader.visibility = View.VISIBLE
        loader.playAnimation()
        println("test phoneNumber" +phoneNumber)

        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber("+91$phoneNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(mCallBacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    val mCallBacks = object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

        override fun onCodeSent(s: String, token: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(s, token)
            verificationId = s
            loader.visibility = View.GONE
        }

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            val code = credential.smsCode
            if (code != null){
                loader.visibility = View.VISIBLE
                loader.playAnimation()
                verifyCode(code)
            }

        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Toast.makeText(activity, p0.message, Toast.LENGTH_SHORT).show()
        }



    }

    fun verifyCode(code:String){
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }





}