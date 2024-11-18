package club.xiaojiawei.hsscript

import club.xiaojiawei.CardAction.Companion.commonActionFactory
import club.xiaojiawei.bean.LRunnable
import club.xiaojiawei.bean.LThread
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.bean.CommonCardAction.Companion.DEFAULT
import club.xiaojiawei.hsscript.bean.Release
import club.xiaojiawei.hsscript.config.InitializerConfig
import club.xiaojiawei.hsscript.data.GAME_CN_NAME
import club.xiaojiawei.hsscript.data.MAIN_IMG_NAME
import club.xiaojiawei.hsscript.data.SCRIPT_NAME
import club.xiaojiawei.hsscript.controller.javafx.StartupController
import club.xiaojiawei.hsscript.core.Core
import club.xiaojiawei.hsscript.dll.SystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.listener.GlobalHotkeyListener
import club.xiaojiawei.hsscript.listener.VersionListener
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.status.TaskManager
import club.xiaojiawei.hsscript.utils.CardUtil
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.hsscript.utils.SystemUtil.addTray
import club.xiaojiawei.hsscript.utils.SystemUtil.shutdown
import club.xiaojiawei.hsscript.utils.VersionUtil
import club.xiaojiawei.hsscript.utils.WindowUtil.buildStage
import club.xiaojiawei.hsscript.utils.WindowUtil.getStage
import club.xiaojiawei.hsscript.utils.WindowUtil.hideStage
import club.xiaojiawei.hsscript.utils.WindowUtil.showStage
import club.xiaojiawei.hsscript.utils.platformRunLater
import club.xiaojiawei.util.isFalse
import javafx.application.Application
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.stage.Screen
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
class MainApplication : Application() {

    private var mainShowingListener: ChangeListener<Boolean?>? = null

    override fun start(stage: Stage?) {
        commonActionFactory = Supplier { DEFAULT.createNewInstance() }
        Platform.setImplicitExit(false)
        Thread.ofVirtual().name("Launch VThread").start(LRunnable(Runnable {
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
        val isPauseItem = MenuItem("开始")
        isPauseItem.addActionListener(object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                PauseStatus.asyncSetPause(!PauseStatus.isPause)
            }
        })
        PauseStatus.addListener( { observableValue: ObservableValue<out Boolean?>?, aBoolean: Boolean?, isPause: Boolean ->
            if (isPause){
                isPauseItem.label = "开始"
            }else{
                isPauseItem.label = "暂停"
            }
        })

        val settingsItem = MenuItem("设置")
        settingsItem.addActionListener(object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                platformRunLater {
                    val stage = getStage(WindowEnum.SETTINGS)?: buildStage(WindowEnum.SETTINGS)
                    if (stage.owner == null){
                        getStage(WindowEnum.MAIN)?.let {
                            stage.initOwner(it)
                        }
                    }
                    stage.show()
                }
            }
        })

        val quitItem = MenuItem("退出")
        quitItem.addActionListener(object : AbstractAction() {
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
        }, isPauseItem, settingsItem, quitItem)
    }

    private fun initClass() {
        Core
        TaskManager
    }

    private fun afterShowing() {
        EXTRA_THREAD_POOL.submit(
            LRunnable {
                initClass()
                GlobalHotkeyListener.register()
                VersionListener.launch()
                WorkListener.launch()
                CardUtil.reloadCardWeight()

                platformRunLater { StartupController.complete() }

                Runtime.getRuntime()
                    .addShutdownHook(LThread({SystemDll.INSTANCE.uninstallDll(GameUtil.findGameHWND())}, "ShutdownHook Thread"))

                SystemDll.INSTANCE.IsRunAsAdministrator().isFalse {
                    log.warn { "当前进程不是以管理员启动，功能受限" }
                    SystemUtil.notice("当前进程不是以管理员启动，功能受限")
                }

                Screen.getScreens()?.let {
                    if (it.size > 1){
                        log.info { "检测到多台显示器，开始运行后${GAME_CN_NAME}不要移动到其他显示器" }
                    }
                }

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
                }else {
                    val version = ConfigUtil.getString(ConfigEnum.CURRENT_VERSION)
                    if (Release.compareVersion(VersionUtil.VERSION, version) > 0){
                        platformRunLater {
                            showStage(WindowEnum.VERSION_MSG, getStage(WindowEnum.MAIN))
                            ConfigUtil.putString(ConfigEnum.CURRENT_VERSION, VersionUtil.VERSION)
                        }
                    }
                }
            }
        )
    }

}