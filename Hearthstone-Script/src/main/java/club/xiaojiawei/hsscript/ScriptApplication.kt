package club.xiaojiawei.hsscript

import club.xiaojiawei.CardAction.Companion.commonActionFactory
import club.xiaojiawei.bean.LogRunnable
import club.xiaojiawei.bean.LogThread
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.bean.CommonCardAction.Companion.DEFAULT
import club.xiaojiawei.hsscript.config.InitializerConfig
import club.xiaojiawei.hsscript.consts.MAIN_IMG_NAME
import club.xiaojiawei.hsscript.consts.SCRIPT_NAME
import club.xiaojiawei.hsscript.controller.javafx.StartupController
import club.xiaojiawei.hsscript.core.Core
import club.xiaojiawei.hsscript.dll.SystemDll
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.listener.GlobalHotkeyListener
import club.xiaojiawei.hsscript.listener.VersionListener
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.SystemUtil.addTray
import club.xiaojiawei.hsscript.utils.SystemUtil.shutdown
import club.xiaojiawei.hsscript.utils.WindowUtil.buildStage
import club.xiaojiawei.hsscript.utils.WindowUtil.getStage
import club.xiaojiawei.hsscript.utils.WindowUtil.hideStage
import club.xiaojiawei.hsscript.utils.WindowUtil.showStage
import club.xiaojiawei.hsscript.utils.platformRunLater
import javafx.application.Application
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.stage.Stage
import java.awt.MenuItem
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent
import java.util.function.Consumer
import java.util.function.Supplier
import javax.swing.AbstractAction

/**
 * javaFX启动器
 * @author 肖嘉威
 * @date 2023/7/6 9:46
 */
class ScriptApplication : Application() {

    private var mainShowingListener: ChangeListener<Boolean?>? = null

    override fun start(stage: Stage?) {
        commonActionFactory = Supplier { DEFAULT.createNewInstance() }
        Platform.setImplicitExit(false)
        Thread.ofVirtual().name("Launch VThread").start(LogRunnable(Runnable {
            InitializerConfig.initializer.init()
            setSystemTray()
            Platform.runLater(Runnable { this.showMainPage() })
        }))
        showStartupPage()
    }

    private fun showStartupPage() {
        showStage(WindowEnum.STARTUP)
    }

    private fun showMainPage() {
        val stage = buildStage(WindowEnum.MAIN)
        mainShowingListener =
            ChangeListener { observableValue: ObservableValue<out Boolean?>?, aBoolean: Boolean?, t1: Boolean ->
                if (t1) {
                    stage.showingProperty().removeListener(mainShowingListener)
                    mainShowingListener = null
                    afterShowing()
                }
            }
        stage.showingProperty().addListener(mainShowingListener)
        stage.show()
    }

    private fun setSystemTray() {
        val quit = MenuItem("退出")
        quit.addActionListener(object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                shutdown()
            }
        })
        addTray(MAIN_IMG_NAME, SCRIPT_NAME, Consumer { e: MouseEvent? ->
//            左键点击
            if (e!!.getButton() == 1) {
                Platform.runLater(Runnable {
                    val stage = getStage(WindowEnum.MAIN)
                    if (stage!!.isShowing) {
                        hideStage(WindowEnum.MAIN)
                    } else {
                        showStage(WindowEnum.MAIN)
                    }
                })
            }
        }, quit)
    }

    private fun initClass() {
        Core
    }

    private fun afterShowing() {
        EXTRA_THREAD_POOL.submit(
            LogRunnable {
                initClass()
                GlobalHotkeyListener.register()
                VersionListener.launch()
                WorkListener.launch()

                platformRunLater { StartupController.complete() }

                Runtime.getRuntime()
                    .addShutdownHook(LogThread({SystemDll.INSTANCE.uninstallDll(GameUtil.findGameHWND())}, "ShutdownHook Thread"))

                val args = this.parameters.raw
                var pause: String? = ""
                for (arg in args) {
                    if (arg.startsWith("--pause=")) {
                        val split: Array<String?> = arg.split("=".toRegex(), limit = 2).toTypedArray()
                        if (split.size > 1) {
                            pause = split[1]
                        }
                    }
                }
                if ("false" == pause) {
                    log.info { "接收到开始参数，开始脚本" }
                    Thread.sleep(1000)
                    PauseStatus.isPause = false
                }
            }
        )
    }

}

var PROGRAM_ARGS: List<String> = emptyList()

fun main(args: Array<String>) {
    System.setProperty("jna.library.path", "lib")
    PROGRAM_ARGS = args.toList()

    Application.launch(ScriptApplication::class.java, *args)
}