/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2020 Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jraf.intellijplugin.nyantray.plugin

import com.google.gson.Gson
import org.jraf.intellijplugin.nyantray.util.Millisecond
import org.jraf.intellijplugin.nyantray.util.Timestamp
import java.io.File
import java.util.Calendar

object TimeCount {
    private data class PersistedState(
        val firstUse: Timestamp,
        val dayOfYear: Int,
        val weekOfYear: Int,
        val month: Int,
        val year: Int,
        val countedTimeDay: Millisecond,
        val countedTimeWeek: Millisecond,
        val countedTimeMonth: Millisecond,
        val countedTimeYear: Millisecond,
        val countedTimeOverall: Millisecond
    )

    var firstUse: Timestamp = System.currentTimeMillis()
    private var countingStartTime: Timestamp? = null
    private var dayOfYear: Int = getDayOfYear()
    private var weekOfYear: Int = getWeekOfYear()
    private var month: Int = getMonth()
    private var year: Int = getYear()

    var countedTimeDay: Millisecond = 0
    var countedTimeWeek: Millisecond = 0
    var countedTimeMonth: Millisecond = 0
    var countedTimeYear: Millisecond = 0
    var countedTimeOverall: Millisecond = 0

    private val persistedStateFile by lazy { File(System.getProperty("user.home"), ".nyantray") }
    private val gson by lazy { Gson() }

    init {
        loadState()
        resetTimeCountIfNeeded()
    }

    private fun addCountedTime(countedTime: Millisecond) {
        resetTimeCountIfNeeded()

        countedTimeDay += countedTime
        countedTimeWeek += countedTime
        countedTimeMonth += countedTime
        countedTimeYear += countedTime
        countedTimeOverall += countedTime
    }

    fun startCountingTime() {
        if (countingStartTime != null) return
        countingStartTime = System.currentTimeMillis()
    }

    fun stopCountingTime() {
        countingStartTime.let {
            if (it == null) return
            addCountedTime(System.currentTimeMillis() - it)
        }
        countingStartTime = null
        persistState()
    }

    private fun resetTimeCountIfNeeded() {
        val currentDayOfYear = getDayOfYear()
        if (dayOfYear != currentDayOfYear) {
            dayOfYear = currentDayOfYear
            countedTimeDay = 0
        }

        val currentWeekOfYear = getWeekOfYear()
        if (weekOfYear != currentWeekOfYear) {
            weekOfYear = currentWeekOfYear
            countedTimeWeek = 0
        }

        val currentMonth = getMonth()
        if (month != currentMonth) {
            month = currentMonth
            countedTimeMonth = 0
        }

        val currentYear = getYear()
        if (year != currentYear) {
            year = currentYear
            countedTimeYear = 0
        }
    }

    private fun getDayOfYear() = Calendar.getInstance()[Calendar.DAY_OF_YEAR]
    private fun getWeekOfYear() = Calendar.getInstance()[Calendar.WEEK_OF_YEAR]
    private fun getMonth() = Calendar.getInstance()[Calendar.MONTH]
    private fun getYear() = Calendar.getInstance()[Calendar.YEAR]

    private fun persistState() {
        persistedStateFile.writeText(
            gson.toJson(
                PersistedState(
                    firstUse = firstUse,
                    dayOfYear = dayOfYear,
                    weekOfYear = weekOfYear,
                    month = month,
                    year = year,
                    countedTimeDay = countedTimeDay,
                    countedTimeWeek = countedTimeWeek,
                    countedTimeMonth = countedTimeMonth,
                    countedTimeYear = countedTimeYear,
                    countedTimeOverall = countedTimeOverall
                )
            )
        )
    }

    private fun loadState() {
        if (!persistedStateFile.exists()) return
        val persistedState = gson.fromJson(persistedStateFile.readText(), PersistedState::class.java)
        firstUse = persistedState.firstUse
        dayOfYear = persistedState.dayOfYear
        weekOfYear = persistedState.weekOfYear
        month = persistedState.month
        year = persistedState.year
        countedTimeDay = persistedState.countedTimeDay
        countedTimeWeek = persistedState.countedTimeWeek
        countedTimeMonth = persistedState.countedTimeMonth
        countedTimeYear = persistedState.countedTimeYear
        countedTimeOverall = persistedState.countedTimeOverall
    }
}
