package com.example.weatherapp.ui.main

import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class DayChipManager(private val chipGroup: ChipGroup) {

    fun setupChips(uniqueDays: List<LocalDate>, onDateSelected: (LocalDate) -> Unit) {
        chipGroup.removeAllViews()
        val context = chipGroup.context

        uniqueDays.forEachIndexed { index, date ->
            val chip = Chip(context).apply {
                text = when (index) {
                    0 -> "Today"
                    else -> DateTimeFormatter.ofPattern("EEE, d MMM", Locale("en")).format(date)
                }
                isCheckable = true
                tag = date

                setOnClickListener {
                    onDateSelected(it.tag as LocalDate)
                }
            }
            chipGroup.addView(chip)

            //Default First Button Selected
            if (index == 0) {
                chip.isChecked = true
            }
        }
    }
}