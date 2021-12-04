package com.example.smox.patient

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.smox.R
import com.github.ybq.android.spinkit.style.Wave
import kotlinx.android.synthetic.main.p_homepage.*

class Homepage : AppCompatActivity() {

    private val buttonClick = AlphaAnimation(1f, 0.7f)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_homepage)
        val data = intent.getStringExtra("name")
        //println(data)
        findViewById<TextView>(R.id.name).text = "HELLO, $data"
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