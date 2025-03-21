package club.xiaojiawei.hsscript.dll

import club.xiaojiawei.config.log
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

    fun leftClick(x: Long, y: Long, hwnd: HWND?, mouseMode: Int)

    fun rightClick(x: Long, y: Long, hwnd: HWND?, mouseMode: Int)

    fun simulateHumanMoveMouse(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        hwnd: HWND?,
        pauseStep: Int,
        mouseMode: Int,
    )

    fun quitWindow(hwnd: HWND?)

    fun frontWindow(hwnd: HWND?)

    fun clickPlatformLoginBtn(loginPlatformHWND: HWND?)

    fun sendText(hwnd: HWND?, text: String?, append: Boolean)

    fun topWindow(hwnd: HWND?, isTop: Boolean): Boolean

    fun topWindowForTitle(title: String?, isTop: Boolean): Boolean

    fun moveWindow(hwnd: HWND?, x: Int, y: Int, w: Int, h: Int, ignoreSize: Boolean): Boolean

    fun moveWindowForTitle(title: String?, x: Int, y: Int, w: Int, h: Int, ignoreSize: Boolean): Boolean

    fun uninstallInjectDll(hwnd: HWND?)

    /**
     * 是否禁用输入
     * @param hwnd
     * @param disable
     */
    fun changeInput(hwnd: HWND?, disable: Boolean)

    /**
     * 是否禁用窗口调整
     * @param hwnd
     * @param disable
     */
    fun changeWindow(hwnd: HWND?, disable: Boolean)

    fun isRunAsAdministrator(): Boolean

    fun messageBox(hwnd: HWND?, text: String?, title: String?, type: Int)

    fun findWindowsByProcessName(processName: String?): HWND?

    fun findProcessId(processName: String?): Long

    fun getWindowsProxy(proxyUrl: Pointer, length: Int)

    fun checkS3Support(): Boolean

    fun enableWakeUpTimer(): Boolean

    fun setWakeUpTimer(seconds: Int): Boolean

    /**
     * 睡眠系统
     */
    fun sleepSystem()

    fun killProcessByName(processName: String)

    fun isProcessRunning(processName: String): Boolean


    interface TrayCallback : Callback {
        fun invoke()
    }

    @Structure.FieldOrder("id", "type", "text", "callback")
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
        @JvmField
        var callback: TrayCallback? = null

        // 嵌套类用于指针访问
        class Reference : TrayItem(), ByReference
        class Value : TrayItem(), ByValue
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
        class Reference : TrayMenu(), ByReference
        class Value : TrayMenu(), ByValue
    }

    fun addSystemTray(trayMenu: TrayMenu.Reference?): Boolean

    fun removeSystemTray(): Boolean

    companion object {

        val INSTANCE: CSystemDll by lazy {
//            Native.load("dll/csystem", CSystemDll::class.java)
            Native.load("S:\\CLionProjects\\hs-script-csystem\\cmake-build-release-visual-studio\\csystem.dll", CSystemDll::class.java)
        }

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
                    "设置[${
                        LocalDateTime.now().plusSeconds(seconds.toLong())
                            .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
                    }]定时唤醒电脑"
                }
            } else {
                log.info { "取消定时唤醒电脑" }
            }
            return INSTANCE.setWakeUpTimer(seconds)
        }
    }
}