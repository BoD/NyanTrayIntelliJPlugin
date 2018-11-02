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
import com.intellij.openapi.progress.TaskInfo
import com.intellij.openapi.progress.util.AbstractProgressIndicatorBase
import com.intellij.openapi.progress.util.ProgressWindow
import com.intellij.openapi.wm.ex.ProgressIndicatorEx

class NyanTrayApplicationComponent : ApplicationComponent {
    companion object {
        private const val COMPONENT_NAME = "NyanTray"
    }

    private var progress = 0
        set(value) {
            field = value
            if (field == 0) {
                Tray.hideIcon()
            } else {
                Tray.showIcon()
            }
        }

    override fun getComponentName() = COMPONENT_NAME

    override fun initComponent() {
        val messageBusConnection = ApplicationManager.getApplication().messageBus.connect()
        messageBusConnection.subscribe(ProgressWindow.TOPIC, ProgressWindow.Listener { progressWindow ->
            progress++
            progressWindow.addStateDelegate(ProgressWindowDelegate())
        })
    }

    private inner class ProgressWindowDelegate : AbstractProgressIndicatorBase(), ProgressIndicatorEx {
        override fun addStateDelegate(delegate: ProgressIndicatorEx) = Unit
        override fun isFinished(task: TaskInfo) = true
        override fun wasStarted() = false
        override fun processFinish() = Unit

        override fun finish(task: TaskInfo) {
            progress--
        }
    }
}