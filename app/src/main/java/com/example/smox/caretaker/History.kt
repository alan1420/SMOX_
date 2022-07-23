package com.example.smox.caretaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.example.smox.R
import com.example.smox.readFile
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.history.*

class History : AppCompatActivity()
{
    val myViews : Array<Int> = arrayOf(R.layout.history_fragment1, R.layout.history_fragment2)

    var userData: JsonArray? = null
    var slot1Data: JsonObject? = null
    var slot2Data: JsonObject? = null
    var history1: JsonArray? = null
    var history2: JsonArray? = null
    var count1 = "-"
    var count2 = "-"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history)

        val dataPatient = readFile(this, "storage.json")
        if (dataPatient != null) {
            val jsonData = Gson().fromJson(dataPatient, JsonObject::class.java)
            if (jsonData.has("medicine_list")) {
                userData = jsonData.get("medicine_list").asJsonArray
                if (userData != null) {
                    for (i in 0 until userData!!.size()) {
                        val item = userData!!.get(i).asJsonObject
                        val slot = item.get("slot").asInt
                        if (slot == 1)
                            slot1Data = item
                        else if (slot == 2)
                            slot2Data = item
                    }
                }
            }
            if (jsonData.has("medicine_history")){
                if (jsonData.get("medicine_history").asJsonObject.has("history1"))
                    history1 = jsonData.get("medicine_history").asJsonObject
                                .get("history1").asJsonArray
                    count1 = history1?.size().toString()
                if (jsonData.get("medicine_history").asJsonObject.has("history2"))
                    history2 = jsonData.get("medicine_history").asJsonObject
                                .get("history2").asJsonArray
                    count2 = history2?.size().toString()
            }
        }

        val adapter = MyAdapter(myViews, this@History)
        pager.adapter = adapter

        pager.offscreenPageLimit = 2
        dots.attachViewPager(pager)
    }

    inner class MyAdapter (private val theViews: Array<Int>, private val theContext: Context) : PagerAdapter()
    {

        override fun isViewFromObject(view: View, `object`: Any): Boolean
        {
            return view === `object`
        }

        override fun getCount(): Int
        {
            return theViews.size
        }

        override fun instantiateItem(container: ViewGroup,
                                     position: Int): Any
        {
            val thisView = theViews[position]
            val inflater = LayoutInflater.from(theContext)

            val layout = inflater.inflate(thisView, container, false) as ViewGroup

            if (position == 0 && slot1Data != null) {
                val period = slot1Data!!.get("period").asString + " " + slot1Data!!.get("period_type").asString.toUpperCase()
                layout.findViewWithTag<TextView>("name1").text = slot1Data!!.get("medicine_name").asString.toUpperCase()
                layout.findViewWithTag<TextView>("period1").text = period
                layout.findViewWithTag<TextView>("days1").text = count1
            } else if (position == 1 && slot2Data != null) {
                val period = slot2Data!!.get("period").asString + " " + slot2Data!!.get("period_type").asString.toUpperCase()
                layout.findViewWithTag<TextView>("name2").text = slot2Data!!.get("medicine_name").asString.toUpperCase()
                layout.findViewWithTag<TextView>("period2").text = period
                layout.findViewWithTag<TextView>("days2").text = count2
            }

            container.addView(layout)
            return layout
        }

        override fun destroyItem(container: ViewGroup,
                                 position: Int,
                                 view: Any)
        {
            container.removeView(view as View)
        }
    }

    fun backtoHome(view: View) {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
    }
}

