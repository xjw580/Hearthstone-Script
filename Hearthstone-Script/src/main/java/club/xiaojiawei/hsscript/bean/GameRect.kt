package club.xiaojiawei.hsscript.bean

import club.xiaojiawei.hsscript.data.GAME_RECT
import club.xiaojiawei.hsscript.data.GameRationConst
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.util.RandomUtil
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
        val realH: Int = GAME_RECT.bottom - GAME_RECT.top
        val usableH = realH
        val realW: Int = GAME_RECT.right - GAME_RECT.left
        val usableW = (realH * GameRationConst.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO).toInt()
        val middleX = realW shr 1
        val middleY = realH shr 1
        val pointX: Int = RandomUtil.getRandom((left * usableW).toInt(), (right * usableW).toInt())
        val pointY: Int = RandomUtil.getRandom((top * usableH).toInt(), (bottom * usableH).toInt())
        return Point(middleX + pointX, middleY + pointY)
    }

    private fun cancel() {
        GameUtil.cancelAction()
        SystemUtil.delayTiny()
    }

    fun isValid(): Boolean {
        return this != INVALID
    }

    /**
     * @param isCancel 执行前先右键取消
     */
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
        SystemUtil.delay(80, 140)
        GameUtil.moveMouse(startPos, endPos)
        SystemUtil.delay(60, 120)
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

    fun buildAction(): GameRectAction {
        return GameRectAction(this)
    }

    class GameRectAction(private val rect: GameRect) {

        private val runnableList: MutableList<Runnable?> = ArrayList<Runnable?>()

        private var lastRect: GameRect? = null

        fun clear(): GameRectAction {
            runnableList.clear()
            lastRect = null
            return this
        }

        fun exec(): GameRectAction {
            runnableList.forEach(Consumer { obj: Runnable? -> obj!!.run() })
            return this
        }

        fun lClick(): GameRectAction {
            runnableList.add(Runnable { rect.lClick() })
            return this
        }

        fun lClick(rect: GameRect?): GameRectAction {
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

        fun rClick(): GameRectAction {
            runnableList.add(Runnable { rect.rClick() })
            return this
        }

        fun rClick(rect: GameRect?): GameRectAction {
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

        fun move(): GameRectAction {
            runnableList.add(Runnable { rect.move() })
            return this
        }

        fun move(endRect: GameRect?): GameRectAction {
            runnableList.add(Runnable { rect.move(endRect) })
            return this
        }
    }

    companion object {
        val INVALID: GameRect = GameRect(0.0, 0.0, 0.0, 0.0)
    }

}
