/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2022 Benoit 'BoD' Lubek (BoD@JRAF.org)
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

data class PersistedState(
    val firstUse: Timestamp,
    val dayOfYear: Int,
    val weekOfYear: Int,
    val month: Int,
    val year: Int,
    val countedTimeDay: Millisecond,
    val countedTimeWeek: Millisecond,
    val countedTimeMonth: Millisecond,
    val countedTimeYear: Millisecond,
    val countedTimeOverall: Millisecond,
    val stayVisible: Boolean?,
) {
    companion object {
        private val persistedStateFile by lazy { File(System.getProperty("user.home"), ".nyantray") }
        private val gson by lazy { Gson() }

        fun persistState(
            firstUse: Timestamp,
            dayOfYear: Int,
            weekOfYear: Int,
            month: Int,
            year: Int,
            countedTimeDay: Millisecond,
            countedTimeWeek: Millisecond,
            countedTimeMonth: Millisecond,
            countedTimeYear: Millisecond,
            countedTimeOverall: Millisecond,
        ) {
            val state = loadState()?.copy(
                firstUse = firstUse,
                dayOfYear = dayOfYear,
                weekOfYear = weekOfYear,
                month = month,
                year = year,
                countedTimeDay = countedTimeDay,
                countedTimeWeek = countedTimeWeek,
                countedTimeMonth = countedTimeMonth,
                countedTimeYear = countedTimeYear,
                countedTimeOverall = countedTimeOverall,
            ) ?: PersistedState(
                firstUse = firstUse,
                dayOfYear = dayOfYear,
                weekOfYear = weekOfYear,
                month = month,
                year = year,
                countedTimeDay = countedTimeDay,
                countedTimeWeek = countedTimeWeek,
                countedTimeMonth = countedTimeMonth,
                countedTimeYear = countedTimeYear,
                countedTimeOverall = countedTimeOverall,
                stayVisible = false,
            )
            persistState(state)
        }

        private fun persistState(state: PersistedState) {
            persistedStateFile.writeText(gson.toJson(state))
        }

        fun loadState(): PersistedState? {
            if (!persistedStateFile.exists()) return null
            return gson.fromJson(persistedStateFile.readText(), PersistedState::class.java)
        }

        fun persistStayVisible(stayVisible: Boolean) {
            val state = loadState() ?: return
            persistState(
                state.copy(stayVisible = stayVisible),
            )
        }
    }
}
