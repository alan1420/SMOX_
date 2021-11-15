package com.example.smox
import android.os.Bundle
import androidx.fragment.app.*
import androidx.viewpager.widget.ViewPager

private const val NUM_PAGES = 2

class MainActivity : FragmentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirm)
    }
}