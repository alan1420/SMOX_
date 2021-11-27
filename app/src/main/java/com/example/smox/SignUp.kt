package com.example.smox

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import java.util.*

class SignUp : AppCompatActivity() {
    private var mFirstName: EditText? = null
    private var mLastName: EditText? = null
    private var mBirthday: EditText? = null
    private var mPhoneNumber: EditText? = null
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var mCPassword: EditText? = null
    private var isPasswordVisible = false
    private var isCPasswordVisible = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)

        mFirstName = findViewById(R.id.firstname)
        mLastName = findViewById(R.id.lastname)
        mBirthday = findViewById(R.id.enterbirthday)
        mPhoneNumber = findViewById(R.id.enternumber)
        mEmail = findViewById(R.id.enteremail)
        mPassword = findViewById(R.id.enterpassword)
        mCPassword = findViewById(R.id.enterconfirmpassword)

        mBirthday?.let { setBirthdayEditText(it) }

        mPassword!!.setOnTouchListener(OnTouchListener { _, event ->
            val right = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= mPassword!!.right - mPassword!!.compoundDrawables[right].bounds.width()) {
                    val selection = mPassword!!.selectionEnd
                    if (isPasswordVisible) {
                        // set drawable image
                        mPassword!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.i_hide, 0)
                        // hide Password
                        mPassword!!.transformationMethod = PasswordTransformationMethod.getInstance()
                        isPasswordVisible = false
                    } else {
                        // set drawable image
                        mPassword!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.i_show, 0)
                        // show Password
                        mPassword!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        isPasswordVisible = true
                    }
                    mPassword!!.setSelection(selection)
                    return@OnTouchListener true
                }
            }
            false
        })

        mCPassword!!.setOnTouchListener(OnTouchListener { _, event ->
            val right = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= mCPassword!!.right - mCPassword!!.compoundDrawables[right].bounds.width()) {
                    val selection = mCPassword!!.selectionEnd
                    if (isCPasswordVisible) {
                        // set drawable image
                        mCPassword!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.i_hide, 0)
                        // hide Password
                        mCPassword!!.transformationMethod = PasswordTransformationMethod.getInstance()
                        isCPasswordVisible = false
                    } else {
                        // set drawable image
                        mCPassword!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.i_show, 0)
                        // show Password
                        mCPassword!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        isCPasswordVisible = true
                    }
                    mCPassword!!.setSelection(selection)
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    fun register(view: View) {
        val url = "http://192.168.0.88/smox/public/api/signup"
        val password = mPassword!!.text.toString()
        //val CPassword = mCPassword!!.text.toString()

        val data = JSONObject()
        data.put("first_name", mFirstName!!.text.toString())
        data.put("last_name", mLastName!!.text.toString())
        data.put("birthday", mBirthday!!.text.toString())
        data.put("email", mEmail!!.text.toString())
        data.put("phoneNumber", mPhoneNumber!!.text.toString())
        data.put("password", password)

        println(data)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, data,
            { response ->
                println("Sukses!")
                val intent = Intent(this, SignIn::class.java)
                Toast.makeText(this, "Successfully signed up! Now please sign in", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            },
            { error ->
                // TODO: Handle error
                println("Error")
                println(error)
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SignIn::class.java)
                startActivity(intent)
                finish()
            }
        )
        //val queue = VolleySingleton.getInstance(this.applicationContext).requestQueue
        //queue.add(jsonObjectRequest)
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    fun signinPage(view: View? = null) {
        val intent = Intent(this, SignIn::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun setBirthdayEditText(birthdayEditText: EditText) {
        birthdayEditText.addTextChangedListener(object : TextWatcher {

            private var current = ""
            private val ddmmyyyy = "DDMMYYYY"
            private val cal = Calendar.getInstance()

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() != current) {
                    var clean = p0.toString().replace("[^\\d.]|\\.".toRegex(), "")
                    val cleanC = current.replace("[^\\d.]|\\.", "")

                    val cl = clean.length
                    var sel = cl
                    var i = 2
                    while (i <= cl && i < 6) {
                        sel++
                        i += 2
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean == cleanC) sel--

                    if (clean.length < 8) {
                        clean += ddmmyyyy.substring(clean.length)
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        var day = Integer.parseInt(clean.substring(0, 2))
                        var mon = Integer.parseInt(clean.substring(2, 4))
                        var year = Integer.parseInt(clean.substring(4, 8))

                        mon = if (mon < 1) 1 else if (mon > 12) 12 else mon
                        cal.set(Calendar.MONTH, mon - 1)
                        year = if (year < 1900) 1900 else if (year > 2100) 2100 else year
                        cal.set(Calendar.YEAR, year)
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = if (day > cal.getActualMaximum(Calendar.DATE)) cal.getActualMaximum(Calendar.DATE) else day
                        clean = String.format("%02d%02d%02d", day, mon, year)
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8))

                    sel = if (sel < 0) 0 else sel
                    current = clean
                    birthdayEditText.setText(current)
                    birthdayEditText.setSelection(if (sel < current.count()) sel else current.count())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable) {

            }
        })
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}