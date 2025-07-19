package club.xiaojiawei.hsscript.dll

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.config.DRIVER_LOCK
import club.xiaojiawei.hsscript.utils.SystemUtil
import com.sun.jna.*
import com.sun.jna.platform.win32.WinDef.HWND
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 系统相关功能
 * @author 肖嘉威
 * @date 2023/9/16 17:34
 */
interface CSystemDll : Library {
    // ===================================mouse===================================

    fun leftClick(
        x: Long,
        y: Long,
        hwnd: HWND?,
        mouseMode: Int,
    )

    fun rightClick(
        x: Long,
        y: Long,
        hwnd: HWND?,
        mouseMode: Int,
    )

    fun simulateHumanMoveMouse(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        hwnd: HWND?,
        pauseStep: Int,
        mouseMode: Int,
    )

    fun clickPlatformLoginBtn(loginPlatformHWND: HWND?)

    fun sendText(
        hwnd: HWND?,
        text: String?,
        append: Boolean,
    )

    fun refreshDriver(): Int

    fun loadDriver(): Int

    fun releaseDriver(): Int

    // ===================================system===================================

    fun quitWindow(hwnd: HWND?)

    fun frontWindow(hwnd: HWND?)

    fun topWindow(
        hwnd: HWND?,
        isTop: Boolean,
    ): Boolean

    fun topWindowForTitle(
        title: String?,
        isTop: Boolean,
    ): Boolean

    fun moveWindow(
        hwnd: HWND?,
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        ignoreSize: Boolean,
    ): Boolean

    fun moveWindowForTitle(
        title: String?,
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        ignoreSize: Boolean,
    ): Boolean

    fun isRunAsAdministrator(): Boolean

    fun messageBox(
        hwnd: HWND?,
        text: String?,
        title: String?,
        type: Int,
    )

    fun findWindowsByProcessName(processName: String?): HWND?

    fun findProcessId(processName: String?): Long

    fun getWindowsProxy(
        proxyUrl: Pointer,
        length: Int,
    )

    fun checkS3Support(): Boolean

    fun enableWakeUpTimer(): Boolean

    fun setWakeUpTimer(seconds: Int): Boolean

    /**
     * 睡眠系统
     */
    fun sleepSystem()

    fun killProcessByName(processName: String)

    fun isProcessRunning(processName: String): Boolean

    fun isDebug(): Boolean

    // ===================================message===================================

    fun limitWindowResize(
        gameHWND: HWND?,
        enable: Boolean,
    )

    fun uninstall()

    fun mouseHook(enable: Boolean)

    fun acHook(enable: Boolean)

    fun limitMouseRange(enable: Boolean)

    fun isConnected(): Boolean

    // ===================================tray===================================

    interface TrayCallback : Callback {
        fun invoke()
    }

    @Structure.FieldOrder("id", "type", "text", "iconPath", "callback")
    open class TrayItem : Structure() {
        @JvmField
        var id: Int = 0

        @JvmField
        var type: Int = 0

        /**
         * 对应wchar_t*
         */
        @JvmField
        var text: Pointer? = null

        /**
         * 对应wchar_t*
         */
        @JvmField
        var iconPath: Pointer? = null

        @JvmField
        var callback: TrayCallback? = null

        // 嵌套类用于指针访问
        class Reference :
            TrayItem(),
            ByReference

        class Value :
            TrayItem(),
            ByValue
    }

    @Structure.FieldOrder("text", "iconPath", "trayItem", "itemCount", "clickCallback")
    open class TrayMenu : Structure() {
        /**
         * 对应wchar_t*
         */
        @JvmField
        var text: Pointer? = null

        @JvmField
        var iconPath: WString? = null

        @JvmField
        var trayItem: TrayItem.Reference? = null

        @JvmField
        var itemCount: Int = 0

        @JvmField
        var clickCallback: TrayCallback? = null

        // 嵌套类用于指针访问
        class Reference :
            TrayMenu(),
            ByReference

        class Value :
            TrayMenu(),
            ByValue
    }

    fun addSystemTray(trayMenu: TrayMenu.Reference?): Boolean

    fun removeSystemTray(): Boolean

    // ex

    companion object {
        const val MB_ICONERROR: Int = 0x00000010

        const val MB_TOPMOST: Int = 0x00040000

        const val MB_OK: Int = 0x00000000

        const val MB_ICONINFORMATION: Int = 0x00000040

        const val MF_STRING: Int = 0x00000000

        const val MF_SEPARATOR: Int = -0x80000000

        const val MF_CHECKED: Int = 0x00000008

        @Synchronized
        fun setWakeUpTimer(seconds: Int): Boolean {
            if (seconds > 0) {
                log.info {
                    "将于[${
                        LocalDateTime.now().plusSeconds(seconds.toLong())
                            .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
                    }]唤醒电脑"
                }
            } else {
                log.info { "取消定时唤醒电脑" }
            }
            return INSTANCE.setWakeUpTimer(seconds)
        }

        fun safeLoadDriver(): Int {
            DRIVER_LOCK.lock()
            try {
                val res = INSTANCE.loadDriver()
                if (res > 0) {
                    log.info { "加载驱动成功" }
                } else if (res < 0) {
                    val text = "加载驱动失败"
                    SystemUtil.notice(text)
                    log.error { text }
                }
                return res
            } finally {
                DRIVER_LOCK.unlock()
            }
        }

        fun safeRefreshDriver(): Int {
            DRIVER_LOCK.lock()
            try {
                val res = INSTANCE.refreshDriver()
                if (res > 0) {
                    log.info { "刷新驱动成功" }
                } else if (res < 0) {
                    val text = "刷新驱动失败"
                    SystemUtil.notice(text)
                    log.error { text }
                }
                return res
            } finally {
                DRIVER_LOCK.unlock()
            }
        }

        fun safeReleaseDriver(): Int {
            DRIVER_LOCK.lock()
            try {
                val res = INSTANCE.releaseDriver()
                if (res > 0) {
                    log.info { "释放驱动成功" }
                } else if (res < 0) {
                    val text = "释放驱动失败"
                    SystemUtil.notice(text)
                    log.error { text }
                }
                return res
            } finally {
                DRIVER_LOCK.unlock()
            }
        }

        val INSTANCE: CSystemDll by lazy {
            Native.load("dll/csystem", CSystemDll::class.java)
        }
    }
}
