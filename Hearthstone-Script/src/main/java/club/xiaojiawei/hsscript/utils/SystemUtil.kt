package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.bean.LRunnable
import club.xiaojiawei.config.VIRTUAL_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.custom.MouseClickListener
import club.xiaojiawei.hsscript.data.*
import club.xiaojiawei.hsscript.dll.NoticeDll
import club.xiaojiawei.hsscript.dll.SystemDll
import club.xiaojiawei.hsscript.dll.SystemDll.Companion.MB_ICONERROR
import club.xiaojiawei.hsscript.dll.SystemDll.Companion.MB_ICONINFORMATION
import club.xiaojiawei.hsscript.dll.SystemDll.Companion.MB_OK
import club.xiaojiawei.hsscript.dll.User32ExDll.Companion.SC_MONITORPOWER
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.RegCommonNameEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.util.RandomUtil
import club.xiaojiawei.util.isTrue
import com.sun.jna.WString
import com.sun.jna.platform.win32.*
import com.sun.jna.platform.win32.WinUser.WM_SYSCOMMAND
import javafx.application.Platform
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
     * 系统托盘
     */
    private var trayIcon: TrayIcon? = null

    fun getProgramIconFile(): File {
        return Path.of(IMG_PATH, MAIN_IMG_NAME).toFile()
    }

    /**
     * 调用系统通知
     * @param content
     * @param title
     */
    fun notice(content: String, title: String = "", btnText: String = "", btnURL: String = "") {
        ConfigUtil.getBoolean(ConfigEnum.SEND_NOTICE).isTrue {
            Thread.ofVirtual().name("Notice VThread").start(LRunnable {
//        trayIcon.displayMessage(title, content, TrayIcon.MessageType.NONE);
                val appIDBytes: ByteArray = SCRIPT_NAME.toByteArray(StandardCharsets.UTF_8)
                val titleBytes = title.toByteArray(StandardCharsets.UTF_8)
                val msgBytes = content.toByteArray(StandardCharsets.UTF_8)

                val jarPath = File(
                    SystemUtil.javaClass.getProtectionDomain()
                        .codeSource
                        .location
                        .toURI()
                )

                val icoPathBytes: ByteArray =
                    getProgramIconFile().toPath().normalize().toString().toByteArray(StandardCharsets.UTF_8)
                val btnTextBytes = btnText.toByteArray(StandardCharsets.UTF_8)
                val btnURLBytes = btnURL.toByteArray(StandardCharsets.UTF_8)
                NoticeDll.INSTANCE.notice(
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
                    btnURLBytes.size
                )
            })
        }
    }

    /**
     * 获取窗口
     * @param windowTitle
     * @return
     */
    fun findHWND(className: String? = null, windowTitle: String?): WinDef.HWND? {
        return User32.INSTANCE.FindWindow(className, windowTitle)
    }

    /**
     * 更新窗口信息
     */
    fun updateRECT(programHWND: WinDef.HWND?, programRECT: WinDef.RECT?) {
//        如果程序最小化无法获取到准确的窗口信息
        if (showWindow(programHWND) && !User32.INSTANCE.GetClientRect(programHWND, programRECT)) {
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
     * @param programHWND
     */
    fun showWindow(programHWND: WinDef.HWND?): Boolean {
        programHWND ?: return false
        if (SystemDll.INSTANCE.IsIconicWindow(programHWND)) {
            if (!User32.INSTANCE.ShowWindow(programHWND, 9)) {
                log.error { "显示窗口异常，错误代码：" + Kernel32.INSTANCE.GetLastError() }
                return false
            }
            delay(200)
        }
        return true
    }

    /**
     * 前置窗口
     */
    fun frontWindow(programHWND: WinDef.HWND?): Boolean {
        programHWND ?: return false
//        显示窗口
        if (!showWindow(programHWND)) {
            log.error { "显示窗口异常，错误代码：" + Kernel32.INSTANCE.GetLastError() }
            return false
        }
//        前置窗口
        if (!User32.INSTANCE.SetForegroundWindow(programHWND)) {
            log.error { "前置窗口异常，错误代码：" + Kernel32.INSTANCE.GetLastError() }
            return false
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
        ROBOT.delay(delay)
    }

    fun delay(minDelay: Int, maxDelay: Int) {
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

    @Suppress("DEPRECATION")
    fun isAliveOfProgram(programName: String): Boolean {
        return String(
            Runtime.getRuntime().exec("cmd /c tasklist | find \"$programName\"").inputStream.readAllBytes()
        ).isNotBlank()
    }

    /**
     * 添加托盘
     * @param menuItems
     */
    fun addTray(
        mouseClickListener: Consumer<MouseEvent?>?,
        vararg menuItems: MenuItem?
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
    fun removeTray() {
        SystemTray.getSystemTray().remove(trayIcon)
    }

    /**
     * 到系统剪切板
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
        userProgramName: String
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
                regCommonNameEnum.value
            )
            &&
            !Advapi32Util.registryValueExists(
                WinReg.HKEY_LOCAL_MACHINE,
                "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\$userProgramName".also { path = it },
                regCommonNameEnum.value
            )
        ) {
            null
        } else Advapi32Util.registryGetStringValue(
            WinReg.HKEY_LOCAL_MACHINE,
            path,
            regCommonNameEnum.value
        )
    }

    /**
     * 关闭本软件
     */
    fun shutdown() {
        val gameHWND = GAME_HWND
        if (gameHWND != null) {
            SystemDll.INSTANCE.uninstallDll(gameHWND)
        }
        removeTray()
        PauseStatus.isPause = true
        Platform.exit()
        Thread.startVirtualThread {
            try {
                log.info { "关闭软件..." }
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                log.error(e) { "休眠被中断" }
            }
            exitProcess(0)
        }
    }

    fun message(text: String, type: Int, hwnd: WinDef.HWND? = null) {
        VIRTUAL_THREAD_POOL.submit {
            SystemDll.INSTANCE.MessageBox_(hwnd ?: let {
                WindowUtil.getStage(WindowEnum.MAIN)?.let {
                    SystemDll.INSTANCE.FindWindowW_(null, WString(it.title))
                }
            }, text, SCRIPT_NAME, type)
        }
    }

    fun messageInfoOk(text: String, type: Int = MB_OK xor MB_ICONINFORMATION, hwnd: WinDef.HWND? = null) {
        message(text, type, hwnd)
    }

    fun messageOk(text: String, type: Int = MB_OK, hwnd: WinDef.HWND? = null) {
        message(text, type, hwnd)
    }

    fun messageError(text: String, type: Int = MB_ICONERROR, hwnd: WinDef.HWND? = null) {
        message(text, type, hwnd)
    }

    /**
     * 获取软件的dll文件路径
     */
    fun getDllFilePath(file: ResourceFile): File? {
        return if (Objects.requireNonNull(javaClass.getResource(""))
                .protocol == "jar"
        ) {
            Path.of(DLL_PATH, file.name).toFile()
        } else {
            loadResource("dll/${file.name}")
        }
    }

    /**
     * 获取软件的exe文件路径
     */
    fun getExeFilePath(file: ResourceFile): File? {
        return if (Objects.requireNonNull(javaClass.getResource(""))
                .protocol == "jar"
        ) {
            Path.of(ROOT_PATH, file.name).toFile()
        } else {
            loadResource("exe/${file.name}")
        }
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
            log.error { "未找到${path}" }
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

    /**
     * 关闭显示器
     */
    fun offScreen() {
        User32.INSTANCE.GetForegroundWindow()?.let {
            User32.INSTANCE.SendMessage(it, WM_SYSCOMMAND, WinDef.WPARAM(SC_MONITORPOWER), WinDef.LPARAM(2));
        }
    }

}
