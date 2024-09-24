package club.xiaojiawei.controller.javafx;

import club.xiaojiawei.DeckStrategy;
import club.xiaojiawei.bean.PluginWrapper;
import club.xiaojiawei.component.PluginItem;
import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.status.DeckStrategyManager;
import club.xiaojiawei.status.PluginManager;
import club.xiaojiawei.utils.PropertiesUtil;
import jakarta.annotation.Resource;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 */
@Component
@Slf4j
public class PluginSettingsController implements Initializable {

    @Resource
    private Properties scriptConfiguration;
    @Resource
    private PropertiesUtil propertiesUtil;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private NotificationManager<Object> notificationManager;
    @FXML
    private ListView<VBox> pluginListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initValue();
        listen();
    }

    private void initValue() {
        List<PluginWrapper<DeckStrategy>> list = PluginManager.INSTANCE.getDECK_STRATEGY_PLUGINS().values().stream().flatMap(Collection::stream).toList();
        var pluginItems = pluginListView.getItems();
        for (PluginWrapper<DeckStrategy> deckStrategyPluginWrapper : list) {
            pluginItems.add(new PluginItem(deckStrategyPluginWrapper));
        }
    }

    private void listen() {
    }


    public void apply(ActionEvent actionEvent) {

    }

    public void save(ActionEvent actionEvent) {

    }
}
