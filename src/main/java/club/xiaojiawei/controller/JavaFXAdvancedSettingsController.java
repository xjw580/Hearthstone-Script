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
    @Resource
    private Properties scriptProperties;
    @Resource
    private PropertiesUtil propertiesUtil;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Objects.equals(scriptProperties.getProperty(AUTO_OPEN_KEY.getKey()), "true")){
            webSwitch.setStatus(true);
        }
//        监听web界面开关
        webSwitch.statusProperty().addListener((observable, oldValue, newValue) -> {
            scriptProperties.setProperty(AUTO_OPEN_KEY.getKey(), String.valueOf(newValue));
            propertiesUtil.storeScriptProperties();
        });
    }
}
