package club.xiaojiawei.bean

import club.xiaojiawei.Plugin
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty

/**
 * @author 肖嘉威
 * @date 2024/9/8 16:42
 */
class PluginWrapper<T>(val plugin: Plugin, val spiInstance: List<T>) {

    private val enabled: BooleanProperty = SimpleBooleanProperty(true)

    fun isEnabled(): Boolean {
        return enabled.get()
    }

    fun enabledProperty(): BooleanProperty {
        return enabled
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled.set(enabled)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PluginWrapper<*>

        return plugin.id() == other.plugin.id()
    }

    override fun hashCode(): Int {
        return plugin.id().hashCode()
    }

}