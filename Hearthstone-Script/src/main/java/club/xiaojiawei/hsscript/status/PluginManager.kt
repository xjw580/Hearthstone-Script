package club.xiaojiawei.hsscript.status

import club.xiaojiawei.*
import club.xiaojiawei.bean.PluginWrapper
import club.xiaojiawei.config.PluginScope
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.utils.ClassLoaderUtil
import club.xiaojiawei.hsscript.utils.ConfigExUtil
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import java.nio.file.Path
import java.util.*
import java.util.stream.StreamSupport


/**
 * @author 肖嘉威
 * @date 2024/9/7 15:05
 */
object PluginManager {

    private val PLUGIN_ROOT_PATH: Path = Path.of(System.getProperty("user.dir"), "plugin")

    /**
     * key：pluginId
     */
    val CARD_ACTION_PLUGINS: MutableMap<String, MutableList<PluginWrapper<CardAction>>> = mutableMapOf()

    val DECK_STRATEGY_PLUGINS: MutableMap<String, MutableList<PluginWrapper<DeckStrategy>>> = mutableMapOf()

    private val loadDeck = ReadOnlyBooleanWrapper(false)

    private val loadCard = ReadOnlyBooleanWrapper(false)

    fun isLoadDeck(): Boolean {
        return loadDeck.get()
    }

    fun loadDeckProperty(): ReadOnlyBooleanProperty {
        return loadDeck.readOnlyProperty
    }

    fun isLoadCard(): Boolean {
        return loadCard.get()
    }

    fun loadCardProperty(): ReadOnlyBooleanProperty {
        return loadCard.readOnlyProperty
    }

    fun loadAllPlugins() {
        loadCardPlugin()
        loadDeckPlugin()
    }

    private fun loadDeckPlugin() {
        DeckStrategyManager
        loadDeck.set(false)
        loadPlugin(DeckStrategy::class.java, DeckPlugin::class.java, DECK_STRATEGY_PLUGINS)
        loadDeck.set(true)
    }

    private fun loadCardPlugin() {
        CardActionManager
        loadCard.set(false)
        loadPlugin(CardAction::class.java, CardPlugin::class.java, CARD_ACTION_PLUGINS)
        loadCard.set(true)
    }

    private fun <T, P : Plugin> loadPlugin(
        aClass: Class<T>,
        pluginClass: Class<P>,
        pluginWrapperMap: MutableMap<String, MutableList<PluginWrapper<T>>>
    ) {
        pluginWrapperMap.clear()
        val deckClassLoaders = ClassLoaderUtil.getClassLoader(PLUGIN_ROOT_PATH.toFile())

        var pluginWrapper: PluginWrapper<T>
        val disableSet:MutableSet<String> =
        if (pluginClass == CardPlugin::class.java) {
            ConfigExUtil.getCardPluginDisabled()
        }else{
            ConfigExUtil.getDeckPluginDisabled()
        }.toMutableSet()
        disableSet.removeAll { it.trim().isEmpty() }

        //        加载内部spi
        val basePlugin = StreamSupport.stream(
            ServiceLoader.load(
                pluginClass,
                PluginManager::class.java.classLoader
            ).spliterator(), false
        ).toList()
        val baseInstance: List<T>?
        if (basePlugin.isEmpty()) {
            baseInstance = null
        } else {
            baseInstance = StreamSupport.stream(
                ServiceLoader.load(aClass, PluginManager::class.java.classLoader).spliterator(),
                false
            ).toList()
            pluginWrapper = PluginWrapper(basePlugin.first(), baseInstance)
            if (disableSet.contains(pluginWrapper.plugin.id())){
                pluginWrapper.setEnabled(false)
            }
            val plugin = pluginWrapper.plugin
            val pluginId = plugin.id()
            if (plugin is CardPlugin) {
                val pluginScope = plugin.pluginScope()
                if (pluginScope === PluginScope.PUBLIC) {
                    addPluginWrapper(pluginWrapper, pluginWrapperMap, "", pluginClass.simpleName)
                } else if (pluginScope !== PluginScope.PROTECTED) {
                    for (id in pluginScope) {
                        addPluginWrapper(pluginWrapper, pluginWrapperMap, id, pluginClass.simpleName)
                    }
                }
            }
            addPluginWrapper(pluginWrapper, pluginWrapperMap, pluginId, pluginClass.simpleName)
        }

        //        加载外部spi
        for (deckClassLoader in deckClassLoaders) {
            try {
                val plugins = ArrayList(
                    StreamSupport.stream(
                        ServiceLoader.load(
                            pluginClass, deckClassLoader
                        ).spliterator(), false
                    ).toList()
                )
                if (plugins.isNotEmpty()) {
                    var stream = StreamSupport.stream(ServiceLoader.load(aClass, deckClassLoader).spliterator(), false)
                    baseInstance?.let {
                        stream = stream.filter { i: T ->
                            for (t in baseInstance) {
                                if (t!!::class.java.name == i!!::class.java.name) {
                                    return@filter false
                                }
                            }
                            true
                        }
                    }
                    pluginWrapper = PluginWrapper(plugins.last(), stream.toList())
                    if (disableSet.contains(pluginWrapper.plugin.id())){
                        pluginWrapper.setEnabled(false)
                    }
                    val pluginId = pluginWrapper.plugin.id()
                    addPluginWrapper(pluginWrapper, pluginWrapperMap, pluginId, pluginClass.simpleName)
                }
            } catch (e: ServiceConfigurationError) {
                log.warn(e) { "加载SPI错误" }
            } catch (e: Error) {
                log.warn(e) { "加载插件错误" }
            } catch (e: Exception) {
                log.warn(e) { "加载插件错误" }
            }
        }
    }

    private fun <T> addPluginWrapper(
        pluginWrapper: PluginWrapper<T>,
        pluginWrapperMap: MutableMap<String, MutableList<PluginWrapper<T>>>,
        pluginId: String, type: String
    ) {
        var pluginWrapperList = pluginWrapperMap[pluginId]
        if (pluginWrapperList == null) {
            pluginWrapperList = mutableListOf(pluginWrapper)
            pluginWrapperMap[pluginId] = pluginWrapperList
        } else {
            pluginWrapperList.add(pluginWrapper)
        }
        pluginWrapper.plugin.apply {
            log.info { "加载${type}：{name: ${name()}, version: ${version()}, author: ${author()}, id: ${id()}, description: ${description()}}" }
        }
    }

    private fun equalsPlugin(plugin1: Plugin?, plugin2: Plugin?): Boolean {
        if (plugin1 == null || plugin2 == null) {
            return false
        }
        return plugin1.id() == plugin2.id()
    }
}
