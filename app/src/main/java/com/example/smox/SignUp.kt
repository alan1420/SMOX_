package com.example.smox

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.VolleyError
import com.github.ybq.android.spinkit.style.Wave
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
    private lateinit var mShowPassword: ImageView
    private lateinit var mShowCPassword: ImageView
    private lateinit var mHidePassword: ImageView
    private lateinit var mHideCPassword: ImageView
    private var isPasswordVisible = false
    private var isCPasswordVisible = false
    private lateinit var progressbar: ProgressBar
    private lateinit var wave: Wave
    private lateinit var mSignUpText: TextView

    var isGoogle = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra("fullname"))
            setContentView(R.layout.sign_up_google)
        else
            setContentView(R.layout.sign_up)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onStart() {
        super.onStart()
        mFirstName = findViewById(R.id.firstname)
        mLastName = findViewById(R.id.lastname)
        mBirthday = findViewById(R.id.enterbirthday)
        mPhoneNumber = findViewById(R.id.enternumber)
        mBirthday?.let { setBirthdayEditText(it) }

        mSignUpText = findViewById(R.id.textSignUp)
        progressbar = findViewById(R.id.spin_kit)
        wave = Wave()
        progressbar.indeterminateDrawable = wave

        progressbar.visibility = View.INVISIBLE;

        //Sign Up with Google
        if (intent.hasExtra("fullname")) {
            isGoogle = true
            //setContentView(R.layout.sign_up_google)
            //chooseView(2)
            var data = intent.getStringExtra("fullname")
            var parts  = data?.split(" ")?.toMutableList()
            val firstName = parts!!.firstOrNull()
            parts.removeAt(0)
            val lastName = parts.joinToString(" ")
            mFirstName?.setText(firstName)
            mLastName?.setText(lastName)
            Toast.makeText(this, intent.getStringExtra("uuid"), Toast.LENGTH_SHORT).show()
        } else {
            //setContentView(R.layout.sign_up)
            mEmail = findViewById(R.id.enteremail)
            mPassword = findViewById(R.id.enterpassword)
            mCPassword = findViewById(R.id.enterconfirmpassword)
            mShowPassword = findViewById(R.id.showpassword)
            mHidePassword = findViewById(R.id.hidepassword)
            mShowCPassword = findViewById(R.id.showcpassword)
            mHideCPassword = findViewById(R.id.hidecpassword)

            mShowPassword.visibility = View.VISIBLE;
            mHidePassword.visibility = View.INVISIBLE;
            mShowCPassword.visibility = View.VISIBLE;
            mHideCPassword.visibility = View.INVISIBLE;
        }
    }

    fun register(view: View) {
        val firstName = mFirstName?.text.toString()
        val lastName = mLastName?.text.toString()
        val birthday = mBirthday?.text.toString()
        val phoneNumber = mPhoneNumber?.text.toString()
        val password = mPassword?.text.toString()
        val CPassword = mCPassword?.text.toString()

        val data = JSONObject()
        data.put("first_name", firstName)
        data.put("last_name", lastName)
        data.put("birthday", birthday)
        data.put("phoneNumber", phoneNumber)

        if(textIsNotEmpty(firstName) && textIsNotEmpty(lastName) && textIsNotEmpty(birthday)
            && textIsNotEmpty(phoneNumber)) {
            if (!isGoogle) {
                if (textIsNotEmpty(mEmail?.text.toString()) && textIsNotEmpty(password) && password == CPassword) {
                    data.put("email", mEmail!!.text.toString())
                    data.put("password", password)
                } else {
                    Toast.makeText(this@SignUp, "Please re-check your data!", Toast.LENGTH_SHORT).show()
                    return
                }
            } else {
                data.put("uuid", intent.getStringExtra("uuid"))
            }

            val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
            view.startAnimation(clickEffect)

            progressbar.visibility = View.VISIBLE
            mSignUpText.visibility = View.INVISIBLE

            sendDataPOST("signup", "null", data, this.applicationContext,
                object : VolleyResult {
                    override fun onSuccess(response: JSONObject) {
                        val intent = Intent(this@SignUp, SignIn::class.java)
                        Toast.makeText(this@SignUp, "Successfully signed up! Now please sign in", Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                        finish()
                    }

                    override fun onError(error: VolleyError?) {
                        if (error != null) {
                            var messageError = ""
                            if (error.networkResponse != null)
                                messageError = if (error.networkResponse.statusCode == 500)
                                    "Please re-check your data!"
                                else
                                    getVolleyError(error)
                            Log.d("Volley", messageError)
                            Toast.makeText(this@SignUp,
                                messageError,
                                Toast.LENGTH_SHORT).show()
                        }
                        progressbar.visibility = View.INVISIBLE
                        mSignUpText.visibility = View.VISIBLE
                    }
                }
            )
        } else
            Toast.makeText(this@SignUp, "Please re-check your data!", Toast.LENGTH_SHORT).show()
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

    fun pickdate(view: View) {
        val myCalendar = Calendar.getInstance()

        val date =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth -> // TODO Auto-generated method stub
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = monthOfYear
                myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                var format = String.format("%02d%02d%02d", dayOfMonth, monthOfYear, year)
                mBirthday?.setText(format)
            }

        DatePickerDialog(
            this@SignUp, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()

    }

    fun showPass(view: View) {
        if (isPasswordVisible) {
            // set drawable image
            mShowPassword.visibility = View.VISIBLE;
            mHidePassword.visibility = View.INVISIBLE;
            // hide Password
            mPassword!!.transformationMethod = PasswordTransformationMethod.getInstance()
            isPasswordVisible = false
        } else {
            // set drawable image
            mShowPassword.visibility = View.INVISIBLE;
            mHidePassword.visibility = View.VISIBLE;
            // show Password
            mPassword!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
            isPasswordVisible = true
        }
    }
    fun showCPass(view: View) {
        if (isCPasswordVisible) {
            // set drawable image
            mShowCPassword.visibility = View.VISIBLE;
            mHideCPassword.visibility = View.INVISIBLE;
            // hide Password
            mCPassword!!.transformationMethod = PasswordTransformationMethod.getInstance()
            isCPasswordVisible = false
        } else {
            // set drawable image
            mShowCPassword.visibility = View.INVISIBLE;
            mHideCPassword.visibility = View.VISIBLE;
            // show Password
            mCPassword!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
            isCPasswordVisible = true
        }
    }

    companion object {
    }
}