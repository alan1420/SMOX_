package com.example.smox

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.util.Log
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.widget.*
import com.android.volley.VolleyError
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.github.ybq.android.spinkit.style.Wave
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import org.json.JSONObject


class SignIn : AppCompatActivity() {
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private lateinit var mShowPassword: ImageView
    private lateinit var mHidePassword: ImageView
    private var isPasswordVisible = false
    private lateinit var progressbar: ProgressBar
    private lateinit var wave: Wave
    private lateinit var mSignInText: TextView
    var firebaseUser: FirebaseUser? = null

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        firebaseUser = firebaseAuth.currentUser
        updateUI(firebaseUser)
    }

    // [START declare_auth]
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    // [END declare_auth]

    private lateinit var googleSignInClient: GoogleSignInClient

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)

        mSignInText = findViewById(R.id.textSignIn)
        progressbar = findViewById(R.id.spin_kit)
        wave = Wave()
        progressbar.indeterminateDrawable = wave
        progressbar.visibility = View.INVISIBLE;

        if(intent.hasExtra("message")) {
            Toast.makeText(this, intent.getStringExtra("message"), Toast.LENGTH_SHORT).show()
        }

        mEmail = findViewById(R.id.signinemail)
        mPassword = findViewById(R.id.signinpass)
        mEmail = findViewById(R.id.enteremail)
        mShowPassword = findViewById(R.id.showpassword)
        mHidePassword = findViewById(R.id.hidepassword)


        mShowPassword.visibility = View.VISIBLE;
        mHidePassword.visibility = View.INVISIBLE;

        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // [END config_signin]

    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        auth.addAuthStateListener(this.authStateListener)
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
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }
    // [END auth_with_google]

    // [START signin]
    fun signInGoogle(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signin]

    fun signInEmail(view: View){
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)

        progressbar.visibility = View.VISIBLE;
        mSignInText.visibility = View.INVISIBLE;

        val email = findViewById<EditText>(R.id.signinemail).text.toString()
        val password = findViewById<EditText>(R.id.signinpass).text.toString()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    progressbar.visibility = View.INVISIBLE;
                    mSignInText.visibility = View.VISIBLE;
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val name = user.displayName.toString()
            user.getIdToken(true).addOnSuccessListener {
                Log.d("Token ID", it.token.toString()) // token #1
                sendDataGET("signinCheck", it.token.toString(), this.applicationContext,
                    object : VolleyResult {
                        override fun onSuccess(response: JSONObject) {
                            if (!response.has("error")) {
                                val isRegister = response.getBoolean("is_registered")
                                var isCompleted : Boolean? = null
                                if (response.has("is_completed"))
                                    isCompleted = response.getBoolean("is_completed")
                                if (isRegister)
                                    if (isCompleted == true) {
                                        Toast.makeText(this@SignIn, "Data lengkap", Toast.LENGTH_SHORT).show()
                                        toHomePage(this@SignIn, response.getString("role").toInt(), response.getString("last_name"))
                                    }
                                    else
                                        roleSelect()
                            } else {
                                Toast.makeText(this@SignIn, "Please fill the data!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@SignIn, SignUp::class.java)
                                intent.putExtra("fullname", name)
                                intent.putExtra("uuid", user.uid)
                                startActivity(intent)
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        }

                        override fun onError(error: VolleyError?) {
                            if (error != null) {
                                Log.d("Volley", getVolleyError(error))
                                Toast.makeText(this@SignIn,
                                    getVolleyError(error),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
            Log.d(TAG, "signInWithCredential:success")
        }
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

    fun signupPage(view: View) {
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun roleSelect() {
        Toast.makeText(this, "Silahkan Pilih Peran", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, ConfirmRole::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(this.authStateListener)
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}

