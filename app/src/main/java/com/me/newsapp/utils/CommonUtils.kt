package com.me.newsapp.utils

import java.text.SimpleDateFormat
import java.util.*

class CommonUtils {
    companion object {
        fun formatDate(date: Date): String {
            val format = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
            return format.format(date)
        }
    }
}