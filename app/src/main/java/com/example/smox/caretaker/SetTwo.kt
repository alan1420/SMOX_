package com.example.smox.caretaker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.android.volley.VolleyError
import com.example.smox.*
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

class SetTwo : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    var patientId by Delegates.notNull<Int>()

    lateinit var medicine: EditText
    lateinit var interval: EditText
    lateinit var dosage: EditText
    lateinit var instruction: EditText
    lateinit var period: EditText
    lateinit var periodType: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_set)

        medicine = findViewById(R.id.medicine_name)
        interval = findViewById(R.id.interval)
        dosage = findViewById(R.id.dosage)
        instruction = findViewById(R.id.instruction)
        period = findViewById(R.id.period)
        periodType = findViewById(R.id.autoCompleteTextView)

        if (intent.hasExtra("slot_data")) {
            val data = intent.getByteArrayExtra("slot_data")
            val data2 = JSONObject(String(data!!))
            println(data2)
            medicine.setText(data2.get("medicine_name").toString())
            interval.setText(data2.get("interval").toString())
            dosage.setText(data2.get("dosage").toString())
            instruction.setText(data2.get("instruction").toString())
            period.setText(data2.get("period").toString())
            periodType.setText(data2.get("period_type").toString())
        }

        val pData = readFile(this, "storage.json")
        if (pData != null) {
            val jsonData = Gson().fromJson(pData, JsonObject::class.java)
            if (jsonData.has("patient_data")) {
                val userData = jsonData.get("patient_data").asJsonObject
                patientId = userData.get("id").asInt
                //println(userData)
            }
        }

        findViewById<TextView>(R.id.slot).text = "SLOT 2"

        // get reference to the string array that we just created
        val languages = resources.getStringArray(R.array.period_list)
        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_list, languages)
        // get reference to the autocomplete text view
        val autocompleteTV = periodType
        // set adapter to the autocomplete tv to the arrayAdapter
        autocompleteTV.setAdapter(arrayAdapter)
    }

    fun backtoHome(view: View) {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
    }

    fun save(view: View) {
        val medicine = findViewById<EditText>(R.id.medicine_name).text.toString()
        val interval = findViewById<EditText>(R.id.interval).text.toString()
        val dosage = findViewById<EditText>(R.id.dosage).text.toString()
        val instruction = findViewById<EditText>(R.id.instruction).text.toString()
        val period = findViewById<EditText>(R.id.period).text.toString()
        val periodType = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView).text.toString()
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val formatted = current.format(formatter)

        if (textIsEmpty(medicine) && textIsEmpty(interval) && textIsEmpty(dosage) && textIsEmpty(instruction)
            && textIsEmpty(period)
        ) {
            auth.currentUser?.getIdToken(true)?.addOnSuccessListener {
                val data = JSONObject()
                data.put("patient_id", patientId)
                data.put("medicine_name", medicine)
                data.put("start_time", formatted)
                data.put("interval", interval.toInt())
                data.put("dosage", dosage.toInt())
                data.put("instruction", instruction)
                data.put("period", period.toInt())
                data.put("period_type", periodType)
                data.put("slot", 2)
                println(data)
                sendDataPOST("add-medicine", it.token.toString(), data, this.applicationContext,
                    object : VolleyResult {
                        override fun onSuccess(response: JSONObject) {
                            Toast.makeText(this@SetTwo, "Changes saved", Toast.LENGTH_SHORT).show()
                            this@SetTwo.onBackPressed()
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                        }
                        override fun onError(error: VolleyError?) {
                            if (error != null) {
                                Log.d("Volley", getVolleyError(error))
                                Toast.makeText(this@SetTwo,
                                    getVolleyError(error),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
        } else {
            Toast.makeText(this, "Please check your data!", Toast.LENGTH_SHORT).show()
        }
    }
}