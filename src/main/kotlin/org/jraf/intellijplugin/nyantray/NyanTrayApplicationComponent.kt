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

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.util.ProgressWindow
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class NyanTrayApplicationComponent : ApplicationComponent {
    companion object {
        private const val COMPONENT_NAME = "NyanTray"

        private val LOGGER = Logger.getInstance(NyanTrayApplicationComponent::class.java)

        private const val LOOP_DELAY_MS = 1500L
    }

    private val progressWindows = mutableSetOf<ProgressWindow>()
    private var loopJob: Job? = null
    private var progressOngoing: Boolean = false

    override fun getComponentName() = COMPONENT_NAME

    override fun initComponent() {
        val messageBusConnection = ApplicationManager.getApplication().messageBus.connect()
        messageBusConnection.subscribe(ProgressWindow.TOPIC, ProgressWindow.Listener { progressWindow ->
            synchronized(progressWindows) {
                progressWindows += progressWindow
            }
        })

        loopJob = launch {
            loop()
        }
    }

    override fun disposeComponent() {
        loopJob?.cancel()
    }

    private suspend fun loop() {
        while (true) {
            synchronized(progressWindows) {
                val i = progressWindows.iterator()
                while (i.hasNext()) {
                    val progressWindow = i.next()
                    if (!progressWindow.isRunning) {
                        i.remove()
                    }
                }

                if (progressWindows.isEmpty()) {
                    if (progressOngoing) {
                        progressOngoing = false
                        onProgressOngoing(false)
                    }
                } else {
                    if (!progressOngoing) {
                        progressOngoing = true
                        onProgressOngoing(true)
                    }
                }
            }

            delay(LOOP_DELAY_MS)
        }
    }

    private fun onProgressOngoing(progressOngoing: Boolean) {
        if (progressOngoing) {
            TimeCount.startCountingTime()
            Tray.showIcon()
        } else {
            TimeCount.stopCountingTime()
            Tray.hideIcon()
        }
    }

    private fun log(s: String) {
        LOGGER.info(s)
        println(s)
    }
}
