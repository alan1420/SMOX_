package com.example.smox.caretaker

import java.time.*
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
import com.google.firebase.auth.GetTokenResult
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

class Set : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    var patientId by Delegates.notNull<Int>()

    lateinit var medicine: EditText
    lateinit var interval: EditText
    lateinit var dosage: EditText
    lateinit var instruction: EditText
    lateinit var starttime: EditText
    lateinit var period: EditText
    lateinit var periodType: AutoCompleteTextView

    var pData: String? = ""
    var indexArr: Int? = null
    var slot: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_set)

        medicine = findViewById(R.id.medicine_name)
        interval = findViewById(R.id.interval)
        dosage = findViewById(R.id.dosage)
        instruction = findViewById(R.id.instruction)
        starttime = findViewById(R.id.starttime)
        period = findViewById(R.id.period)
        periodType = findViewById(R.id.autoCompleteTextView)

        if (intent.hasExtra("slot_data")) {
            val data = intent.getByteArrayExtra("slot_data")
            indexArr = intent.getIntExtra("index", 99)
            val data2 = JSONObject(String(data!!))
            medicine.setText(data2.get("medicine_name").toString())
            interval.setText(data2.get("interval").toString())
            dosage.setText(data2.get("dosage").toString())
            instruction.setText(data2.get("instruction").toString())
            starttime.setText(data2.get("start_time").toString())
            period.setText(data2.get("period").toString())
            periodType.setText(data2.get("period_type").toString())
            patientId = data2.get("patient_id").toString().toInt()
            intent.removeExtra("slot_data")
        }

        slot = intent.getIntExtra("slot", 99)

        pData = readFile(this, "storage.json")
        if (pData != null || pData != "{}") {
            val jsonData = Gson().fromJson(pData, JsonObject::class.java)
            if (jsonData.has("patient_data")) {
                val userData = jsonData.get("patient_data").asJsonObject
                patientId = userData.get("id").asInt
            }
        }

        findViewById<TextView>(R.id.slot).text = "SLOT $slot"

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

    fun getTime(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val formatted = current.format(formatter)
        starttime.setText(formatted)
    }

    fun save(view: View) {
        val medicine = findViewById<EditText>(R.id.medicine_name).text.toString()
        val interval = findViewById<EditText>(R.id.interval).text.toString()
        val dosage = findViewById<EditText>(R.id.dosage).text.toString()
        val instruction = findViewById<EditText>(R.id.instruction).text.toString()
        val starttime = findViewById<EditText>(R.id.starttime).text.toString()
        val period = findViewById<EditText>(R.id.period).text.toString()
        val periodType = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView).text.toString()

        if (textIsNotEmpty(medicine) && textIsNotEmpty(interval) && textIsNotEmpty(dosage) && textIsNotEmpty(instruction)
            && textIsNotEmpty(period)
        ) {
            getToken(this, object: TokenResult {
                override fun onSuccess(token: String) {
                    val data = JSONObject()
                    data.put("patient_id", patientId)
                    data.put("medicine_name", medicine)
                    data.put("start_time", starttime)
                    data.put("interval", interval.toInt())
                    data.put("dosage", dosage.toInt())
                    data.put("instruction", instruction)
                    data.put("period", period.toInt())
                    data.put("period_type", periodType)
                    data.put("slot", slot!!)
                    sendDataPOST("add-medicine", token, data, this@Set,
                        object : VolleyResult {
                            override fun onSuccess(response: JSONObject) {
                                if (pData != null) {
                                    val JSONData = JSONObject(pData)
                                    if (JSONData.has("medicine_list")) {
                                        val arr1 = JSONData.getJSONArray("medicine_list")
                                        if (indexArr != null)
                                            arr1.remove(indexArr!!)
                                        arr1.put(response)
                                    } else {
                                        val satu = JSONArray().put(response)
                                        JSONData.put("medicine_list", satu)
                                    }
                                   createFile(this@Set,
                                        "storage.json", JSONData.toString())
                                }
                                Toast.makeText(this@Set, "Changes saved", Toast.LENGTH_SHORT).show()
                                this@Set.onBackPressed()
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                            }
                            override fun onError(error: VolleyError?) {
                                if (error != null) {
                                    Log.d("Volley", getVolleyError(error))
                                    Toast.makeText(this@Set,
                                        getVolleyError(error),
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            })
        } else {
            Toast.makeText(this, "Please check your data!", Toast.LENGTH_SHORT).show()
        }
    }

    fun markAsDone(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
        if (pData != null && indexArr != null) {
            val JSONData = JSONObject(pData)
            if (JSONData.has("medicine_list")) {
                val arr1 = JSONData.getJSONArray("medicine_list")
                var id = arr1.getJSONObject(indexArr!!).getInt("id")
                getToken(this, object: TokenResult {
                    override fun onSuccess(token: String) {
                        sendDataGET("delete-medicine/$id", token, this@Set,
                            object : VolleyResult {
                                override fun onSuccess(response: JSONObject) {
                                    Toast.makeText(this@Set,
                                        "Data berhasil dihapus!",
                                        Toast.LENGTH_SHORT).show()
                                    arr1.remove(indexArr!!)
                                    val isCreated = createFile(this@Set,
                                        "storage.json", JSONData.toString())
                                    if (isCreated) {
                                        finish()
                                        this@Set.onBackPressed()
                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                                    }

                                }
                                override fun onError(error: VolleyError?) {
                                    if (error != null)
                                    Toast.makeText(this@Set,
                                        getVolleyError(error),
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                })
            }
        } else {
            //Error
            Toast.makeText(this@Set,
                "Tidak dapat menghapus karena data tidak ada di database!",
                Toast.LENGTH_SHORT).show()
        }
    }

}