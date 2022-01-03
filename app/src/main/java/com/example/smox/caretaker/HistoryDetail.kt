package com.example.smox.caretaker

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.smox.R
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import kotlinx.android.synthetic.main.history_detail.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth


class HistoryDetail : AppCompatActivity() {
    private var selectedDate: LocalDate? = null
    private val selectedDates = mutableSetOf<LocalDate>()
    private val today = LocalDate.now()
    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_detail)

        findViewById<TextView>(R.id.achievement).text = "-"

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        calendarView.setup(firstMonth, lastMonth, DayOfWeek.MONDAY)

        if (intent.hasExtra("history")) {
            val history = Gson().fromJson(intent.getStringExtra("history"), JsonArray::class.java)
            if (history.size() > 0) {
                startDate = LocalDate.parse(history[0].asString)
                endDate = LocalDate.parse(history[history.size()-1].asString)
                findViewById<TextView>(R.id.achievement).text = history.size().toString()
                calendarView.scrollToDate(LocalDate.parse(history[0].asString))
            }
        } else
            calendarView.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = view.findViewById<TextView>(R.id.calendarDayText)
            init {
                view.setOnClickListener {
                    // Check the day owner as we do not want to select in or out dates.
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDates.contains(day.date)) {
                            selectedDates.remove(day.date)
                        } else {
                            selectedDates.add(day.date)
                        }
                        calendarView.notifyDayChanged(day)
                    }
                }
            }
        }
        class MonthViewContainer(view: View) : ViewContainer(view) {
            val textView = view.findViewById<TextView>(R.id.headerTextView)
        }

        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                textView.text = day.date.dayOfMonth.toString()
                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.setTextColor(Color.BLACK)
                    when {
                        selectedDates.contains(day.date) -> {
                            textView.setTextColor(Color.parseColor("#3A284C"))
                            textView.setBackgroundResource(R.drawable.selection_background)
                        }
                        startDate != null && endDate != null && (day.date >= startDate && day.date <= endDate) -> {
                            textView.setTextColor(Color.parseColor("#3A284C"))
                            textView.setBackgroundResource(R.drawable.selection_background)
                        }
                        day.date == today -> {
                            textView.setTextColor(Color.parseColor("#D9FFFFFF"))
                            textView.setBackgroundResource(R.drawable.selection_today)
                        }
                        else -> {
                            textView.setTextColor(Color.BLACK)
                            textView.background = null
                        }
                    }
                } else {
                    textView.setTextColor(Color.GRAY)
                    textView.background = null
                }
            }
        }

        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                container.textView.text = "${month.yearMonth.month.name.toLowerCase().capitalize()} ${month.year}"
            }
        }
    }

    fun back(view: View) {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
    }
}