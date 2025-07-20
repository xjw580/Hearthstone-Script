package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.controls.ico.AbstractIco
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/24 14:45
 */
object MenuItemUtil {
    fun format(menuItem: MenuItem, label: Label, ico: AbstractIco?, iconWidth: Int): MenuItem {
        val iconPane: HBox?
        if (ico == null) {
            iconPane = HBox()
        } else {
            iconPane = HBox(ico)
        }
        iconPane.alignment = Pos.CENTER
        iconPane.prefWidth = iconWidth.toDouble()
        HBox.setHgrow(iconPane, Priority.ALWAYS)
        label.textFill = Color.BLACK
        label.style = "-fx-text-fill: black"
        menuItem.graphic = HBox(iconPane, label).apply {
            style = "-fx-spacing: 6;-fx-alignment: CENTER"
        }
        return menuItem
    }

    fun format(menuItem: MenuItem, text: String?, ico: AbstractIco?, iconWidth: Int): MenuItem {
        format(menuItem, Label(text), ico, iconWidth)
        return menuItem
    }

    fun format(menuItem: MenuItem, label: Label, ico: AbstractIco?): MenuItem {
        format(menuItem, label, ico, 20)
        return menuItem
    }

    fun format(menuItem: MenuItem, text: String?, ico: AbstractIco?): MenuItem {
        format(menuItem, Label(text), ico)
        return menuItem
    }

    fun format(menuItem: MenuItem, label: Label): MenuItem {
        format(menuItem, label, null)
        return menuItem
    }

    fun format(menuItem: MenuItem, text: String?): MenuItem {
        format(menuItem, Label(text), null)
        return menuItem
    }

    fun format(menuItem: MenuItem, label: Label, width: Int): MenuItem {
        format(menuItem, label, null, width)
        return menuItem
    }
}
