package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.enums.ModeEnum
import club.xiaojiawei.hsscript.bean.isDiscoverCardThread
import club.xiaojiawei.hsscript.bean.isOutCardThread
import club.xiaojiawei.hsscript.bean.single.WarEx
import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.initializer.DRIVER_LOCK
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.status.Mode
import com.sun.jna.platform.win32.WinDef.HWND
import java.awt.Point

/**
 * 鼠标工具类
 * @author 肖嘉威
 * @date 2022/11/24 11:18
 */
object MouseUtil {

    private val mouseMovePauseStep: Int
        get() {
            return ConfigUtil.getInt(ConfigEnum.PAUSE_STEP)
        }

    private val prevPoint = Point(0, 0)

    private fun validatePoint(point: Point?): Boolean {
        return point?.let {
            it.x != -1 && it.y != -1
        } == true
    }

    private fun savePos(pos: Point) {
        prevPoint.x = pos.x
        prevPoint.y = pos.y
    }

    /**
     * 计算斜率
     * @return double 斜率
     */
    private fun calcK(startX: Int, startY: Int, endX: Int, endY: Int): Double {
        return (startY - endY).toDouble() / (startX - endX)
    }

    fun leftButtonClick(hwnd: HWND?) {
        leftButtonClick(prevPoint, hwnd)
    }

    fun leftButtonClick(
        pos: Point,
        hwnd: HWND?,
        mouseMode: Int = ConfigExUtil.getMouseControlMode().code
    ) {
        if (!validateEnv(hwnd)) return
        if (validatePoint(pos)) {
            DRIVER_LOCK.lock()
            try {
                if (!WorkListener.working) return

                if (prevPoint != pos) {
                    CSystemDll.INSTANCE.simulateHumanMoveMouse(
                        prevPoint.x,
                        prevPoint.y,
                        pos.x,
                        pos.y,
                        hwnd,
                        mouseMovePauseStep,
                        mouseMode
                    )
                    SystemUtil.delayShort()
                }
                CSystemDll.INSTANCE.leftClick(pos.x.toLong(), pos.y.toLong(), hwnd, mouseMode)
                savePos(pos)
            } finally {
                DRIVER_LOCK.unlock()
            }
        }
    }

    fun rightButtonClick(hwnd: HWND?) {
        rightButtonClick(prevPoint, hwnd)
    }

    fun rightButtonClick(
        pos: Point,
        hwnd: HWND?,
        mouseMode: Int = ConfigExUtil.getMouseControlMode().code
    ) {
        if (!validateEnv(hwnd)) return

        if (validatePoint(pos)) {
            DRIVER_LOCK.lock()
            try {
                if (!WorkListener.working && Mode.currMode !== ModeEnum.GAMEPLAY) return

                if (prevPoint != pos) {
                    CSystemDll.INSTANCE.simulateHumanMoveMouse(
                        prevPoint.x,
                        prevPoint.y,
                        pos.x,
                        pos.y,
                        hwnd,
                        mouseMovePauseStep,
                        mouseMode
                    )
                    SystemUtil.delayShort()
                }
                CSystemDll.INSTANCE.rightClick(pos.x.toLong(), pos.y.toLong(), hwnd, mouseMode)
                savePos(pos)
            } finally {
                DRIVER_LOCK.unlock()
            }
        }
    }

    fun moveMouseByHuman(endPos: Point, hwnd: HWND?) {
        moveMouseByHuman(null, endPos, hwnd)
    }

    private fun validateEnv(hwnd: HWND?): Boolean {
//        选择卡牌时间只让特定线程执行
        if (WarEx.war.isChooseCardTime && !isDiscoverCardThread()) return false
        hwnd ?: return false
        return !(ConfigUtil.getBoolean(ConfigEnum.DISABLE_MOUSE) ||
                ((isOutCardThread() || isDiscoverCardThread()) && Mode.currMode !== ModeEnum.GAMEPLAY)
                || !WorkListener.working)
    }

    /**
     * 鼠标移动
     */
    fun moveMouseByHuman(
        startPos: Point?,
        endPos: Point,
        hwnd: HWND?,
        mouseMode: Int = ConfigExUtil.getMouseControlMode().code
    ) {
        if (!validateEnv(hwnd)) return

        DRIVER_LOCK.lock()
        try {
            if (!WorkListener.working) return

            val prevPoint = prevPoint
            if (validatePoint(startPos)) {
                startPos!!
                moveMouseByHuman(startPos, hwnd)
                if (validatePoint(endPos)) {
                    SystemUtil.delayShort()
                    if (startPos != endPos) {
                        CSystemDll.INSTANCE.simulateHumanMoveMouse(
                            startPos.x,
                            startPos.y,
                            endPos.x,
                            endPos.y,
                            hwnd,
                            mouseMovePauseStep,
                            mouseMode
                        )
                        savePos(endPos)
                    }
                }
            } else if (validatePoint(prevPoint)) {
                if (prevPoint != endPos) {
                    CSystemDll.INSTANCE.simulateHumanMoveMouse(
                        prevPoint.x,
                        prevPoint.y,
                        endPos.x,
                        endPos.y,
                        hwnd,
                        mouseMovePauseStep,
                        mouseMode
                    )
                    savePos(endPos)
                }
            }
        } finally {
            DRIVER_LOCK.unlock()
        }
    }

}
