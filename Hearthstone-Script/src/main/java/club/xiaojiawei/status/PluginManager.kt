package club.xiaojiawei.status

import club.xiaojiawei.*
import club.xiaojiawei.bean.PluginWrapper
import club.xiaojiawei.config.PluginScope
import club.xiaojiawei.config.log
import club.xiaojiawei.utils.ClassLoaderUtil
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

    private val SPI_ROOT_PATH: Path = Path.of(System.getProperty("user.dir"), "plugin")

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
        loadDeckPlugin()
        loadCardPlugin()
    }

    private fun loadDeckPlugin() {
        loadDeck.set(false)
        loadPlugin(DeckStrategy::class.java, DeckPlugin::class.java, "deck", DECK_STRATEGY_PLUGINS)
        loadDeck.set(true)
        DeckStrategyManager.DECK_STRATEGIES
    }

    private fun loadCardPlugin() {
        loadCard.set(false)
        loadPlugin(CardAction::class.java, CardPlugin::class.java, "card", CARD_ACTION_PLUGINS)
        loadCard.set(true)
        CardActionManager.CARD_ACTION_MAP
    }

    private fun <T, P : Plugin> loadPlugin(
        aClass: Class<T>,
        pluginClass: Class<P>,
        pluginDir: String,
        pluginWrapperMap: MutableMap<String, MutableList<PluginWrapper<T>>>
    ) {
        pluginWrapperMap.clear()
        val deckClassLoaders = ClassLoaderUtil.getClassLoader(SPI_ROOT_PATH.resolve(pluginDir).toFile())

        var pluginWrapper: PluginWrapper<T>

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
            val plugin = pluginWrapper.plugin
            val pluginId = plugin.id()
            if (plugin is CardPlugin) {
                val pluginScope = plugin.pluginScope()
                if (pluginScope === PluginScope.PUBLIC) {
                    addPluginWrapper(pluginWrapper, pluginWrapperMap, "", pluginDir)
                } else if (pluginScope !== PluginScope.PROTECTED) {
                    for (id in pluginScope) {
                        addPluginWrapper(pluginWrapper, pluginWrapperMap, id, pluginDir)
                    }
                }
            }
            addPluginWrapper(pluginWrapper, pluginWrapperMap, pluginId, pluginDir)
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
                    pluginWrapper = PluginWrapper(plugins.last, stream.toList())
                    val pluginId = pluginWrapper.plugin.id()
                    addPluginWrapper(pluginWrapper, pluginWrapperMap, pluginId, pluginDir)
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
        log.info { "加载${type}插件: 【${pluginWrapper.plugin.getInfoString()}】" }
    }

    private fun equalsPlugin(plugin1: Plugin?, plugin2: Plugin?): Boolean {
        if (plugin1 == null || plugin2 == null) {
            return false
        }
        return plugin1.id() == plugin2.id()
    }
}
