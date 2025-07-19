package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.hsscript.bean.GameRect
import club.xiaojiawei.hsscript.consts.GAME_CN_NAME
import club.xiaojiawei.hsscript.consts.GameRationConst
import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.dll.CSystemDll.Companion.MB_ICONERROR
import club.xiaojiawei.hsscript.dll.CSystemDll.Companion.MB_TOPMOST
import club.xiaojiawei.hsscript.interfaces.StageHook
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.hsscript.utils.go
import club.xiaojiawei.hsscript.utils.runUI
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Cursor
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.Popup
import javafx.stage.Screen
import javafx.stage.Window
import java.net.URL
import java.util.*
import java.util.concurrent.Future
import java.util.function.Consumer
import kotlin.math.abs
import kotlin.math.min

/**
 * @author 肖嘉威
 * @date 2025/2/6 13:23
 */
class GameWindowModalController : Initializable, StageHook {

    @FXML
    protected lateinit var rootPane: AnchorPane

    private var updateTask: Future<*>? = null

    private var rectangle: Pane? = null

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        addGameRectListener()
        addMouseEventListener()
    }

    private fun buildRectangle(): Pane {
        return StackPane().apply rectangle@{
            cursor = Cursor.MOVE
            children.add(Text((rootPane.children.size + 1).toString()).apply {
                isManaged = false
                style = "-fx-fill: white"
            })
            style = "-fx-border-color: red;-fx-border-width: 2"
            var startX = 0.0
            var startY = 0.0
            onMousePressed = EventHandler { event ->
                if (event.button === MouseButton.PRIMARY) {
                    startX = event.sceneX
                    startY = event.sceneY
                } else if (event.button === MouseButton.SECONDARY) {
                    val popup = Popup().apply popup@{
                        content.add(VBox().apply {
                            style = "-fx-background-color:white;-fx-effect:default-common-effect;-fx-padding:2;"
                            children.addAll(
                                StackPane(Text("删除")).apply {
                                    styleClass.addAll("bg-hover-ui", "radius-ui")
                                    style = "-fx-padding:5 10 5 10"
                                    onMouseClicked = EventHandler { event ->
                                        rootPane.children.remove(this@rectangle)
                                        (this@popup).hide()
                                    }
                                },
                            )
                        })
                        isAutoHide = true
                    }
                    popup.show(rootPane.scene.window, event.screenX, event.screenY)
                }
                event.consume()
            }
            onMouseDragged = EventHandler { event ->
                if (event.button === MouseButton.PRIMARY) {
                    val offsetX = event.sceneX - startX
                    val offsetY = event.sceneY - startY
                    val e = {
                        prefWidth += offsetX
                    }
                    val w = {
                        prefWidth -= offsetX
                        AnchorPane.setLeftAnchor(this, AnchorPane.getLeftAnchor(this) + offsetX)
                    }
                    val s = {
                        prefHeight += offsetY
                    }
                    val n = {
                        prefHeight -= offsetY
                        AnchorPane.setTopAnchor(this, AnchorPane.getTopAnchor(this) + offsetY)
                    }
                    when (cursor) {
                        Cursor.MOVE -> {
                            AnchorPane.setLeftAnchor(this, AnchorPane.getLeftAnchor(this) + event.sceneX - startX)
                            AnchorPane.setTopAnchor(this, AnchorPane.getTopAnchor(this) + event.sceneY - startY)
                        }

                        Cursor.E_RESIZE -> {
                            e()
                        }

                        Cursor.W_RESIZE -> {
                            w()
                        }

                        Cursor.S_RESIZE -> {
                            s()
                        }

                        Cursor.N_RESIZE -> {
                            n()
                        }

                        Cursor.NW_RESIZE -> {
                            n()
                            w()
                        }

                        Cursor.SW_RESIZE -> {
                            s()
                            w()
                        }

                        Cursor.NE_RESIZE -> {
                            n()
                            e()
                        }

                        Cursor.SE_RESIZE -> {
                            s()
                            e()
                        }
                    }
                    startX = event.sceneX
                    startY = event.sceneY
                }
                event.consume()
            }
            onMouseMoved = EventHandler { event ->
                val sceneX = event.sceneX
                val sceneY = event.sceneY
                val left = AnchorPane.getLeftAnchor(this)
                val top = AnchorPane.getTopAnchor(this)
                val right = left + width
                val bottom = top + height
                val range = 5
                cursor = if (abs(sceneX - left) < range && abs(sceneY - top) < range) {
                    Cursor.NW_RESIZE
                } else if (abs(sceneX - left) < range && abs(sceneY - bottom) < range) {
                    Cursor.SW_RESIZE
                } else if (abs(sceneX - right) < range && abs(sceneY - top) < range) {
                    Cursor.NE_RESIZE
                } else if (abs(sceneX - right) < range && abs(sceneY - bottom) < range) {
                    Cursor.SE_RESIZE
                } else if (abs(sceneX - right) < range) {
                    Cursor.E_RESIZE
                } else if (abs(sceneX - left) < range) {
                    Cursor.W_RESIZE
                } else if (abs(sceneY - top) < range) {
                    Cursor.N_RESIZE
                } else if (abs(sceneY - bottom) < range) {
                    Cursor.S_RESIZE
                } else {
                    Cursor.MOVE
                }
            }
            onMouseReleased
        }
    }

    private fun addMouseEventListener() {
        rootPane.onMousePressed = EventHandler { event: MouseEvent ->
            if (event.button !== MouseButton.PRIMARY) return@EventHandler
            rectangle = buildRectangle()
            rootPane.children.add(rectangle)
            AnchorPane.setLeftAnchor(rectangle, event.sceneX)
            AnchorPane.setTopAnchor(rectangle, event.sceneY)
        }
        rootPane.onMouseDragged = EventHandler { event: MouseEvent ->
            rectangle ?: return@EventHandler
            val startY = AnchorPane.getTopAnchor(rectangle)
            val startX = AnchorPane.getLeftAnchor(rectangle)
            rectangle?.prefWidth = min(event.sceneX - startX, rootPane.width - startX)
            rectangle?.prefHeight = min(event.sceneY - startY, rootPane.height - startY)
        }
    }

    private fun addGameRectListener() {
        rootPane.sceneProperty().addListener { _, _, newScene ->
            newScene?.let {
                val exec = Consumer<Window> { newWindow ->
                    ScriptStatus.gameHWND?.let {
                        CSystemDll.INSTANCE.frontWindow(it)
                    } ?: go {
                        GameUtil.launchPlatformAndGame()
                        SystemUtil.message("${GAME_CN_NAME}不在运行", type = MB_ICONERROR xor MB_TOPMOST)
                        CSystemDll.INSTANCE.topWindow(ScriptStatus.gameHWND, true)
                    }
                    newWindow.showingProperty().addListener { _, _, isShow ->
                        if (isShow) {
                            updateTask?.cancel(true)
                            updateTask = go {
                                while (!Thread.interrupted()) {
                                    ScriptStatus.gameHWND?.let {
                                        val gameRect = calcGameRect(it)
                                        newWindow.x = gameRect.x
                                        newWindow.y = gameRect.y
                                        newWindow.width = gameRect.w
                                        newWindow.height = gameRect.h
                                        rootPane.prefWidth = gameRect.w
                                        rootPane.prefHeight = gameRect.h
                                    }
                                    Thread.sleep(10)
                                }
                            }
                        } else {
                            updateTask?.cancel(true)
                        }
                    }
                }
                newScene.window?.let {
                    exec.accept(it)
                } ?: let {
                    newScene.windowProperty().addListener { _, _, newWindow ->
                        newWindow?.let {
                            exec.accept(it)
                        }
                    }
                }
            }
        }
    }

    private data class Rect(
        val x: Double,
        val y: Double,
        val w: Double,
        val h: Double,
    )

    private fun calcGameRect(gameHWND: HWND): Rect {
        val clientRECT = WinDef.RECT()
        val windowRECT = WinDef.RECT()
        val titleH = 25.0
        User32.INSTANCE.GetClientRect(gameHWND, clientRECT)
        User32.INSTANCE.GetWindowRect(gameHWND, windowRECT)
        val clientW = clientRECT.right - clientRECT.left
        val clientH = clientRECT.bottom - clientRECT.top
        val windowW = windowRECT.right - windowRECT.left
        val windowH = windowRECT.bottom - windowRECT.top
        val outputScaleX = Screen.getPrimary().outputScaleX
        val usableH = (clientH) / outputScaleX
        val usableW = usableH * GameRationConst.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO
        return Rect(
            x = ((windowRECT.left + ((windowW - clientW) / 2.0)) / outputScaleX) + ((clientW / outputScaleX - usableW) / 2),
            y = (windowRECT.top + ((windowH - clientH - titleH) / 2.0) + titleH) / outputScaleX,
            w = usableW,
            h = usableH,
        )
    }

    fun getGameRect(): MutableList<GameRect> {
        val usableW = rootPane.width
        val usableH = rootPane.height
        val middleH = usableH / 2
        val middleW = usableW / 2
        return rootPane.children.map {
            it as Pane
            GameRect(
                (AnchorPane.getLeftAnchor(it) - middleW) / usableW,
                (AnchorPane.getLeftAnchor(it) + it.width - middleW) / usableW,
                (AnchorPane.getTopAnchor(it) - middleH) / usableH,
                (AnchorPane.getTopAnchor(it) + it.height - middleH) / usableH
            )
        }.toMutableList()
    }

    fun clearGameRect() {
        runUI {
            rootPane.children.clear()
        }
    }

    fun gameRectSize(): Int {
        return rootPane.children.size
    }

    fun removeFirstGameRect() {
        runUI {
            rootPane.children.removeFirst()
        }
    }

    fun drawGameRect(gameRect: GameRect) {
        val usableW = rootPane.width
        val usableH = rootPane.height
        val middleH = usableH / 2
        val middleW = usableW / 2
        runUI {
            rootPane.children.add(
                StackPane().apply {
                    AnchorPane.setLeftAnchor(this, (gameRect.left * usableW + middleW))
                    AnchorPane.setTopAnchor(this, (gameRect.top * usableH + middleH))
                    prefWidth = (gameRect.right - gameRect.left) * usableW
                    prefHeight = (gameRect.bottom - gameRect.top) * usableH
                    style = "-fx-border-color:red"
                }
            )
        }
    }

    override fun onHidden() {
        CSystemDll.INSTANCE.topWindow(ScriptStatus.gameHWND, false)
    }

    fun setOpacity(opacity: Double) {
        rootPane.style = "-fx-background-color: rgba(0,0,0,${opacity})"
    }

}