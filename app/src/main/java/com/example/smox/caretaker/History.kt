package com.example.smox.caretaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.example.smox.R
import kotlinx.android.synthetic.main.history.*

class History : AppCompatActivity()
{

    val myViews : Array<Int> = arrayOf(R.layout.history_fragment1, R.layout.history_fragment2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history)
        val adapter = MyAdapter(myViews, this@History)
        pager.adapter = adapter

        pager.offscreenPageLimit = 2
        dots.attachViewPager(pager)
    }

    class MyAdapter (private val theViews: Array<Int>, private val theContext: Context) : PagerAdapter()
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
    }

    fun gotoHistoryDetailOne(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.HistoryDetailOne::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun gotoHistoryDetailTwo(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.HistoryDetailTwo::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}

