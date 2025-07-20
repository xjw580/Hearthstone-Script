package club.xiaojiawei.hsscript.component

import club.xiaojiawei.controls.ico.HelpIco
import javafx.beans.property.StringProperty
import javafx.scene.control.Control
import javafx.scene.control.Skin
import javafx.scene.control.SkinBase
import javafx.scene.control.Tooltip
import javafx.util.Duration

/**
 * @author 肖嘉威
 * @date 2025/6/30 22:37
 */
class TipNode : Control() {

    private val textProperty: StringProperty

    fun textProperty() = textProperty

    var text: String
        @Override
        get() = textProperty.get()
        @Override
        set(value) {
            textProperty.set(value)
        }

    init {
        tooltip = Tooltip().apply {
            showDelay = Duration.millis(200.0)
            showDuration = Duration.seconds(10.0)
        }
        textProperty = tooltip.textProperty()
        children.add(HelpIco())
    }

    override fun createDefaultSkin(): Skin<*> {
        return TipNodeSkin(this)
    }

    private class TipNodeSkin(control: TipNode) : SkinBase<TipNode>(control)
}