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
package org.jraf.intellijplugin.nyantray.images

import java.awt.Image
import java.awt.Toolkit

object Images {
    val nyan: Array<Image> by lazy {
        arrayOf(
            getImage("nyan00.png"),
            getImage("nyan01.png"),
            getImage("nyan02.png"),
            getImage("nyan03.png"),
            getImage("nyan04.png"),
            getImage("nyan05.png"),
            getImage("nyan06.png"),
            getImage("nyan07.png"),
            getImage("nyan08.png"),
            getImage("nyan09.png"),
            getImage("nyan10.png"),
            getImage("nyan11.png")
        )
    }

    private fun getImage(imageName: String) = Toolkit.getDefaultToolkit().getImage(Images::class.java.getResource(imageName))
}

val Image.small: Image get() = getScaledInstance(68, 21, Image.SCALE_DEFAULT)