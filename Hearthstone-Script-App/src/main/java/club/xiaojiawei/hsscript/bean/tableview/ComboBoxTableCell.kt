package club.xiaojiawei.hsscript.bean.tableview

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.layout.Region
import javafx.util.StringConverter

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/6/21 11:07
 */
open class ComboBoxTableCell<S, T> : ComboBoxTableCell<S?, T?> {
    private var graphicListener: ChangeListener<Node?>? = null

    @SafeVarargs
    constructor(converter: StringConverter<T?>?, vararg items: T?) : super(converter, *items) {
        addListener()
    }

    @SafeVarargs
    constructor(vararg items: T?) : this(null, *items)

    constructor(items: ObservableList<T?>?) : super(items) {
        addListener()
    }

    private fun addListener() {
        graphicListener = ChangeListener { observable: ObservableValue<out Node?>?, oldValue: Node?, newValue: Node? ->
            if (newValue != null) {
                if (newValue is Region) {
                    val h = getHeight() - 7
                    val w = getWidth() - 7
                    newValue.setPrefHeight(h)
                    newValue.setMinWidth(w)
                    newValue.setMaxWidth(w)
                }
                newValue.getStyleClass().addAll(comboBoxStyleClass())
                graphicProperty().removeListener(graphicListener)
                graphicListener = null
            }
        }
        graphicProperty().addListener(graphicListener)
    }

    protected open fun comboBoxStyleClass(): MutableList<String?> {
        val styleClass = ArrayList<String?>()
        styleClass.add("combo-box-ui")
        styleClass.add("combo-box-ui-normal")
        return styleClass

    }
}
