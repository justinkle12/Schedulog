package com.example.schedulog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulog.databinding.CalendarItemBinding
import timber.log.Timber


/* This class is responsible for holding onto an instance of the view and
 * binding the CalendarItem(calendar_item.xml) to the RecyclerView(fragment_feed.xml). */
class CalendarViewHolder(
    private val binding: CalendarItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(calendarItem: CalendarData) {
        // Bind data to your calendar view (e.g., set selected date)
        binding.calendarView.date = calendarItem.dateInMillis
    }
}

class CalendarAdapter(
    private val calendarData: List<CalendarData>
    ) : RecyclerView.Adapter<CalendarViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CalendarItemBinding.inflate(inflater, parent, false)
        return CalendarViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return calendarData.size
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        Timber.tag("CalendarAdapter").i(calendarData.toString())
        val calendar = calendarData[position]
        holder.bind(calendar)
    }
}
