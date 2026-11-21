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

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.progress.util.ProgressWindow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Suppress("DEPRECATION")
class NyanTrayApplicationComponent : ApplicationComponent {
  companion object {
    private const val COMPONENT_NAME = "NyanTray"

    private val LOOP_DELAY = 2000.milliseconds
  }

  private val progressWindows = mutableSetOf<ProgressWindow>()
  private var loopJob: Job? = null
  private var progressOngoing: Boolean = false
  private val nyanClient = NyanClient()

  override fun getComponentName() = COMPONENT_NAME

  @Deprecated("Deprecated in Java")
  override fun initComponent() {
    val messageBusConnection = ApplicationManager.getApplication().messageBus.connect()
    messageBusConnection.subscribe(
      ProgressWindow.TOPIC,
      ProgressWindow.Listener { progressWindow ->
        synchronized(progressWindows) {
          progressWindows += progressWindow
        }
      },
    )

    loopJob = pluginCoroutineScope.launch {
      loop()
    }
  }

  @Deprecated("Deprecated in Java")
  override fun disposeComponent() {
    loopJob?.cancel()
  }

  private suspend fun loop() {
    while (true) {
      var isEmpty = false
      synchronized(progressWindows) {
        val i = progressWindows.iterator()
        while (i.hasNext()) {
          val progressWindow = i.next()
          if (!progressWindow.isRunning) {
            i.remove()
          }
        }
        isEmpty = progressWindows.isEmpty()
      }

      if (isEmpty) {
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
      delay(LOOP_DELAY)
    }
  }

  private suspend fun onProgressOngoing(progressOngoing: Boolean) {
    if (progressOngoing) {
      TimeCount.startCountingTime()
      if (!nyanClient.setBusy()) {
        Tray.showIcon()
      }
    } else {
      TimeCount.stopCountingTime()
      if (!nyanClient.setIdle()) {
        Tray.hideIcon()
      }
    }
  }
}
