package club.xiaojiawei.status;


import club.xiaojiawei.CardAction;
import club.xiaojiawei.DeckStrategy;
import club.xiaojiawei.Plugin;
import club.xiaojiawei.bean.PluginWrapper;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.utils.ClassLoaderUtil;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author 肖嘉威
 * @date 2024/9/7 15:05
 */
@Slf4j
public class PluginManager {

    public static final Path SPI_ROOT_PATH = Path.of(System.getProperty("user.dir"), "plugin");

    public final static Set<PluginWrapper<CardAction>> CARD_ACTION_PLUGINS = new HashSet<>();

    public final static Set<PluginWrapper<DeckStrategy>> DECK_STRATEGY_PLUGINS = new HashSet<>();

    private final static ReadOnlyBooleanWrapper loadDeck = new ReadOnlyBooleanWrapper(false);

    private final static ReadOnlyBooleanWrapper loadCard = new ReadOnlyBooleanWrapper(false);

    public static boolean isLoadDeck() {
        return loadDeck.get();
    }

    public static ReadOnlyBooleanProperty loadDeckProperty() {
        return loadDeck.getReadOnlyProperty();
    }

    public static boolean isLoadCard() {
        return loadCard.get();
    }

    public static ReadOnlyBooleanProperty loadCardProperty() {
        return loadCard.getReadOnlyProperty();
    }

    public static void loadAllPlugins() throws Exception {
        loadDeckPlugin();
        loadCardPlugin();
    }

    public static void loadDeckPlugin() throws Exception {
        loadDeck.set(false);
        loadPlugin(DeckStrategy.class, "deck", DECK_STRATEGY_PLUGINS);
        loadDeck.set(true);
    }

    public static void loadCardPlugin() throws Exception {
        loadCard.set(false);
        loadPlugin(CardAction.class, "card", CARD_ACTION_PLUGINS);
        loadCard.set(true);
    }

    private static<T> void loadPlugin(Class<T> aClass, String pluginDir, Set<PluginWrapper<T>> pluginWrappers) throws Exception {
        pluginWrappers.clear();
        List<ClassLoader> deckClassLoaders = ClassLoaderUtil.getClassLoader(SPI_ROOT_PATH.resolve(pluginDir).toFile());

        PluginWrapper<T> pluginWrapper;

//        加载内部spi
        List<Plugin> basePlugin = StreamSupport.stream(ServiceLoader.load(Plugin.class, PluginManager.class.getClassLoader()).spliterator(), false).toList();
        List<T> baseInstance;
        if (basePlugin.isEmpty()) {
            baseInstance = null;
        } else {
            baseInstance = StreamSupport.stream(ServiceLoader.load(aClass, PluginManager.class.getClassLoader()).spliterator(), false).toList();
            pluginWrapper = new PluginWrapper<>(basePlugin.getFirst(), baseInstance);
            pluginWrappers.add(pluginWrapper);
        }

//        加载外部spi
        for (ClassLoader deckClassLoader : deckClassLoaders) {
            try{
                ArrayList<Plugin> plugins = new ArrayList<>(StreamSupport.stream(ServiceLoader.load(Plugin.class, deckClassLoader).spliterator(), false).toList());
                if (!plugins.isEmpty()){
                    Stream<T> stream = StreamSupport.stream(ServiceLoader.load(aClass, deckClassLoader).spliterator(), false);
                    if (baseInstance != null) {
                        stream = stream.filter(i -> {
                            for (T t : baseInstance) {
                                if (Objects.equals(t.getClass().getName(), i.getClass().getName())) {
                                    return false;
                                }
                            }
                            return true;
                        });
                    }
                    pluginWrapper = new PluginWrapper<>(plugins.getLast(), stream.toList());
                    pluginWrappers.add(pluginWrapper);
                }
            }catch (ServiceConfigurationError e){
                log.warn("加载SPI错误", e);
            }
        }
    }

    private static boolean equalsPlugin(Plugin plugin1, Plugin plugin2) {
        if (plugin1 == null || plugin2 == null) {
            return false;
        }
        return Objects.equals(plugin1.id(), plugin2.id());
    }

}
