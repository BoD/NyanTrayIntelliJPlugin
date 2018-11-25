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
import org.jraf.intellijplugin.nyantray.TimeCount.asFormattedDate
import org.jraf.intellijplugin.nyantray.TimeCount.asFormattedDuration
import org.jraf.intellijplugin.nyantray.images.Images
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.JSeparator
import javax.swing.SwingUtilities


@Suppress("EXPERIMENTAL_FEATURE_WARNING")
object NyanWindow {
    private const val MENU_ITEM_OVERALL = "Since %s: %s"
    private const val MENU_ITEM_YEAR = "This year: %s"
    private const val MENU_ITEM_MONTH = "This month: %s"
    private const val MENU_ITEM_WEEK = "This week: %s"
    private const val MENU_ITEM_DAY = "Today: %s"
    private const val MENU_ITEM_ABOUT = "BoD NyanTray v1.1.1 - https://JRAF.org"

    private const val ANIMATION_DELAY_MS = 128L
    private var animationJob: Job? = null

    private val showing = AtomicBoolean(false)

    private val overallMenuItem by lazy {
        JMenuItem(getFormattedTimeCountOverall()).apply {
            isEnabled = false
        }
    }

    private val yearMenuItem by lazy {
        JMenuItem(getFormattedTimeCountYear()).apply {
            isEnabled = false
        }
    }

    private val monthMenuItem by lazy {
        JMenuItem(getFormattedTimeCountMonth()).apply {
            isEnabled = false
        }
    }

    private val weekMenuItem by lazy {
        JMenuItem(getFormattedTimeCountWeek()).apply {
            isEnabled = false
        }
    }

    private val dayMenuItem by lazy {
        JMenuItem(getFormattedTimeCountDay()).apply {
            isEnabled = false
        }
    }

    private val popupMenu by lazy {
        JPopupMenu().apply {
            add(overallMenuItem)
            add(yearMenuItem)
            add(monthMenuItem)
            add(weekMenuItem)
            add(dayMenuItem)
            add(JSeparator())
            add(
                JMenuItem(MENU_ITEM_ABOUT).apply {
                    isEnabled = false
                    addActionListener { }
                }
            )
        }
    }

    private val label by lazy {
        JLabel(ImageIcon(Images.nyanSmall[0]))
    }

    private val frame: AutoFrame by lazy {
        AutoFrame("", this::class.java).apply {
            background = Color(0, 0, 0, 0)
            contentPane.add(label)
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (e.button == MouseEvent.BUTTON3) {
                        popupMenu.show(this@apply, e.x, e.y)
                    }
                }
            })
            pack()
        }
    }

    fun showWindow() {
        if (showing.get()) return
        showing.set(true)
        SwingUtilities.invokeLater {
            updateMenuItems()
            frame.isVisible = true
            animationJob = launch { startTrayIconAnimation() }
        }
    }

    fun hideWindow() {
        if (!showing.get()) return
        showing.set(false)
        animationJob?.cancel()
        SwingUtilities.invokeLater {
            frame.isVisible = false
        }
    }

    private fun updateMenuItems() {
        overallMenuItem.text = getFormattedTimeCountOverall()
        yearMenuItem.text = getFormattedTimeCountYear()
        monthMenuItem.text = getFormattedTimeCountMonth()
        weekMenuItem.text = getFormattedTimeCountWeek()
        dayMenuItem.text = getFormattedTimeCountDay()
    }

    private suspend fun startTrayIconAnimation() {
        while (true) {
            for (i in Images.nyanSmall) {
                label.icon = ImageIcon(i)
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