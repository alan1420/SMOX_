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
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import org.json.JSONObject

class SignIn : AppCompatActivity() {
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var isPasswordVisible = false

    // [START declare_auth]
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    // [END declare_auth]

    private lateinit var googleSignInClient: GoogleSignInClient

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)

        if(intent.hasExtra("message")) {
            Toast.makeText(this, intent.getStringExtra("message"), Toast.LENGTH_SHORT).show()
        }

        mEmail = findViewById(R.id.signinemail)
        mPassword = findViewById(R.id.signinpass)
        mPassword!!.setOnTouchListener(OnTouchListener { _, event ->
            val right = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= mPassword!!.right - mPassword!!.compoundDrawables[right].bounds.width()) {
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

        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // [END config_signin]

        // [START initialize_auth]
        // Initialize Firebase Auth
        //auth = Firebase.auth
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }
    // [END auth_with_google]

    // [START signin]
    fun signInGoogle(view: View) {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signin]

    fun signInEmail(view: View){
        val email = findViewById<EditText>(R.id.signinemail).text.toString()
        val password = findViewById<EditText>(R.id.signinpass).text.toString()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val nama = user.displayName.toString()
            Toast.makeText(this, "Welcome, $nama", Toast.LENGTH_SHORT).show()
            user.getIdToken(true).addOnSuccessListener {
                loginCheck(it.token.toString())
                Log.d("Token ID", it.token.toString()) // token #1
            }
            Log.d(TAG, "signInWithCredential:success")
        }
    }

    fun signupPage(view: View) {
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
        finish()
    }

    fun loginCheck(token: String) {
        val url = "http://192.168.0.88/smox/public/api/signinCheck"
        val data = JSONObject().put("token", token)
        println(data)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, data,
            { response ->
                //Toast.makeText(this, response.toString(), Toast.LENGTH_SHORT).show()
                //println(response.toString())
                val isRegister = response.getBoolean("is_registered")
                var isCompleted : Boolean? = null
                if (response.has("is_completed"))
                    isCompleted = response.getBoolean("is_completed")
                if (isRegister)
                    if (isCompleted == true)
                        Toast.makeText(this, "Data lengkap", Toast.LENGTH_SHORT).show()
                    else
                        roleSelect()
                else
                    Toast.makeText(this, "Belum terdaftar", Toast.LENGTH_SHORT).show()
                //println("Status register: $data")
            },
            { error ->
                // TODO: Handle error
            }
        )
        val queue = VolleySingleton.getInstance(this.applicationContext).requestQueue
        queue.add(jsonObjectRequest)
    }

    fun roleSelect() {
        Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, ConfirmRole::class.java)
        startActivity(intent)
        finish()
    }


    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}