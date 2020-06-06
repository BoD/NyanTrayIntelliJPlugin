/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2018 Benoit 'BoD' Lubek (BoD@JRAF.org)
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

package org.jraf.intellijplugin.nyantray

import org.jraf.intellijplugin.nyantray.util.asFormattedDuration
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

class MillisecondTest {
    @Test
    fun `test format duration`() {
        assertEquals("0s", 0L.asFormattedDuration())
        assertEquals("30s", TimeUnit.SECONDS.toMillis(30).asFormattedDuration())
        assertEquals("1m", TimeUnit.MINUTES.toMillis(1).asFormattedDuration())
        assertEquals("2m", TimeUnit.MINUTES.toMillis(2).asFormattedDuration())
        assertEquals("1h", TimeUnit.HOURS.toMillis(1).asFormattedDuration())
        assertEquals(
            "1h30m", (TimeUnit.HOURS.toMillis(1) +
                    TimeUnit.MINUTES.toMillis(30)).asFormattedDuration()
        )
        assertEquals("2h", TimeUnit.HOURS.toMillis(2).asFormattedDuration())
        assertEquals("7h", TimeUnit.HOURS.toMillis(7).asFormattedDuration())
        assertEquals(
            "7h59m", (TimeUnit.HOURS.toMillis(7) +
                    TimeUnit.MINUTES.toMillis(59)).asFormattedDuration()
        )

        assertEquals("1d", TimeUnit.HOURS.toMillis(8).asFormattedDuration())
        assertEquals("1d1h", TimeUnit.HOURS.toMillis(9).asFormattedDuration())
        assertEquals("1d7h", TimeUnit.HOURS.toMillis(15).asFormattedDuration())
        assertEquals("2d", TimeUnit.HOURS.toMillis(16).asFormattedDuration())

        assertEquals("3d", TimeUnit.DAYS.toMillis(1).asFormattedDuration())

        assertEquals("1d30m", (TimeUnit.HOURS.toMillis(8) + TimeUnit.MINUTES.toMillis(30)).asFormattedDuration())

        assertEquals(
            "3d5h30m", (TimeUnit.DAYS.toMillis(1) +
                    TimeUnit.HOURS.toMillis(5) +
                    TimeUnit.MINUTES.toMillis(30)).asFormattedDuration()
        )
    }
}
