package club.xiaojiawei.hsscript.controller.javafx;

import club.xiaojiawei.hsscript.enums.WindowEnum;
import club.xiaojiawei.hsscript.utils.WindowUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author 肖嘉威
 * @date 2023/10/14 12:43
 */
public class SettingsController implements Initializable {

    @FXML
    protected Tab initTab;
    @FXML
    protected Tab advancedTab;
    @FXML
    protected Tab pluginTab;
    @FXML
    protected Tab strategyTab;
    @FXML
    protected Tab weightTab;
    @FXML
    protected TabPane rootPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Tab selectedItem = rootPane.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            loadTab(selectedItem);
        }
        rootPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            loadTab(newValue);
        });
    }

    private void loadTab(Tab tab) {
        if (Objects.equals(advancedTab, tab)) {
            if (advancedTab.getContent() == null) {
                Node node = WindowUtil.INSTANCE.loadRoot(WindowEnum.ADVANCED_SETTINGS);
                ScrollPane scrollPane = new ScrollPane(node);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                advancedTab.setContent(scrollPane);
            }
        } else if (Objects.equals(initTab, tab)) {
            if (initTab.getContent() == null) {
                initTab.setContent(WindowUtil.INSTANCE.loadRoot(WindowEnum.INIT_SETTINGS));
            }
        } else if (Objects.equals(pluginTab, tab)) {
            if (pluginTab.getContent() == null) {
                pluginTab.setContent(WindowUtil.INSTANCE.loadRoot(WindowEnum.PLUGIN_SETTINGS));
            }
        } else if (Objects.equals(strategyTab, tab)) {
            if (strategyTab.getContent() == null) {
                strategyTab.setContent(WindowUtil.INSTANCE.loadRoot(WindowEnum.STRATEGY_SETTINGS));
            }
        } else if (Objects.equals(weightTab, tab)) {
            if (weightTab.getContent() == null) {
                weightTab.setContent(WindowUtil.INSTANCE.loadRoot(WindowEnum.WEIGHT_SETTINGS));
            }
        }
    }
}