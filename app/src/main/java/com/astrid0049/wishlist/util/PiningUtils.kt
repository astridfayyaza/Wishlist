package com.astrid0049.wishlist.util

import com.astrid0049.wishlist.data.Place

fun pineDays(place: Place): Int {
    val now = System.currentTimeMillis()
    return ((now - place.dateAdded) / (1000L * 60 * 60 * 24)).toInt()
}

fun pineLabel(days: Int): String {
    return when {
        days <= 7 -> "New crush"
        days <= 30 -> "Slow burn"
        days <= 90 -> "Aching for it"
        else -> "Cursed to wait"
    }
}

fun relativeDate(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val day = 1000L * 60 * 60 * 24

    return when {
        diff < day -> "Today"
        diff < day * 2 -> "Yesterday"
        diff < day * 7 -> "${diff / day} days ago"
        diff < day * 28 -> "${diff / (day * 7)} weeks ago"
        diff < day * 365 -> "${diff / (day * 30)} months ago"
        else -> "Over a year ago"
    }
}