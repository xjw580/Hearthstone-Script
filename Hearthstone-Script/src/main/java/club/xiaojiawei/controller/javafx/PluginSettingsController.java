package club.xiaojiawei.controller.javafx;

import club.xiaojiawei.component.PluginItem;
import club.xiaojiawei.controls.CopyLabel;
import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.status.PluginManager;
import club.xiaojiawei.utils.PropertiesUtil;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.Resource;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Collection;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 */
@Component
@Slf4j
public class PluginSettingsController implements Initializable {

    @FXML
    private CopyLabel pluginDescription;
    @FXML
    private VBox pluginInfo;
    @FXML
    private CopyLabel pluginName;
    @FXML
    private CopyLabel pluginAuthor;
    @FXML
    private CopyLabel pluginId;
    @FXML
    private CopyLabel pluginVersion;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private NotificationManager<Object> notificationManager;
    @FXML
    private ListView<PluginItem> pluginListView;

    @Resource
    private Properties scriptConfiguration;
    @Resource
    private PropertiesUtil propertiesUtil;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initValue();
        listen();
    }

    private void initValue() {
        var pluginItems = pluginListView.getItems();
        Stream.concat(
                PluginManager.INSTANCE.getDECK_STRATEGY_PLUGINS().values().stream().flatMap(Collection::stream),
                PluginManager.INSTANCE.getCARD_ACTION_PLUGINS().values().stream().flatMap(Collection::stream)
        ).forEach(plugin -> pluginItems.add(new PluginItem(plugin, notificationManager)));
    }

    private void listen() {
        pluginListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
           pluginInfo.setVisible(newValue != null);
           if (newValue != null) {
               pluginName.setText(newValue.getPluginWrapper().getPlugin().name());
               pluginAuthor.setText(newValue.getPluginWrapper().getPlugin().author());
               pluginId.setText(newValue.getPluginWrapper().getPlugin().id());
               pluginVersion.setText(newValue.getPluginWrapper().getPlugin().version());
               pluginDescription.setText(newValue.getPluginWrapper().getPlugin().description());
           }
        });
    }


    public void apply(ActionEvent actionEvent) {

    }

    public void save(ActionEvent actionEvent) {

    }

    @FXML
    protected void jumpToHome(ActionEvent actionEvent) {
        PluginItem selectedItem = pluginListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;
        String homeUrl = selectedItem.getPluginWrapper().getPlugin().homeUrl();
        if (homeUrl.isBlank() || !homeUrl.contains("http")) return;
        SystemUtil.openUrlByBrowser(homeUrl);
    }

}
