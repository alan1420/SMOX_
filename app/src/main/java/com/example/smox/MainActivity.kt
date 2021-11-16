package com.example.smox

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    var mPassword: EditText? = null
    var isPasswordVisible = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_sign_in)
        mPassword = findViewById<View>(R.id.signinpass) as EditText
        mPassword!!.setOnTouchListener(OnTouchListener { v, event ->
            val RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= mPassword!!.right - mPassword!!.compoundDrawables[RIGHT].bounds.width()) {
                    val selection = mPassword!!.selectionEnd
                    if (isPasswordVisible) {
                        // set drawable image
                        mPassword!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide, 0)
                        // hide Password
                        mPassword!!.transformationMethod = PasswordTransformationMethod.getInstance()
                        isPasswordVisible = false
                    } else {
                        // set drawable image
                        mPassword!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show, 0)
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
    }

}