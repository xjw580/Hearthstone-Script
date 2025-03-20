package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.JavaFXUI
import club.xiaojiawei.hsscript.data.FXML_DIR
import club.xiaojiawei.hsscript.data.SCRIPT_NAME
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.interfaces.StageHook
import club.xiaojiawei.hsscript.utils.SystemUtil.findHWND
import club.xiaojiawei.hsscript.utils.SystemUtil.showWindow
import club.xiaojiawei.util.isTrue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint
import javafx.stage.*
import java.io.IOException

/**
 * 窗口工具类
 * @author 肖嘉威
 * @date 2023/2/10 19:42
 */
object WindowUtil {

    private const val CONTROLLER_KEY = "controller"

    private val STAGE_MAP: MutableMap<WindowEnum, Stage> = mutableMapOf()

    private fun addIcon(stage: Stage) {
        stage.icons.add(Image(SystemUtil.getProgramIconFile().toURI().toURL().toExternalForm()))
    }

    fun createMenuPopup(vararg labels: Label?): Popup {
        val popup = Popup()

        val vBox: VBox = object : VBox() {
            init {
                style =
                    "-fx-effect: dropshadow(gaussian, rgba(128, 128, 128, 0.67), 10, 0, 3, 3);-fx-padding: 5 3 5 3;-fx-background-color: white"
            }
        }
        vBox.styleClass.add("radius-ui")

        popup.isAutoHide = true
        popup.content.add(vBox)
        return popup
    }

    /**
     * 创建对话框
     * @param headerText
     * @param contentText
     * @param okHandler
     * @param cancelHandler
     * @return
     */
    fun createAlert(
        headerText: String?,
        contentText: String?,
        okHandler: EventHandler<ActionEvent?>?,
        cancelHandler: EventHandler<ActionEvent?>? = null,
        window: Window?,
        okText: String = "确认",
        cancelText: String = "取消",
    ): Stage {
        val stage = Stage().apply {
            title = SCRIPT_NAME
            isMaximized = false
            isResizable = false
            initModality(Modality.APPLICATION_MODAL)
            initOwner(window)
            onCloseRequest = EventHandler {
                cancelHandler?.handle(null)
            }
        }
        addIcon(stage)

        val okBtn = Button(okText).apply {
            styleClass.addAll("btn-ui", "btn-ui-success")
            onAction = EventHandler { actionEvent: ActionEvent? ->
                stage.hide()
                okHandler?.handle(actionEvent)
            }
        }
        val cancelBtn: Button? = cancelHandler?.let {
            Button(cancelText).apply {
                styleClass.addAll("btn-ui")
                onAction = EventHandler { actionEvent: ActionEvent? ->
                    stage.hide()
                    cancelHandler.handle(actionEvent)
                }
            }
        }
        val head: HBox? = headerText?.let {
            HBox(Label(it).apply {
                style = "-fx-wrap-text: true"
            }).apply {
                alignment = Pos.CENTER_LEFT
                style = "-fx-padding: 15;-fx-font-weight: bold"
            }
        }
        val center: HBox? = contentText?.let {
            HBox(Label(it).apply {
                style = "-fx-wrap-text: true"
            }).apply {
                alignment = Pos.CENTER_LEFT
                style = "-fx-padding: 10 30 10 30;-fx-font-size: 14"
            }
        }
        val bottom = HBox(okBtn).apply {
            cancelBtn?.let {
                children.add(it)
            }
            alignment = Pos.CENTER_RIGHT
            style = "-fx-padding: 10;-fx-spacing: 20"
        }
        val scene = Scene(VBox().apply {
            head?.let {
                children.add(it)
            }
            center?.let {
                children.add(it)
            }
            children.add(bottom)
        }, 400.0, -1.0).apply {
            fill = Paint.valueOf("#FFFFFF00")
        }
        JavaFXUI.addjavafxUIStylesheet(scene)
        stage.scene = scene

        return stage
    }

    fun createAlert(headerText: String?, contentText: String?, window: Window?): Stage {
        return createAlert(headerText, contentText, null, null, window)
    }


    fun showStage(windowEnum: WindowEnum, owner: Window? = null) {
        var stage = getStage(windowEnum)
        if (stage == null) {
            stage = buildStage(windowEnum, owner)
        }
        if (stage.owner == null && owner != null) {
            stage.initOwner(owner)
        }
        if (stage.isShowing) {
            showWindow(findHWND(windowTitle = windowEnum.title))
            stage.requestFocus()
        } else {
            stage.show()
        }
    }

    fun hideStage(windowEnum: WindowEnum) {
        getStage(windowEnum)?.let {
            it.isShowing.isTrue {
                it.hide()
            }
        }
    }

    fun hideAllStage() {
        for (entry in STAGE_MAP) {
            entry.value.hide()
        }
    }

    fun getController(windowEnum: WindowEnum): Any? {
        return getStage(windowEnum)?.let {
            it.properties[CONTROLLER_KEY]
        }
    }

    fun buildStage(windowEnum: WindowEnum, owner: Window? = null): Stage {
        return buildStage(windowEnum, true, owner)
    }

    fun buildStage(windowEnum: WindowEnum, createStage: Boolean, owner: Window? = null): Stage {
        var stage = STAGE_MAP[windowEnum]
        if (stage == null || createStage) {
            stage = createStage(windowEnum)
            owner?.let {
                stage.initOwner(it)
            }
            STAGE_MAP[windowEnum] = stage
            val controller = getController(windowEnum)
            if (controller is StageHook) {
                stage.setOnShown {
                    controller.onShown()
                }
                stage.setOnShowing {
                    controller.onShowing()
                }
                stage.setOnHidden {
                    controller.onHidden()
                }
            }
            stage.setOnHiding {
                stage.isIconified = false
                if (controller is StageHook){
                    controller.onHiding()
                }
            }
            stage.setOnCloseRequest { event ->
                stage.isIconified = false
                if (controller is StageHook){
                    controller.onCloseRequest(event)
                }
            }

            if (!windowEnum.cache) {
                stage.showingProperty().addListener { o, oldV, newV ->
                    if (!newV) {
                        STAGE_MAP.remove(windowEnum)
                    }
                }
            }
        }
        stage.scene.stylesheets.add("/fxml/css/common.css")
        return stage
    }

    fun loadRoot(windowEnum: WindowEnum): Node {
        try {
            val fxmlLoader =
                FXMLLoader(WindowUtil::class.java.getResource(FXML_DIR + windowEnum.fxmlName))
            return fxmlLoader.load()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun createStage(windowEnum: WindowEnum): Stage {
        val stage = Stage()
        try {
            val fxmlLoader =
                FXMLLoader(WindowUtil::class.java.getResource(FXML_DIR + windowEnum.fxmlName))
            val scene = Scene(fxmlLoader.load())
            stage.properties[CONTROLLER_KEY] = fxmlLoader.getController()
            scene.stylesheets.add(JavaFXUI.javafxUIStylesheet())
            stage.scene = scene
            stage.title = windowEnum.title
            addIcon(stage)

            (windowEnum.width > 0).isTrue {
                stage.width = windowEnum.width
                stage.minWidth = windowEnum.width
            }
            (windowEnum.height > 0).isTrue {
                stage.height = windowEnum.height
                stage.minHeight = windowEnum.height
            }

            if (windowEnum.x != -1.0) {
                stage.x = windowEnum.x
            }
            if (windowEnum.y != -1.0) {
                stage.y = windowEnum.y
            }
            stage.isAlwaysOnTop = windowEnum.alwaysOnTop
            stage.initStyle(windowEnum.initStyle)
            if (windowEnum.initStyle === StageStyle.TRANSPARENT) {
                scene.fill = null
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return stage
    }

    /**
     * 获取stage
     * @param windowEnum
     * @return
     */
    fun getStage(windowEnum: WindowEnum): Stage? {
        return STAGE_MAP[windowEnum]
    }

}
