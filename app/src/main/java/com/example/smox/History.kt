package com.example.smox

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.p_history.*

class History : AppCompatActivity()
{
    var viewPager: ViewPager? = null
    var sliderDotspanel: LinearLayout? = null
    private var dotscount = 0
    private lateinit var dots: Array<ImageView>

    val myViews : Array<Int> = arrayOf(R.layout.p_history_fragment1, R.layout.p_history_fragment2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_history)
        pager.adapter = MyAdapter(myViews, this@History)
    }
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

