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

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
object Tray {
    private const val INFO = "BoD NyanTray v1.0.4 - https://JRAF.org"
    private const val ANIMATION_DELAY_MS = 128L
    private var animationJob: Job? = null

    @Volatile
    private var showing = false

    private val trayIcon: TrayIcon by lazy {
        TrayIcon(
            Images.nyan[0],
            INFO,
            PopupMenu().apply {
                add(
                    MenuItem(INFO).apply {
                        isEnabled = false
                        addActionListener { }
                    }
                )
            }
        )
    }

    fun showIcon() {
        if (!SystemTray.isSupported()) return
        if (showing) return
        showing = true
        SystemTray.getSystemTray().add(trayIcon)
        animationJob = launch { startTrayIconAnimation() }
    }

    fun hideIcon() {
        if (!SystemTray.isSupported()) return
        if (!showing) return
        showing = false
        animationJob?.cancel()
        SystemTray.getSystemTray().remove(trayIcon)
    }

    private suspend fun startTrayIconAnimation() {
        while (true) {
            for (i in Images.nyan) {
                trayIcon.image = i
                delay(ANIMATION_DELAY_MS)
            }
        }
    }
}