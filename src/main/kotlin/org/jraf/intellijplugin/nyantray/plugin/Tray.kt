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

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jraf.intellijplugin.nyantray.VERSION
import org.jraf.intellijplugin.nyantray.images.Images
import org.jraf.intellijplugin.nyantray.util.asFormattedDate
import org.jraf.intellijplugin.nyantray.util.asFormattedDuration
import java.awt.CheckboxMenuItem
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.ItemEvent
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.SwingUtilities

object Tray {
    private const val MENU_ITEM_OVERALL = "Since %s: %s"
    private const val MENU_ITEM_YEAR = "This year: %s"
    private const val MENU_ITEM_MONTH = "This month: %s"
    private const val MENU_ITEM_WEEK = "This week: %s"
    private const val MENU_ITEM_DAY = "Today: %s"
    private const val MENU_ITEM_INFO = "(1d = 1 work day = 8 hours)"
    private const val MENU_ITEM_STAY_VISIBLE = "Stay visible"
    private const val MENU_ITEM_ABOUT = "BoD NyanTray $VERSION - https://JRAF.org"

    private const val ANIMATION_DELAY_MS = 128L
    private var animationJob: Job? = null

    private val showing = AtomicBoolean(false)
    private var needAdding = AtomicBoolean(true)

    private fun disabledMenuItem(label: String) = MenuItem(label).apply {
        isEnabled = false
    }

    private val overallMenuItem by lazy {
        disabledMenuItem(getFormattedTimeCountOverall())
    }

    private val yearMenuItem by lazy {
        disabledMenuItem(getFormattedTimeCountYear())
    }

    private val monthMenuItem by lazy {
        disabledMenuItem(getFormattedTimeCountMonth())
    }
    private val weekMenuItem by lazy {
        disabledMenuItem(getFormattedTimeCountWeek())
    }
    private val dayMenuItem by lazy {
        disabledMenuItem(getFormattedTimeCountDay())
    }

    private val stayVisibleMenuItem by lazy {
        CheckboxMenuItem(
            MENU_ITEM_STAY_VISIBLE,
            PersistedState.loadState()?.stayVisible == true,
        ).apply {
            addItemListener {
                if (it.stateChange == ItemEvent.SELECTED) {
                    PersistedState.persistStayVisible(true)
                } else {
                    PersistedState.persistStayVisible(false)
                    if (!showing.get()) {
                        removeIconFromSystemTray()
                    }
                }
            }
        }
    }

    private val trayIcon: TrayIcon by lazy {
        TrayIcon(
            Images.nyan[0],
            MENU_ITEM_ABOUT,
            PopupMenu().apply {
                add(overallMenuItem)
                add(yearMenuItem)
                add(monthMenuItem)
                add(weekMenuItem)
                add(dayMenuItem)
                add(MenuItem("-"))
                add(stayVisibleMenuItem)
                add(MenuItem("-"))
                add(
                    MenuItem(MENU_ITEM_INFO).apply {
                        isEnabled = false
                    }
                )
                add(
                    MenuItem(MENU_ITEM_ABOUT).apply {
                        isEnabled = false
                    }
                )
            }
        )
    }

    fun showIcon() {
        if (showing.get()) return
        if (!SystemTray.isSupported()) return
        showing.set(true)
        SwingUtilities.invokeLater {
            updateMenuItems()
            if (needAdding.get()) {
                needAdding.set(false)
                SystemTray.getSystemTray().add(trayIcon)
            }
            animationJob = GlobalScope.launch { startTrayIconAnimation() }
        }
    }

    fun hideIcon() {
        if (!showing.get()) return
        if (!SystemTray.isSupported()) return
        showing.set(false)
        animationJob?.cancel()
        SwingUtilities.invokeLater {
            if (PersistedState.loadState()?.stayVisible == true) {
                trayIcon.image = Images.idle
            } else {
                removeIconFromSystemTray()
            }
        }
    }

    private fun removeIconFromSystemTray() {
        SystemTray.getSystemTray().remove(trayIcon)
        needAdding.set(true)
    }

    private fun updateMenuItems() {
        overallMenuItem.label = getFormattedTimeCountOverall()
        yearMenuItem.label = getFormattedTimeCountYear()
        monthMenuItem.label = getFormattedTimeCountMonth()
        weekMenuItem.label = getFormattedTimeCountWeek()
        dayMenuItem.label = getFormattedTimeCountDay()
    }

    private suspend fun startTrayIconAnimation() {
        while (true) {
            for (i in Images.nyan) {
                trayIcon.image = i
                delay(ANIMATION_DELAY_MS)
            }
        }
    }

    private fun getFormattedTimeCountOverall() =
        MENU_ITEM_OVERALL.format(TimeCount.firstUse.asFormattedDate(), TimeCount.countedTimeOverall.asFormattedDuration())

    private fun getFormattedTimeCountYear() = MENU_ITEM_YEAR.format(TimeCount.countedTimeYear.asFormattedDuration())
    private fun getFormattedTimeCountMonth() = MENU_ITEM_MONTH.format(TimeCount.countedTimeMonth.asFormattedDuration())
    private fun getFormattedTimeCountWeek() = MENU_ITEM_WEEK.format(TimeCount.countedTimeWeek.asFormattedDuration())
    private fun getFormattedTimeCountDay() = MENU_ITEM_DAY.format(TimeCount.countedTimeDay.asFormattedDuration())
}

fun main() {
    while (true) {
//        // See https://github.com/dyorgio/macos-tray-icon-fixer
//        System.setProperty("apple.awt.enableTemplateImages", "false")
//        Tray.showIcon()
//        Thread.sleep(5000)
//        Tray.hideIcon()
//        Thread.sleep(5000)

        System.setProperty("apple.awt.enableTemplateImages", "true")
        Tray.showIcon()
        Thread.sleep(5000)
        Tray.hideIcon()
        Thread.sleep(5000)
    }
}
