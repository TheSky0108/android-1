/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tools.idea.naveditor.scene.draw

import com.android.tools.adtui.common.SwingCoordinate
import com.android.tools.idea.common.scene.SceneContext
import icons.StudioIcons.NavEditor.Surface

import javax.swing.*
import java.awt.*

/**
 * [DrawIcon] is a DrawCommand that draws an icon
 * in the specified rectangle.
 */
class DrawIcon(@SwingCoordinate private val myRectangle: Rectangle, private val myIconType: IconType) : NavBaseDrawCommand() {
  enum class IconType {
    START_DESTINATION,
    DEEPLINK
  }

  private val myIcon: Icon =
    when (myIconType) {
      DrawIcon.IconType.START_DESTINATION -> Surface.START_DESTINATION
      DrawIcon.IconType.DEEPLINK -> Surface.DEEPLINK
      else -> throw IllegalArgumentException()
    }

  private constructor(sp: Array<String>) : this(NavBaseDrawCommand.stringToRect(sp[2]), IconType.valueOf(sp[3]))

  constructor(s: String) : this(parse(s))

  override fun getLevel(): Int {
    return NavBaseDrawCommand.DRAW_ICON
  }

  override fun getProperties(): Array<Any> {
    return arrayOf(NavBaseDrawCommand.rectToString(myRectangle), myIconType)
  }

  override fun paint(g: Graphics2D, sceneContext: SceneContext) {
    val scaleX = myRectangle.width.toDouble() / myIcon.iconWidth
    val scaleY = myRectangle.height.toDouble() / myIcon.iconHeight

    val g2 = g.create() as Graphics2D

    g2.scale(scaleX, scaleY)
    myIcon.paintIcon(null, g2, (myRectangle.x / scaleX).toInt(), (myRectangle.y / scaleY).toInt())

    g2.dispose()
  }

  companion object {
    private fun parse(s: String): Array<String> {
      val sp = s.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
      when {
        sp.size == 4 -> return sp
        else -> throw IllegalArgumentException()
      }
    }
  }
}