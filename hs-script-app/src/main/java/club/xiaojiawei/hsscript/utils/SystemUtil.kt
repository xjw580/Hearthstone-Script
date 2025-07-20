package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.hsscriptbase.bean.LRunnable
import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscript.consts.*
import club.xiaojiawei.hsscript.custom.MouseClickListener
import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.dll.CSystemDll.Companion.MB_ICONERROR
import club.xiaojiawei.hsscript.dll.CSystemDll.Companion.MB_ICONINFORMATION
import club.xiaojiawei.hsscript.dll.CSystemDll.Companion.MB_OK
import club.xiaojiawei.hsscript.dll.CSystemDll.Companion.MB_TOPMOST
import club.xiaojiawei.hsscript.dll.GSystemDll
import club.xiaojiawei.hsscript.dll.User32ExDll
import club.xiaojiawei.hsscript.dll.User32ExDll.Companion.SC_MONITORPOWER
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.RegCommonNameEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.initializer.ServiceInitializer
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.SystemUtil.delay
import club.xiaojiawei.hsscriptbase.util.RandomUtil
import club.xiaojiawei.hsscriptbase.util.isTrue
import com.sun.jna.WString
import com.sun.jna.platform.win32.*
import com.sun.jna.platform.win32.WinUser.*
import java.awt.*
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.io.File
import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.util.*
import java.util.function.Consumer
import kotlin.system.exitProcess

/**
 * 系统工具类
 * @author 肖嘉威
 * @date 2022/11/24 17:21
 */
@Suppress("unused")
object SystemUtil {
    /**
     * 所有鼠标键盘模拟都需要此对象
     */
    @Deprecated("")
    private val ROBOT: Robot by lazy {
        Robot()
    }

    /**
     * 系统托盘
     */
    @Deprecated("")
    private var trayIcon: TrayIcon? = null

    fun getProgramIconFile(): File = Path.of(IMG_PATH, MAIN_IMG_NAME).toFile()

    fun getTrayIconFile(): File = getResouceImgFile(TRAY_IMG_NAME)

    fun getResouceImgFile(name: String): File = Path.of(IMG_PATH, name).toFile()

    /**
     * 调用系统通知
     * @param content
     * @param title
     */
    fun notice(
        content: String,
        title: String = "",
        btnText: String = "",
        btnURL: String = "",
        forceNotify: Boolean = false,
    ) {
        (ConfigUtil.getBoolean(ConfigEnum.SEND_NOTICE) || forceNotify).isTrue {
            Thread.ofVirtual().name("Notice VThread").start(
                LRunnable {
                    val appIDBytes: ByteArray = SCRIPT_NAME.toByteArray(StandardCharsets.UTF_8)
                    val titleBytes = title.toByteArray(StandardCharsets.UTF_8)
                    val msgBytes = content.toByteArray(StandardCharsets.UTF_8)

                    val icoPathBytes: ByteArray =
                        getProgramIconFile()
                            .toPath()
                            .normalize()
                            .toString()
                            .toByteArray(StandardCharsets.UTF_8)
                    val btnTextBytes = btnText.toByteArray(StandardCharsets.UTF_8)
                    val btnURLBytes = btnURL.toByteArray(StandardCharsets.UTF_8)
                    GSystemDll.INSTANCE.notice(
                        appIDBytes,
                        titleBytes,
                        msgBytes,
                        icoPathBytes,
                        btnTextBytes,
                        btnURLBytes,
                        appIDBytes.size,
                        titleBytes.size,
                        msgBytes.size,
                        icoPathBytes.size,
                        btnTextBytes.size,
                        btnURLBytes.size,
                    )
                },
            )
        }
    }

    /**
     * 获取窗口
     * @param windowTitle
     * @return
     */
    fun findHWND(
        className: String? = null,
        windowTitle: String?,
    ): WinDef.HWND? =
        User32ExDll.INSTANCE.FindWindowW(
            if (className == null) null else WString(className),
            if (windowTitle == null) null else WString(windowTitle),
        )

    /**
     * 更新窗口信息
     */
    fun updateRECT(
        hwnd: WinDef.HWND?,
        programRECT: WinDef.RECT?,
    ) {
        if (showWindow(hwnd) && !User32.INSTANCE.GetClientRect(hwnd, programRECT)) {
            log.error { "获取窗口尺寸异常，错误代码：" + Kernel32.INSTANCE.GetLastError() }
        }
    }

    @Deprecated("")
    @Suppress("DEPRECATION")
    fun deleteAllContent() {
        ROBOT.keyPress(KeyEvent.VK_CONTROL)
        sendKey(KeyEvent.VK_A)
        ROBOT.keyRelease(KeyEvent.VK_CONTROL)
        delay(200)
        sendKey(KeyEvent.VK_DELETE)
    }

    @Deprecated("")
    @Suppress("DEPRECATION")
    fun pasteFromClipboard() {
        ROBOT.keyPress(KeyEvent.VK_CONTROL)
        sendKey(KeyEvent.VK_V)
        ROBOT.keyRelease(KeyEvent.VK_CONTROL)
    }

    @Deprecated("")
    fun sendKey(keyCode: Int) {
        ROBOT.keyPress(keyCode)
        ROBOT.keyRelease(keyCode)
    }

    /**
     * 显示窗口
     * @param hwnd
     */
    fun showWindow(hwnd: WinDef.HWND?): Boolean {
        if (hwnd == null || !User32.INSTANCE.IsWindow(hwnd)) return false
        if (User32ExDll.INSTANCE.IsIconic(hwnd)) {
            if (!User32.INSTANCE.ShowWindow(hwnd, SW_RESTORE)) {
                log.error { "显示窗口异常，错误代码：" + Kernel32.INSTANCE.GetLastError() }
                return false
            }
            delay(200)
        }
        return true
    }

    /**
     * 通过浏览器打开链接
     * @param url
     */
    fun openURL(url: String) {
        // 判断桌面是否支持浏览器调用
        if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            // 调用默认浏览器打开网页
            Desktop.getDesktop().browse(URI(url))
        }
    }

    /**
     * 单位毫秒
     * @param delay
     */
    fun delay(delay: Int) {
        Thread.sleep(delay.toLong())
    }

    fun delay(
        minDelay: Int,
        maxDelay: Int,
    ) {
        delay(RandomUtil.getRandom(minDelay, maxDelay))
    }

    fun delayHuman() {
        delay(RandomUtil.getHumanRandom())
    }

    fun delayTiny() {
        delay(RandomUtil.getTinyRandom())
    }

    fun delayShort() {
        delay(RandomUtil.getShortRandom())
    }

    fun delayShortMedium() {
        delay(RandomUtil.getShortMediumRandom())
    }

    fun delayMedium() {
        delay(RandomUtil.getMediumRandom())
    }

    fun delayLong() {
        delay(RandomUtil.getLongRandom())
    }

    fun delayHuge() {
        delay(RandomUtil.getHugeRandom())
    }

    /**
     * 杀死窗口程序
     * @param programHWND
     */
    @Deprecated("由 {@link SystemDll#closeProgram(WinDef.HWND)} 取代")
    fun killProgram(programHWND: WinDef.HWND?) {
        showWindow(programHWND)
        ROBOT.keyPress(18)
        ROBOT.keyPress(115)
        ROBOT.keyRelease(115)
        ROBOT.keyRelease(18)
        log.info { "已关闭程序" }
    }

    /**
     * 添加托盘
     * @param menuItems
     */
    @Deprecated("丑且编码有问题")
    fun addTray(
        mouseClickListener: Consumer<MouseEvent?>?,
        vararg menuItems: MenuItem?,
    ) {
        if (trayIcon != null) {
            return
        }
        if (!SystemTray.isSupported()) {
            log.warn { "当前系统不支持系统托盘" }
            return
        }

        val image = Toolkit.getDefaultToolkit().getImage(getProgramIconFile().toURI().toURL())
        //        托盘右键弹出菜单
        val popupMenu = PopupMenu()
        for (menuItem in menuItems) {
            popupMenu.add(menuItem)
        }
        //        托盘图标
        trayIcon = TrayIcon(image, SCRIPT_NAME, popupMenu)
        trayIcon?.let { tray ->
            tray.setImageAutoSize(true)
            tray.setToolTip(SCRIPT_NAME)
            mouseClickListener?.let { listener ->
                tray.addMouseListener(MouseClickListener(listener))
            }
        }
        SystemTray.getSystemTray().add(trayIcon)
    }

    /**
     * 移除托盘
     */
    @Deprecated("")
    fun removeTray() {
        SystemTray.getSystemTray().remove(trayIcon)
    }

    /**
     * 复制到系统剪切板
     * @param content
     */
    fun copyToClipboard(content: String?): Boolean {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(content), null)
        return true
    }

    /**
     * 获取注册表中用户程序REG_SZ类型的信息
     * @param regCommonNameEnum
     * @param userProgramName
     * @return
     */
    fun registryGetStringValueForUserProgram(
        regCommonNameEnum: RegCommonNameEnum?,
        userProgramName: String,
    ): String? {
        if (regCommonNameEnum == null) {
            return null
        }
        //        64位程序和32位程序
        var path: String?
        return if (!Advapi32Util.registryValueExists(
                WinReg.HKEY_LOCAL_MACHINE,
                "SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\$userProgramName".also {
                    path = it
                },
                regCommonNameEnum.value,
            ) &&
            !Advapi32Util.registryValueExists(
                WinReg.HKEY_LOCAL_MACHINE,
                "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\$userProgramName".also { path = it },
                regCommonNameEnum.value,
            )
        ) {
            null
        } else {
            Advapi32Util.registryGetStringValue(
                WinReg.HKEY_LOCAL_MACHINE,
                path,
                regCommonNameEnum.value,
            )
        }
    }

    /**
     * 关闭本软件
     */
    fun shutdownSoft() {
        runCatching {
            WindowUtil.hideAllStage(true)
        }
        log.info { "准备关闭软件..." }
        runCatching {
            PauseStatus.isPause = true
        }
        runCatching {
            ServiceInitializer().stop()
        }
        exitProcess(0)
    }

    fun message(
        text: String,
        type: Int,
        hwnd: WinDef.HWND? = null,
    ) {
        go {
            CSystemDll.INSTANCE.messageBox(
                hwnd ?: let {
                    WindowUtil.getStage(WindowEnum.MAIN)?.let {
                        User32.INSTANCE.FindWindow(null, it.title)
                    }
                },
                text,
                SCRIPT_NAME,
                type xor MB_TOPMOST,
            )
        }
    }

    fun messageInfoOk(
        text: String,
        type: Int = MB_OK xor MB_ICONINFORMATION,
        hwnd: WinDef.HWND? = null,
    ) {
        message(text, type, hwnd)
    }

    fun messageOk(
        text: String,
        type: Int = MB_OK,
        hwnd: WinDef.HWND? = null,
    ) {
        message(text, type, hwnd)
    }

    fun messageError(
        text: String,
        type: Int = MB_ICONERROR,
        hwnd: WinDef.HWND? = null,
    ) {
        message(text, type, hwnd)
    }

    /**
     * 获取软件的dll文件路径
     */
    fun getDllFilePath(file: ResourceFile): File? =
        if (Objects
                .requireNonNull(javaClass.getResource(""))
                .protocol == "jar"
        ) {
            Path.of(DLL_PATH, file.name).toFile()
        } else {
            loadResource("dll/${file.name}")
        }

    /**
     * 获取软件的exe文件路径
     */
    fun getExeFilePath(file: ResourceFile): File? =
        if (Objects
                .requireNonNull(javaClass.getResource(""))
                .protocol == "jar"
        ) {
            Path.of(ROOT_PATH, file.name).toFile()
        } else {
            loadResource("exe/${file.name}")
        }

    private fun loadResource(path: String): File? {
        var file: File? = null
        javaClass.classLoader.getResource(path)?.let {
            File(it.path).let { f ->
                if (f.exists()) {
                    file = f
                } else {
                    log.error { "未找到${f.absolutePath}" }
                }
            }
        } ?: let {
            log.error { "未找到$path" }
        }
        return file
    }

    /**
     * 打开文件
     */
    @Suppress("DEPRECATION")
    fun openFile(path: String) {
        Runtime.getRuntime().exec(String.format("explorer %s", path))
    }

    fun openFile(file: File) {
        openFile(file.absolutePath)
    }

    /**
     * 关闭显示器
     */
    fun offScreen() {
        User32.INSTANCE.GetForegroundWindow()?.let {
            User32.INSTANCE.SendMessage(it, WM_SYSCOMMAND, WinDef.WPARAM(SC_MONITORPOWER), WinDef.LPARAM(2))
        }
    }

    /**
     * 修改指定窗口的透明度
     * @param hwnd 窗口句柄
     * @param opacity 不透明度
     */
    fun changeWindowOpacity(
        hwnd: WinDef.HWND?,
        opacity: Int,
    ) {
        if (User32.INSTANCE.IsWindow(hwnd)) {
            val windowLong = User32.INSTANCE.GetWindowLong(hwnd, GWL_EXSTYLE)
            if ((windowLong and WS_EX_LAYERED) == 0) {
                User32.INSTANCE.SetWindowLong(hwnd, GWL_EXSTYLE, windowLong xor WS_EX_LAYERED)
            }

            User32.INSTANCE.SetLayeredWindowAttributes(
                hwnd,
                0,
                Math.clamp(opacity.toDouble(), 0.0, 255.0).toInt().toByte(),
                LWA_ALPHA,
            )
        }
    }

    /**
     * 系统关机
     */
    fun shutdownSystem(): Boolean =
        User32.INSTANCE
            .ExitWindowsEx(WinDef.UINT((EWX_SHUTDOWN xor EWX_FORCE).toLong()), WinDef.DWORD(0))
            .booleanValue()

    /**
     * 锁屏
     */
    fun lockScreen(): Boolean = User32.INSTANCE.LockWorkStation().booleanValue()
}
