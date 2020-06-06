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

package org.jraf.intellijplugin.nyantray.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

typealias Millisecond = Long
typealias Timestamp = Long

private val dateTimeFormatter: DateTimeFormatter by lazy {
    var pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null, IsoChronology.INSTANCE, Locale.getDefault())
    pattern = pattern.replace("yy", "yyyy");
    DateTimeFormatter.ofPattern(pattern)
}

fun Millisecond.asFormattedDuration(): String {
    if (this < TimeUnit.MINUTES.toMillis(1)) {
        return "${this / TimeUnit.SECONDS.toMillis(1)}s"
    }
    if (this < TimeUnit.HOURS.toMillis(1)) {
        return "${this / TimeUnit.MINUTES.toMillis(1)}m"
    }
    if (this < TimeUnit.HOURS.toMillis(8)) {
        val remainingMinutesInMs = this % TimeUnit.HOURS.toMillis(1)
        val remainingMinutes = remainingMinutesInMs / TimeUnit.MINUTES.toMillis(1)
        val remainingMinutesStr = if (remainingMinutes == 0L) "" else "${remainingMinutes}m"
        return "${this / TimeUnit.HOURS.toMillis(1)}h$remainingMinutesStr"
    }

    val workingDays = this / TimeUnit.HOURS.toMillis(8)
    val remainingHoursInMs = this % TimeUnit.HOURS.toMillis(8)
    val remainingHours = remainingHoursInMs / TimeUnit.HOURS.toMillis(1)
    val remainingHoursStr = if (remainingHours == 0L) "" else "${remainingHours}h"

    val remainingMinutesInMs = this % TimeUnit.HOURS.toMillis(1)
    val remainingMinutes = remainingMinutesInMs / TimeUnit.MINUTES.toMillis(1)
    val remainingMinutesStr = if (remainingMinutes == 0L) "" else "${remainingMinutes}m"

    return "${workingDays}d$remainingHoursStr$remainingMinutesStr"
}

fun Timestamp.asFormattedDate(): String {
    return dateTimeFormatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(this), TimeZone.getDefault().toZoneId()))
}
