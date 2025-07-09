package club.xiaojiawei.hsscript.component

import club.xiaojiawei.controls.WindowBar
import club.xiaojiawei.controls.ico.ClearIco
import club.xiaojiawei.enums.CardActionEnum
import club.xiaojiawei.hsscript.MainApplication
import club.xiaojiawei.hsscript.bean.InfoCard
import club.xiaojiawei.hsscript.enums.CardInfoActionTypeEnum
import javafx.beans.property.ObjectProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.util.StringConverter
import java.io.IOException

/**
 * @author 肖嘉威
 * @date 2025/6/10 9:50
 */
class EditActionPane(
    var infoCard: InfoCard, var actionTypeEnum: CardInfoActionTypeEnum, val saveCallback: (() -> Unit)? = null
) : StackPane() {

    @FXML
    protected lateinit var actionPane: VBox

    @FXML
    protected lateinit var windowBar: WindowBar

    fun setTitle(title: String) {
        windowBar.title = title
    }

    init {
        try {
            val fxmlLoader = FXMLLoader(MainApplication::class.java.getResource("/fxml/component/EditActionPane.fxml"))
            fxmlLoader.setRoot(this)
            fxmlLoader.setController(this)
            fxmlLoader.load<Any?>()
            afterFXMLLoaded()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private class ActionItem(
        cardActionEnum: CardActionEnum, deleteCallback: ((ActionItem) -> Unit)? = null
    ) : HBox() {
        val cardActionProperty: ObjectProperty<CardActionEnum>

        init {
            styleClass.add("actionItem")
            children.addAll(
                Label("行为"),
                ComboBox<CardActionEnum>().apply {
                    cardActionProperty = this.valueProperty()
                    converter = object : StringConverter<CardActionEnum?>() {
                        override fun toString(p0: CardActionEnum?): String? {
                            return p0?.comment
                        }

                        override fun fromString(p0: String?): CardActionEnum? {
                            return null
                        }
                    }
                    styleClass.addAll(
                        "combo-box-ui", "combo-box-ui-small", "combo-box-ui-normal"
                    )
                    items.addAll(CardActionEnum.entries)
                    value = cardActionEnum
                },
                ClearIco().apply {
                    styleClass.add("clear-ico")
                    onMouseClicked = EventHandler {
                        if (it.button === MouseButton.PRIMARY) {
                            deleteCallback?.invoke(this@ActionItem)
                        }
                    }
                },
            )
        }
    }

    private fun afterFXMLLoaded() {
        update()
    }

    fun update() {
        actionPane.children.clear()
        val actions: List<CardActionEnum> = when (actionTypeEnum) {
            CardInfoActionTypeEnum.PLAY -> {
                infoCard.playActions
            }

            CardInfoActionTypeEnum.POWER -> {
                infoCard.powerActions
            }
        }
        for (enum in actions) {
            actionPane.children.add(ActionItem(enum, actionPane.children::remove))
        }
    }


    @FXML
    protected fun addAction() {
        actionPane.children.add(
            ActionItem(CardActionEnum.NO_POINT, actionPane.children::remove)
        )
    }

    @FXML
    protected fun apply() {
        val cardActionEnums = mutableListOf<CardActionEnum>()
        for (node in actionPane.children) {
            if (node is ActionItem) {
                cardActionEnums.add(node.cardActionProperty.get())
            }
        }
        if (actionTypeEnum === CardInfoActionTypeEnum.PLAY) {
            infoCard.playActions = cardActionEnums
        } else if (actionTypeEnum === CardInfoActionTypeEnum.POWER) {
            infoCard.powerActions = cardActionEnums
        }

        saveCallback?.invoke()
    }

    @FXML
    protected fun save() {
        apply()
        scene.window.hide()
    }

}