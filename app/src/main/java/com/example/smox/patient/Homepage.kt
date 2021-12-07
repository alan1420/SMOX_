package com.example.smox.patient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import com.android.volley.VolleyError
import kotlinx.android.synthetic.main.p_homepage.*
import com.example.smox.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.json.JSONObject

class Homepage : AppCompatActivity() {
    var jsonLocalData: String? = null

    var firebaseUser: FirebaseUser? = null

    private val buttonClick = AlphaAnimation(1f, 0.7f)
    // [START declare_auth]
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    // [END declare_auth]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_homepage)
        val data = intent.getStringExtra("name")
        //println(data)
        findViewById<TextView>(R.id.sa).text = "HELLO, $data"

        firebaseUser = auth.currentUser

        firebaseUser?.getIdToken(true)?.addOnSuccessListener {
            sendDataGET("get-patient-data", it.token.toString(),this,
                object : VolleyResult {
                    override fun onSuccess(response: JSONObject) {
                        val isFileCreated: Boolean = createFile(this@Homepage,
                            "storage.json", response.toString())
                        //proceed with storing the first todo or show ui
                        if (isFileCreated) {
                            Log.d("Notif", "Data telah tersimpan!")
                            //proceed with storing the first todo or show
                            Toast.makeText(this@Homepage,
                                "Data telah tersimpan!",
                                Toast.LENGTH_SHORT).show()
                        } else {
                            //show error or try again.
                            Toast.makeText(this@Homepage,
                                "Error menyimpan data",
                                Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(error: VolleyError?) {
                        if (error != null) {
                            Log.d("Volley", getVolleyError(error))
                            Toast.makeText(this@Homepage,
                                getVolleyError(error),
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }

    fun gotoSchedule(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
        val intent = Intent(this, Schedule::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun gotoDosage(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
        val intent = Intent(this, Dosage::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun gotoHistory(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
        val intent = Intent(this, History::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun gotoInformation(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
        val intent = Intent(this, Information::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun menu(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
        val intent = Intent(this, Menu::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}