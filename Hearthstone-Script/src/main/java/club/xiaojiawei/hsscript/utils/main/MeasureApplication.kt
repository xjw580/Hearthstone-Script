package club.xiaojiawei.hsscript.utils.main

import club.xiaojiawei.hsscript.data.GameRationConst
import club.xiaojiawei.hsscript.dll.SystemDll
import club.xiaojiawei.hsscript.utils.GameUtil.findGameHWND
import club.xiaojiawei.hsscript.utils.SystemUtil
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.stage.Popup
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.Window
import kotlin.math.max
import kotlin.math.min


/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/8/28 10:25
 */
class MeasureApplication : Application() {

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        startStage(primaryStage)
    }

    companion object {
        private var stage: Window? = null

        @JvmStatic
        fun startStage(showStage: Stage) {
            val vBox: VBox = object : VBox() {
                init {
                    alignment = Pos.CENTER
                    style = "-fx-padding: 10;-fx-spacing: 20"
                }
            }
            val btnPane: HBox = object : HBox() {
                init {
                    style = "-fx-spacing: 20;-fx-alignment: center"
                }
            }
            val showBtn = Button("显示")
            val textArea = TextArea()
            textArea.minHeight = 500.0
            showBtn.onAction = EventHandler { event: ActionEvent? ->
                if (stage != null) {
                    stage!!.hide()
                }
                stage = show(showStage, textArea)
            }
            val hideBtn = Button("隐藏")
            hideBtn.onAction = EventHandler { event: ActionEvent? ->
                if (stage != null) {
                    stage!!.hide()
                }
            }
            val clearBtn = Button("清空")
            clearBtn.onAction = EventHandler { event: ActionEvent? ->
                textArea.text = ""
            }
            btnPane.children.addAll(showBtn, hideBtn, clearBtn)
            vBox.children.addAll(textArea, btnPane)
            showStage.scene = Scene(vBox, 450.0, 600.0)
            val title = "GameRectUtil"
            showStage.title = title
            showStage.show()
            SystemDll.INSTANCE.topWindowForTitle(title, true)
        }

        private fun show(stage: Stage, textArea: TextArea): Window? {
            val hwnd = findGameHWND()
            if (hwnd == null) {
                println("null")
                return null
            }
            SystemUtil.frontWindow(hwnd)
            try {
                Thread.sleep(50)
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
            val clientRECT = WinDef.RECT()
            val windowRECT = WinDef.RECT()
            User32.INSTANCE.GetClientRect(hwnd, clientRECT)
            User32.INSTANCE.GetWindowRect(hwnd, windowRECT)
            val clientW = clientRECT.right - clientRECT.left
            val clientH = clientRECT.bottom - clientRECT.top
            val windowW = windowRECT.right - windowRECT.left
            val windowH = windowRECT.bottom - windowRECT.top
            val popup = Popup()
            popup.width = clientW.toDouble()
            popup.height = clientH.toDouble()
            val root = StackPane()
            val stackPane = StackPane(root)
            val circle = Circle(1.5, Color.RED)
            val outputScaleX = Screen.getPrimary().outputScaleX
            val usableH = (clientH) / outputScaleX
            val usableW = usableH * GameRationConst.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO
            val hLine = Line(0.0, 0.0, usableW, 0.0)
            hLine.fill = Color.BLACK
            val vLine = Line(0.0, 0.0, 0.0, usableH)
            vLine.fill = Color.BLACK
            val rectangle = AnchorPane()
            rectangle.style = "-fx-background-color: transparent;-fx-border-color: red;-fx-border-width: 2"
            val anchorPane = AnchorPane(rectangle)
            root.children.addAll(hLine, vLine, circle, anchorPane)
            root.style = "-fx-background-color: rgba(0,0,0,0.2);-fx-border-color: white"
            root.onMousePressed = EventHandler { event: MouseEvent ->
                stage.requestFocus()
                rectangle.prefWidth = 0.0
                rectangle.prefHeight = 0.0
                AnchorPane.setTopAnchor(rectangle, event.sceneY)
                AnchorPane.setLeftAnchor(rectangle, event.sceneX)
            }
            root.onMouseDragged = EventHandler<MouseEvent> { event: MouseEvent ->
                val startY = AnchorPane.getTopAnchor(rectangle)
                val startX = AnchorPane.getLeftAnchor(rectangle)
                rectangle.prefWidth = min(event.sceneX - startX, usableW - startX)
                rectangle.prefHeight = min(event.sceneY - startY, usableH - startY)
            }
            val middleH = usableH / 2
            val middleW = usableW / 2
            val runnable = Runnable {
                val msg = String.format(
                    "public static final GameRect RECT = new GameRect(%.4fD, %.4fD, %.4fD, %.4fD);\n",
                    (AnchorPane.getLeftAnchor(rectangle) - middleW) / usableW,
                    (AnchorPane.getLeftAnchor(rectangle) + rectangle.width - middleW) / usableW,
                    (AnchorPane.getTopAnchor(rectangle) - middleH) / usableH,
                    (AnchorPane.getTopAnchor(rectangle) + rectangle.height - middleH) / usableH
                )
                println(msg)
                textArea.appendText(msg)
            }
            popup.addEventFilter<KeyEvent>(
                KeyEvent.KEY_PRESSED
            ) { event: KeyEvent ->
                if (event.code == KeyCode.CONTROL) {
                    controlDown = true
                } else if (event.code == KeyCode.ESCAPE) {
                    if (popup.isShowing) {
                        popup.hide()
                    } else {
                        popup.show(stage)
                    }
                } else if (event.code == KeyCode.SHIFT) {
                    shiftDown = true
                } else if (shiftDown) {
                    if (event.code == KeyCode.RIGHT) {
                        AnchorPane.setLeftAnchor(
                            rectangle,
                            min(AnchorPane.getLeftAnchor(rectangle) + 5, usableW - rectangle.width)
                        )
                    } else if (event.code == KeyCode.LEFT) {
                        AnchorPane.setLeftAnchor(
                            rectangle,
                            max(AnchorPane.getLeftAnchor(rectangle) - 5, 0.0)
                        )
                    } else if (event.code == KeyCode.UP) {
                        AnchorPane.setTopAnchor(rectangle, max(AnchorPane.getTopAnchor(rectangle) - 5, 0.0))
                    } else if (event.code == KeyCode.DOWN) {
                        AnchorPane.setTopAnchor(
                            rectangle,
                            min(AnchorPane.getTopAnchor(rectangle) + 5, usableH - rectangle.height)
                        )
                    } else if (event.code == KeyCode.ENTER) {
                        runnable.run()
                    }
                } else if (controlDown) {
                    if (event.code == KeyCode.RIGHT) {
                        if (AnchorPane.getLeftAnchor(rectangle) + rectangle.width >= usableW) {
                            AnchorPane.setLeftAnchor(rectangle, AnchorPane.getLeftAnchor(rectangle) - 1)
                        }
                        rectangle.prefWidth = rectangle.width + 1
                    } else if (event.code == KeyCode.LEFT) {
                        rectangle.prefWidth = rectangle.width - 1
                    } else if (event.code == KeyCode.UP) {
                        if (AnchorPane.getTopAnchor(rectangle) + rectangle.height >= usableH) {
                            println("up")
                            AnchorPane.setTopAnchor(rectangle, AnchorPane.getTopAnchor(rectangle) - 1)
                        }
                        rectangle.prefHeight = rectangle.height + 1
                    } else if (event.code == KeyCode.DOWN) {
                        rectangle.prefHeight = rectangle.height - 1
                    }
                }
                if (event.code == KeyCode.RIGHT) {
                    AnchorPane.setLeftAnchor(
                        rectangle,
                        min(AnchorPane.getLeftAnchor(rectangle) + 1, usableW - rectangle.width)
                    )
                } else if (event.code == KeyCode.LEFT) {
                    AnchorPane.setLeftAnchor(rectangle, max(AnchorPane.getLeftAnchor(rectangle) - 1, 0.0))
                } else if (event.code == KeyCode.UP) {
                    AnchorPane.setTopAnchor(rectangle, max(AnchorPane.getTopAnchor(rectangle) - 1, 0.0))
                } else if (event.code == KeyCode.DOWN) {
                    AnchorPane.setTopAnchor(
                        rectangle,
                        min(AnchorPane.getTopAnchor(rectangle) + 1, usableH - rectangle.height)
                    )
                }
            }
            popup.addEventFilter(
                KeyEvent.KEY_RELEASED
            ) { event: KeyEvent ->
                if (event.code == KeyCode.CONTROL) {
                    controlDown = false
                } else if (event.code == KeyCode.SHIFT) {
                    shiftDown = false
                }
            }
            popup.content.addAll(stackPane)
            val titleH = 25.0
            popup.x =
                ((windowRECT.left + ((windowW - clientW) / 2.0)) / outputScaleX) + ((clientW / outputScaleX - usableW) / 2)
            popup.y = (windowRECT.top + ((windowH - clientH - titleH) / 2.0) + titleH) / outputScaleX
            popup.show(stage)
            return popup
        }

        private var controlDown = false
        private var shiftDown = false
    }
}
