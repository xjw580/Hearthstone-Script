package club.xiaojiawei.controller;

import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.utils.PropertiesUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

import static club.xiaojiawei.enums.ConfigurationKeyEnum.AUTO_OPEN_KEY;
import static club.xiaojiawei.enums.ConfigurationKeyEnum.STRATEGY_KEY;

/**
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 * @msg
 */
@Component
@Slf4j
public class JavaFXAdvancedSettingsController implements Initializable {

    @FXML
    private Switch webSwitch;
    @FXML
    private Switch strategySwitch;
    @Resource
    private Properties scriptConfiguration;
    @Resource
    private PropertiesUtil propertiesUtil;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Objects.equals(scriptConfiguration.getProperty(AUTO_OPEN_KEY.getKey()), "true")){
            webSwitch.setStatus(true);
        }
        if (Objects.equals(scriptConfiguration.getProperty(STRATEGY_KEY.getKey()), "true")){
            strategySwitch.setStatus(true);
        }
//        监听web界面开关
        webSwitch.statusProperty().addListener((observable, oldValue, newValue) -> {
            scriptConfiguration.setProperty(AUTO_OPEN_KEY.getKey(), String.valueOf(newValue));
            propertiesUtil.storeScriptProperties();
        });
//        监听策略开关
        strategySwitch.statusProperty().addListener((observable, oldValue, newValue) -> {
            scriptConfiguration.setProperty(STRATEGY_KEY.getKey(), String.valueOf(newValue));
            propertiesUtil.storeScriptProperties();
        });
    }
}
