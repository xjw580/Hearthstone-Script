package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.hsscript.dll.SystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.status.PauseStatus
import com.sun.jna.platform.win32.WinDef.HWND
import java.awt.Point
import kotlin.math.abs

/**
 * 鼠标工具类
 * @author 肖嘉威
 * @date 2022/11/24 11:18
 */
object MouseUtil {

    /**
     * 鼠标每次移动后的最小间隔时间：ms
     */
    private const val MIN_MOVE_INTERVAL = 1

    private val MAX_MOVE_INTERVAL = 4

    /**
     * 鼠标每次移动的距离：px
     */
    private const val MOVE_DISTANCE = 10

    private val lastPoint = Point(-1, -1)

    private fun validPoint(point: Point?): Boolean {
        return point?.let {
            it.x != -1 && it.y != -1
        } == true
    }

    private fun savePos(pos: Point) {
        lastPoint.x = pos.x
        lastPoint.y = pos.y
    }

    /**
     * 计算斜率
     * @return double 斜率
     */
    private fun calcK(startX: Int, startY: Int, endX: Int, endY: Int): Double {
        return (startY - endY).toDouble() / (startX - endX)
    }

    fun leftButtonClick(hwnd: HWND?) {
        leftButtonClick(lastPoint, hwnd)
    }

    fun leftButtonClick(pos: Point, hwnd: HWND?) {
        hwnd?:return
        if (!PauseStatus.isPause && validPoint(pos)) {
            SystemDll.INSTANCE.leftClick(pos.x.toLong(), pos.y.toLong(), hwnd)
            savePos(pos)
        }
    }

    fun rightButtonClick(hwnd: HWND?) {
        rightButtonClick(lastPoint, hwnd)
    }

    fun rightButtonClick(pos: Point, hwnd: HWND?) {
        hwnd?:return
        if (!PauseStatus.isPause && validPoint(pos)) {
            SystemDll.INSTANCE.rightClick(pos.x.toLong(), pos.y.toLong(), hwnd)
            savePos(pos)
        }
    }


    fun moveMouseByLine(endPos: Point, hwnd: HWND?) {
        moveMouseByLine(null, endPos, hwnd)
    }

    /**
     * 鼠标按照直线方式移动
     */
    fun moveMouseByLine(startPos: Point?, endPos: Point, hwnd: HWND?) {
        hwnd?:return
        if (!PauseStatus.isPause && validPoint(endPos)) {
            val endX = endPos.x
            val endY = endPos.y
            if (validPoint(startPos)) {
                var startX = startPos!!.x
                var startY = startPos.y
                if (abs((startY - endY).toDouble()) <= 5) {
                    startX -= MOVE_DISTANCE
                    while (startX >= endX && !PauseStatus.isPause) {
                        SystemDll.INSTANCE.moveMouse(startX.toLong(), startY.toLong(), hwnd)
                        SystemUtil.delay(MIN_MOVE_INTERVAL, MAX_MOVE_INTERVAL)
                        startX -= MOVE_DISTANCE
                    }
                } else if (abs((startX - endX).toDouble()) <= 5) {
                    startY -= MOVE_DISTANCE
                    while (startY >= endY && !PauseStatus.isPause) {
                        SystemDll.INSTANCE.moveMouse(startX.toLong(), startY.toLong(), hwnd)
                        SystemUtil.delay(MIN_MOVE_INTERVAL, MAX_MOVE_INTERVAL)
                        startY -= MOVE_DISTANCE
                    }
                } else {
                    val k = calcK(startX, startY, endX, endY)
                    val b = startY - k * startX
                    startY -= MOVE_DISTANCE
                    while (startY >= endY && !PauseStatus.isPause) {
                        val x = ((startY - b) / k).toInt()
                        SystemDll.INSTANCE.moveMouse(x.toLong(), startY.toLong(), hwnd)
                        SystemUtil.delay(MIN_MOVE_INTERVAL, MAX_MOVE_INTERVAL)
                        startY -= MOVE_DISTANCE
                    }
                }
            }
            SystemDll.INSTANCE.moveMouse(endX.toLong(), endY.toLong(), hwnd)
            savePos(endPos)
        }
    }

}
