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

import java.awt.Point
import java.awt.Window
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.prefs.Preferences
import javax.swing.JFrame

class AutoFrame(title: String, clazz: Class<*>) : JFrame(title) {
    private val prefs: Preferences = Preferences.userNodeForPackage(clazz).node(clazz.simpleName)

    init {
        addComponentListener(object : ComponentAdapter() {
            override fun componentMoved(componentEvent: ComponentEvent) {
                prefs.apply {
                    put("x", "$x")
                    put("y", "$y")
                }
            }
        })

        isUndecorated = true
        type = Window.Type.UTILITY
        isAlwaysOnTop = true

        val frameDragListener = FrameDragListener(this)
        addMouseListener(frameDragListener)
        addMouseMotionListener(frameDragListener)
    }

    override fun pack() {
        val x = prefs.get("x", "320").toInt()
        val y = prefs.get("y", "240").toInt()
        super.pack()
        setBounds(x, y, width, height)
    }

    private class FrameDragListener(private val frame: JFrame) : MouseAdapter() {
        private var origin: Point? = null

        override fun mouseReleased(e: MouseEvent) {
            origin = null
        }

        override fun mousePressed(e: MouseEvent) {
            origin = e.point
        }

        override fun mouseDragged(e: MouseEvent) {
            val newLocation = e.locationOnScreen
            frame.setLocation(newLocation.x - origin!!.x, newLocation.y - origin!!.y)
        }
    }
}