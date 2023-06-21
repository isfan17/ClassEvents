package com.isfan17.classevents.utils

import android.widget.Toast
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

object Helper {

    // VALID EMAIL FORM VALIDATION
    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    // SIMPLE TOAST EXTENSION FUNCTION FOR FRAGMENTS
    fun Fragment.toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    // TIME 00:00 STRING FORMAT GENERATOR FROM INTEGER
    fun generateTimeText(pickedHour: Int, pickedMinute: Int): String {
        val hour = if (pickedHour < 10) {
            "0$pickedHour"
        } else {
            pickedHour.toString()
        }

        val minute = if (pickedMinute < 10) {
            "0$pickedMinute"
        } else {
            pickedMinute.toString()
        }

        return "$hour:$minute"
    }

    // CONVERT DAY TO NUMBER TO STORE IN LOCAL DB FOR SORTING PURPOSES
    fun convertDayToNumber(day: String): Int {
        return when (day) {
            "Monday" -> 1
            "Tuesday" -> 2
            "Wednesday" -> 3
            "Thursday" -> 4
            "Friday" -> 5
            "Saturday" -> 6
            "Sunday" -> 7
            else -> -1 // Handle unknown day names
        }
    }

    // CONVERT NUMBER TO DAY NAME
    fun convertNumberToDay(number: Int): String {
        return when (number) {
            1 -> "Monday"
            2 -> "Tuesday"
            3 -> "Wednesday"
            4 -> "Thursday"
            5 -> "Friday"
            6 -> "Saturday"
            7 -> "Sunday"
            else -> "" // Handle unknown numbers
        }
    }

    // CONVERT DATE TO NUMBER (FOR SORTING PURPOSES)
    fun convertStringToSortableDate(dateString: String): Long {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = dateFormat.parse(dateString)
        return date?.time ?: 0
    }

    // CONVERT NUMBER TO DATE (FOR SORTING PURPOSES)
    fun convertSortableDateToString(sortableDate: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = Date(sortableDate)
        return dateFormat.format(date)
    }
}