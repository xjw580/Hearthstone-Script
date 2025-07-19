package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.bean.Card
import club.xiaojiawei.controls.Switch
import club.xiaojiawei.hsscript.consts.GameRationConst
import club.xiaojiawei.hsscript.interfaces.StageHook
import club.xiaojiawei.hsscript.utils.CardLikeTrieUtil.root
import club.xiaojiawei.hsscript.utils.GameDataAnalysisUtil
import club.xiaojiawei.status.WAR
import javafx.animation.AnimationTimer
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Popup
import javafx.stage.WindowEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.net.URL
import java.util.*
import java.util.function.Consumer
import kotlin.math.min

/**
 * @author 肖嘉威
 * @date 2025/1/21 12:52
 */
class GameDataAnalysisController : Initializable, StageHook {

    @FXML
    protected lateinit var filterField: TextField

    @FXML
    protected lateinit var outerPane: VBox

    @FXML
    protected lateinit var topPane: VBox

    @FXML
    protected lateinit var outputField: TextField

    @FXML
    protected lateinit var cardCanvas: Canvas

    @FXML
    protected lateinit var inputField: TextField

    @FXML
    protected lateinit var rootPane: StackPane

    @FXML
    protected lateinit var warCanvas: Canvas

    @FXML
    protected lateinit var analysisSwitch: Switch

    private var canvasWidth = 0.0
    private var canvasHeight = 0.0

    private fun calcHeight(width: Double): Double {
        return width / GameRationConst.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO
    }

    private var warAnimationTimer: AnimationTimer? = null

    private var cardAnimationTimer: AnimationTimer? = null

    private var tipPopup: Popup? = null

    private var inputTipList: ObservableList<Node>? = null

    private var inputCard: Card? = null

    private var banTip = false

    private val war = WAR

    //    private final War war = DeckStrategyUtil.INSTANCE.createMCTSWar();
    private val methodRoot = root


    private fun initCardCanvas() {
        inputField.onKeyPressed = EventHandler { event: KeyEvent ->
            if (event.code == KeyCode.ENTER) {
                outputCardMsg()
                event.consume()
            }
        }
        inputField.focusedProperty()
            .addListener { observable: ObservableValue<out Boolean>?, oldValue: Boolean?, newValue: Boolean ->
                if (newValue) {
                    handleTip()
                } else {
                    hideTipPopup()
                }
            }
        inputField.textProperty()
            .addListener { observable: ObservableValue<out String?>?, oldValue: String?, newValue: String? ->
                handleTip()
            }
        val methods = methodRoot[""]
        val font = Font("Arial", 15.0)
        cardAnimationTimer = object : AnimationTimer() {
            override fun handle(now: Long) {
                if (analysisSwitch.status && inputCard != null) {
                    val text = StringBuilder("\n")
                    var methodName = ""
                    val filterText = filterField.text.lowercase(Locale.getDefault())
                    for (method in methods) {
                        try {
                            methodName = method.name
                            if (!methodName.lowercase(Locale.getDefault()).contains(filterText)) continue
                            if (methodName.startsWith("get")) {
                                methodName = methodName.replace("get", "")
                            }
                            val result = invokeMethod(method, inputCard!!)
                            text.append(methodName).append(": ").append(result).append("\n")
                        } catch (e: InvocationTargetException) {
                            println(methodName)
                        } catch (e: IllegalAccessException) {
                            println(methodName)
                        }
                    }
                    //                    outputArea.setText(text.toString());
                    val height = min(outerPane.height, rootPane.height) - topPane.height
                    if (height <= 0 || canvasWidth <= 0) return
                    cardCanvas.width = canvasWidth
                    cardCanvas.height = height
                    val context = cardCanvas.graphicsContext2D
                    context.clearRect(0.0, 0.0, canvasWidth, height)
                    context.font = font
                    context.fillText(text.toString(), 0.0, 0.0)
                }
            }
        }.apply {
            start()
        }
    }

    private fun handleTip() {
        var text = inputField.text
        if (banTip) return
        if (handleInputCard() == null || !text.contains(".")) {
            hideTipPopup()
            return
        }
        text = text.trim { it <= ' ' }
        val index = text.trim { it <= ' ' }.indexOf(".")
        if (index == -1) return
        val methodName = if (index == text.length - 1) {
            ""
        } else {
            text.substring(index + 1)
        }
        val methods = methodRoot[methodName]
        showTipPopup(methods)
    }

    private fun handleInputMethod(): Method? {
        val text = inputField.text.trim { it <= ' ' }
        val index = text.indexOf(".")
        if (index == -1 || index == text.length - 1) return null
        val methodName = text.substring(index + 1).lowercase(Locale.getDefault())
        val methods = methodRoot[methodName]
        for (method in methods) {
            var name = method.name.lowercase(Locale.getDefault())
            name = name.replace("get", "")
            if (name == methodName) return method
        }
        return null
    }

    private fun handleInputCard(): Card? {
        val text = inputField.text.trim { it <= ' ' }
        val split = text.split("[.]".toRegex()).dropLastWhile { it.isEmpty() }.toList()
        val entityId: String
        val length = split.size
        entityId = if (length == 0) {
            text
        } else {
            split[0]
        }
        return war.cardMap[entityId].also { inputCard = it }
    }

    private fun calcOutput(inputMethod: Method) {
        inputCard?.let {
            try {
                val result = invokeMethod(inputMethod, it)
                if (result != null) {
                    outputField.text = result.toString()
                }
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            } catch (e: InvocationTargetException) {
                throw RuntimeException(e)
            }
        }
    }

    @Throws(InvocationTargetException::class, IllegalAccessException::class)
    private fun invokeMethod(inputMethod: Method, card: Card): Any? {
        val parameterTypes = inputMethod.parameterTypes
        val result: Any
        if (parameterTypes.size > 0) {
            val params = arrayOfNulls<Any>(parameterTypes.size)
            for (i in parameterTypes.indices) {
                val parameterType = parameterTypes[i]
                val param: Any =
                    if (parameterType == Int::class.javaPrimitiveType || parameterType == Long::class.javaPrimitiveType || parameterType == Double::class.javaPrimitiveType) {
                        0
                    } else if (parameterType == Boolean::class.javaPrimitiveType) {
                        false
                    } else {
                        return null
                    }
                params[i] = param
            }
            result = inputMethod.invoke(card, *params)
        } else {
            result = inputMethod.invoke(card)
        }
        return result
    }

    private fun hideTipPopup() {
        tipPopup?.hide()
    }

    private fun showTipPopup(methods: List<Method>) {
        if (tipPopup == null) {
            tipPopup = Popup().apply {
                isAutoFix = false
                isAutoHide = false
                val vBox: VBox = object : VBox() {
                    init {
                        style = "-fx-padding: 5"
                    }
                }
                inputTipList = vBox.children
                val scrollPane: ScrollPane = object : ScrollPane(vBox) {
                    init {
                        style = "-fx-effect: default-common-effect;-fx-padding: 0;-fx-background-insets: 0"
                        styleClass.addAll("radius-ui")
                    }
                }
                scrollPane.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                scrollPane.maxHeight = 500.0
                content.add(scrollPane)
            }

        }
        inputTipList?.clear()
        methods.forEach(Consumer { method: Method ->
            val methodName = if (method.name.contains("get")) method.name.replace("get", "") else method.name
            val pane: StackPane = object : StackPane(Label(methodName)) {
                init {
                    style = "-fx-padding: 5 10 5 5"
                    styleClass.addAll("radius-ui", "bg-hover-ui")
                    onMouseClicked =
                        EventHandler { event: MouseEvent? ->
                            banTip = true
                            inputField.text =
                                inputField.text.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toList()[0] + "." + methodName
                            calcOutput(method)
                            hideTipPopup()
                            banTip = false
                        }
                }
            }
            inputTipList?.add(pane)
        })
        tipPopup?.let {
            it.width = inputField.width
            val bounds = inputField.localToScreen(inputField.boundsInLocal)
            it.anchorX = bounds.minX
            it.anchorY = bounds.maxY
            it.show(rootPane.scene.window)
        }
    }

    private fun initWarCanvas() {
        val padding = rootPane.padding.left + rootPane.padding.right
        canvasWidth = 800.0
        canvasHeight = calcHeight(canvasWidth)
        rootPane.widthProperty()
            .addListener { observable: ObservableValue<out Number>?, oldValue: Number?, newValue: Number ->
                val newWidth = newValue.toInt() - padding
                canvasWidth = newWidth
                canvasHeight = calcHeight(newWidth)
            }
        val analysisUtil = GameDataAnalysisUtil
        analysisUtil.init(warCanvas)
        warAnimationTimer = object : AnimationTimer() {
            override fun handle(now: Long) {
                if (analysisSwitch.status) {
                    warCanvas.width = canvasWidth
                    warCanvas.height = canvasHeight
                    analysisUtil.draw(war, warCanvas)
                }
            }
        }.apply { start() }
    }

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        initWarCanvas()
        initCardCanvas()
    }

    override fun onHiding() {
        warAnimationTimer?.stop()
        cardAnimationTimer?.stop()
    }

    override fun onCloseRequest(event: WindowEvent) {
    }

    @FXML
    protected fun outputCardMsg() {
        if (handleInputCard() == null) return

        val method = handleInputMethod() ?: return

        calcOutput(method)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(GameDataAnalysisController::class.java)
    }
}
