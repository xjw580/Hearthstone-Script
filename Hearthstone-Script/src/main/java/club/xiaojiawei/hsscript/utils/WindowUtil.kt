package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.JavaFXUI
import club.xiaojiawei.hsscript.data.FXML_PATH
import club.xiaojiawei.hsscript.data.SCRIPT_ICON_PATH
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.utils.SystemUtil.findHWND
import club.xiaojiawei.hsscript.utils.SystemUtil.showWindow
import club.xiaojiawei.util.isTrue
import javafx.beans.value.ObservableValue
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
import java.util.*

/**
 * 窗口工具类
 * @author 肖嘉威
 * @date 2023/2/10 19:42
 */
object WindowUtil {

    private const val CONTROLLER_KEY = "controller"

    private val STAGE_MAP: MutableMap<WindowEnum, Stage> = mutableMapOf()

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
        cancelHandler: EventHandler<ActionEvent?>?,
        window: Window?
    ): Stage {
        val stage = Stage()
        val rootPane = VBox()
        rootPane.style =
            "-fx-effect: dropshadow(gaussian, rgba(128, 128, 128, 0.67), 10, 0, 0, 0);-fx-background-radius: 5;-fx-background-insets: 10;-fx-padding: 10"
        val okBtn = Button("确认")
        okBtn.styleClass.addAll("btn-ui", "btn-ui-success")
        okBtn.onAction = EventHandler { actionEvent: ActionEvent? ->
            stage.hide()
            okHandler?.handle(actionEvent)
        }
        val cancelBtn = Button("取消")
        cancelBtn.styleClass.addAll("btn-ui")
        cancelBtn.onAction = EventHandler { actionEvent: ActionEvent? ->
            stage.hide()
            cancelHandler?.handle(actionEvent)
        }
        val head = HBox(object : Label(headerText) {
            init {
                style = "-fx-wrap-text: true"
            }
        })
        val center = HBox(object : Label(contentText) {
            init {
                style = "-fx-wrap-text: true"
            }
        })
        val bottom = HBox(okBtn, cancelBtn)
        head.alignment = Pos.CENTER_LEFT
        center.alignment = Pos.CENTER_LEFT
        bottom.alignment = Pos.CENTER_RIGHT
        head.style = "-fx-padding: 15;-fx-font-weight: bold"
        center.style = "-fx-padding: 10 30 10 30;-fx-font-size: 14"
        bottom.style = "-fx-padding: 10;-fx-spacing: 20"
        rootPane.children.addAll(head, center, bottom)
        val scene = Scene(rootPane, 400.0, -1.0)
        scene.fill = Paint.valueOf("#FFFFFF00")
        JavaFXUI.addjavafxUIStylesheet(scene)
        stage.isMaximized = false
        stage.isResizable = false
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.scene = scene
        stage.icons.add(
            Image(
                Objects.requireNonNull(WindowUtil::class.java.getResource(SCRIPT_ICON_PATH))
                    .toExternalForm()
            )
        )
        stage.showingProperty()
            .addListener { _: ObservableValue<out Boolean?>?, _: Boolean?, t1: Boolean? ->
                if (!t1!! && cancelHandler != null) {
                    cancelHandler.handle(null)
                }
            }
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.initOwner(window)
        return stage
    }

    fun createAlert(headerText: String?, contentText: String?, window: Window?): Stage {
        return createAlert(headerText, contentText, null, null, window)
    }


    fun showStage(windowEnum: WindowEnum, owner: Window? = null) {
        var stage = getStage(windowEnum)
        if (stage == null) {
            stage = buildStage(windowEnum)
        }
        if (owner != null && stage.owner == null) {
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
        for (value in WindowEnum.values()) {
            hideStage(value)
        }
    }

    fun getController(windowEnum: WindowEnum): Any? {
        val stage = STAGE_MAP[windowEnum]
        return stage?.let {
            stage.properties[CONTROLLER_KEY]
        }
    }

    fun buildStage(windowEnum: WindowEnum): Stage {
        return buildStage(windowEnum, true)
    }

    fun buildStage(windowEnum: WindowEnum, createStage: Boolean): Stage {
        var stage = STAGE_MAP[windowEnum]
        if (stage == null || createStage) {
            stage = createStage(windowEnum)
            STAGE_MAP[windowEnum] = stage
        }
        return stage
    }

    fun loadRoot(windowEnum: WindowEnum): Node {
        try {
            val fxmlLoader =
                FXMLLoader(WindowUtil::class.java.getResource(FXML_PATH + windowEnum.fxmlName))
            return fxmlLoader.load<Node>()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun createStage(windowEnum: WindowEnum): Stage {
        val stage = Stage()
        try {
            val fxmlLoader =
                FXMLLoader(WindowUtil::class.java.getResource(FXML_PATH + windowEnum.fxmlName))
            stage.properties[CONTROLLER_KEY] = fxmlLoader.getController()
            val scene = Scene(fxmlLoader.load())
            scene.stylesheets.add(JavaFXUI.javafxUIStylesheet())
            stage.scene = scene
            stage.title = windowEnum.title
            stage.icons.add(
                Image(
                    Objects.requireNonNull(WindowUtil::class.java.getResource(SCRIPT_ICON_PATH))
                        .toExternalForm()
                )
            )
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
