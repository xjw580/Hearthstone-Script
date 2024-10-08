package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.bean.LogRunnable
import club.xiaojiawei.hsscript.config.SpringBeanConfig
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.custom.MouseClickListener
import club.xiaojiawei.hsscript.consts.ScriptStaticData
import club.xiaojiawei.hsscript.dll.NoticeDll
import club.xiaojiawei.hsscript.dll.SystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.RegCommonNameEnum
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.util.isTrue
import club.xiaojiawei.ws.WebSocketServer
import com.sun.jna.platform.win32.*
import javafx.application.Platform
import org.apache.logging.log4j.util.Strings
import java.awt.*
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
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

    /**
     * 调用系统通知
     * @param title
     * @param content
     */
    fun notice(title: String = "", content: String, btnText: String = "", btnURL: String = "") {
        ConfigUtil.getBoolean(ConfigEnum.SEND_NOTICE).isTrue {
            Thread.ofVirtual().name("Notice VThread").start(LogRunnable {
//        trayIcon.displayMessage(title, content, TrayIcon.MessageType.NONE);
                val appIDBytes: ByteArray = ScriptStaticData.SCRIPT_NAME.toByteArray(StandardCharsets.UTF_8)
                val titleBytes = title.toByteArray(StandardCharsets.UTF_8)
                val msgBytes = content.toByteArray(StandardCharsets.UTF_8)
                val icoPathBytes: ByteArray =
                    Paths.get(SpringBeanConfig.springData.resourcePath + ScriptStaticData.MAIN_IMG_PNG_NAME)
                        .toAbsolutePath()
                        .normalize().toString().toByteArray(
                            StandardCharsets.UTF_8
                        )
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

    fun notice(content: String) {
        notice(content = content)
    }

    /**
     * 获取窗口
     * @param windowTitle
     * @return
     */
    fun findHWND(className: String? = null, windowTitle: String?): WinDef.HWND? {
        return User32.INSTANCE.FindWindow(className, windowTitle)
    }

    fun findHWND(windowTitle: String?): WinDef.HWND? {
        return findHWND(windowTitle = windowTitle)
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

    fun deleteAllContent() {
        ScriptStaticData.ROBOT.keyPress(KeyEvent.VK_CONTROL)
        sendKey(KeyEvent.VK_A)
        ScriptStaticData.ROBOT.keyRelease(KeyEvent.VK_CONTROL)
        delay(200)
        sendKey(KeyEvent.VK_DELETE)
    }

    fun pasteFromClipboard() {
        ScriptStaticData.ROBOT.keyPress(KeyEvent.VK_CONTROL)
        sendKey(KeyEvent.VK_V)
        ScriptStaticData.ROBOT.keyRelease(KeyEvent.VK_CONTROL)
    }

    fun sendKey(keyCode: Int) {
        ScriptStaticData.ROBOT.keyPress(keyCode)
        ScriptStaticData.ROBOT.keyRelease(keyCode)
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
        ScriptStaticData.ROBOT.delay(delay)
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
        ScriptStaticData.ROBOT.keyPress(18)
        ScriptStaticData.ROBOT.keyPress(115)
        ScriptStaticData.ROBOT.keyRelease(115)
        ScriptStaticData.ROBOT.keyRelease(18)
        log.info { "已关闭程序" }
    }


    fun isAliveOfProgram(programName: String): Boolean {
        return Strings.isNotBlank(
            String(
                Runtime.getRuntime().exec("cmd /c tasklist | find \"$programName\"").inputStream.readAllBytes()
            )
        )
    }

    /**
     * 添加托盘
     * @param trayIconName
     * @param trayName
     * @param menuItems
     */
    fun addTray(
        trayIconName: String,
        trayName: String?,
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
        val image = Toolkit.getDefaultToolkit()
            .getImage(SystemUtil::class.java.getResource(ScriptStaticData.FXML_IMAGE_PATH + trayIconName))
        //        托盘右键弹出菜单
        val popupMenu = PopupMenu()
        for (menuItem in menuItems) {
            popupMenu.add(menuItem)
        }
        //        托盘图标
        trayIcon = TrayIcon(image, trayName, popupMenu)
        trayIcon?.let {
            it.setImageAutoSize(true)
            it.setToolTip(ScriptStaticData.SCRIPT_NAME)
            it.addMouseListener(MouseClickListener(mouseClickListener))
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
        val gameHWND = ScriptStaticData.getGameHWND()
        if (gameHWND != null) {
            SystemDll.INSTANCE.uninstallDll(gameHWND)
        }
        removeTray()
        PauseStatus.isPause = true
        Platform.exit()
        Thread.startVirtualThread {
            try {
                log.info { "关闭软件..." }
                WebSocketServer.closeAll()
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                log.error(e) { "休眠被中断" }
            }
            exitProcess(0)
        }
    }

}
