package club.xiaojiawei.hsscript.bean

import club.xiaojiawei.hsscript.consts.GameRationConst
import club.xiaojiawei.hsscript.consts.ScriptStaticData
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.RandomUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import java.awt.Point
import java.util.ArrayList
import java.util.function.Consumer

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/8/28 15:15
 */
data class GameRect(val left: Double, val right: Double, val top: Double, val bottom: Double) {

    fun getClickPos(): Point {
        val realH: Int = ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top
        val usableH = realH
        val realW: Int = ScriptStaticData.GAME_RECT.right - ScriptStaticData.GAME_RECT.left
        val usableW = (realH * GameRationConst.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO).toInt()
        val middleX = realW shr 1
        val middleY = realH shr 1
        val pointX: Double = RandomUtil.getRandom(left, right)
        val pointY: Double = RandomUtil.getRandom(top, bottom)
        return Point((middleX + pointX * usableW).toInt(), (middleY + pointY * usableH).toInt())
    }

    private fun cancel() {
        GameUtil.cancelAction()
        SystemUtil.delayTiny()
    }

    fun isValid(): Boolean {
        return this != INVALID
    }

    @JvmOverloads
    fun lClick(isCancel: Boolean = true) {
        if (isCancel) cancel()
        GameUtil.leftButtonClick(getClickPos())
    }

    fun rClick() {
        GameUtil.rightButtonClick(getClickPos())
    }

    @JvmOverloads
    fun lClickMoveLClick(endRect: GameRect?, isCancel: Boolean = true) {
        if (endRect == null) {
            return
        }
        if (isCancel) cancel()
        val startPos = getClickPos()
        val endPos = endRect.getClickPos()
        GameUtil.leftButtonClick(startPos)
        SystemUtil.delay(100)
        GameUtil.moveMouse(startPos, endPos)
        SystemUtil.delay(100)
        GameUtil.leftButtonClick(endPos)
    }

    fun move() {
        GameUtil.moveMouse(getClickPos())
    }

    fun move(endRect: GameRect?) {
        if (endRect == null) {
            move()
        } else {
            GameUtil.moveMouse(getClickPos(), endRect.getClickPos())
        }
    }

    fun buildAction(): Action {
        return Action(this)
    }

    class Action constructor(private val rect: GameRect) {

        private val runnableList: MutableList<Runnable?> = ArrayList<Runnable?>()

        private var lastRect: GameRect? = null

        fun clear(): Action {
            runnableList.clear()
            lastRect = null
            return this
        }

        fun exec(): Action {
            runnableList.forEach(Consumer { obj: Runnable? -> obj!!.run() })
            return this
        }

        fun lClick(): Action {
            runnableList.add(Runnable { rect.lClick() })
            return this
        }

        fun lClick(rect: GameRect?): Action {
            runnableList.add(Runnable {
                if (rect == null) {
                    if (lastRect != null) {
                        lastRect!!.lClick()
                    }
                } else {
                    rect.lClick()
                    lastRect = rect
                }
            })
            return this
        }

        fun rClick(): Action {
            runnableList.add(Runnable { rect.rClick() })
            return this
        }

        fun rClick(rect: GameRect?): Action {
            runnableList.add(Runnable {
                if (rect == null) {
                    if (lastRect != null) {
                        lastRect!!.rClick()
                    }
                } else {
                    rect.rClick()
                    lastRect = rect
                }
            })
            return this
        }

        fun move(): Action {
            runnableList.add(Runnable { rect.move() })
            return this
        }

        fun move(endRect: GameRect?): Action {
            runnableList.add(Runnable { rect.move(endRect) })
            return this
        }
    }

    companion object {
        val INVALID: GameRect = GameRect(0.0, 0.0, 0.0, 0.0)
    }

}
