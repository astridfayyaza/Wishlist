package com.astrid0049.wishlist.util

import com.astrid0049.wishlist.R
import com.astrid0049.wishlist.data.Place

fun pineDays(place: Place): Int {
    val now = System.currentTimeMillis()
    return ((now - place.dateAdded) / (1000L * 60 * 60 * 24)).toInt()
}

fun pineLabelRes(days: Int): Int {
    return when {
        days <= 7 -> R.string.pine_new_crush
        days <= 30 -> R.string.pine_slow_burn
        days <= 90 -> R.string.pine_aching
        else -> R.string.pine_cursed
    }
}

fun relativeDate(timestamp: Long): Pair<Int, Long?> {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val day = 1000L * 60 * 60 * 24

    return when {
        diff < day -> R.string.relative_today to null
        diff < day * 2 -> R.string.relative_yesterday to null
        diff < day * 7 -> R.string.relative_days_ago to (diff / day)
        diff < day * 28 -> R.string.relative_weeks_ago to (diff / (day * 7))
        diff < day * 365 -> R.string.relative_months_ago to (diff / (day * 30))
        else -> R.string.relative_over_year to null
    }
}

fun getCategoryLabelRes(category: String): Int {
    return when (category) {
        "Food" -> R.string.cat_food
        "Nature" -> R.string.cat_nature
        "City" -> R.string.cat_city
        "Culture" -> R.string.cat_culture
        "Hidden Gem" -> R.string.cat_hidden_gem
        else -> R.string.cat_food
    }
}