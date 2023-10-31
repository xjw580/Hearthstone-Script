package club.xiaojiawei.controller;

import club.xiaojiawei.controls.PasswordShowField;
import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.utils.PropertiesUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

import static club.xiaojiawei.enums.ConfigurationEnum.*;

/**
 *
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 */
@Component
@Slf4j
public class JavaFXAdvancedSettingsController implements Initializable {

    @FXML
    private Switch webSwitch;
    @FXML
    private Switch strategySwitch;
    @FXML
    private Switch verifySwitch;
    @FXML
    private PasswordShowField psw;
    @FXML
    private Switch updateDev;
    @FXML
    private Switch autoUpdate;
    @Resource
    private Properties scriptConfiguration;
    @Resource
    private PropertiesUtil propertiesUtil;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initValue();
        listen();
    }
    private void initValue(){
        if (Objects.equals(scriptConfiguration.getProperty(AUTO_OPEN_WEB.getKey()), "true")){
            webSwitch.setInitStatus(true);
        }
        if (Objects.equals(scriptConfiguration.getProperty(STRATEGY.getKey()), "true")){
            strategySwitch.setInitStatus(true);
        }
        if (Objects.equals(scriptConfiguration.getProperty(ENABLE_VERIFY.getKey()), "true")){
            verifySwitch.setInitStatus(true);
        }
        if (Objects.equals(scriptConfiguration.getProperty(UPDATE_DEV.getKey()), "true")){
            updateDev.setInitStatus(true);
        }
        if (Objects.equals(scriptConfiguration.getProperty(AUTO_UPDATE.getKey()), "true")){
            autoUpdate.setInitStatus(true);
        }
    }
    private void listen(){
//        监听web界面开关
        webSwitch.initStatusProperty().addListener((observable, oldValue, newValue) -> {
            scriptConfiguration.setProperty(AUTO_OPEN_WEB.getKey(), String.valueOf(newValue));
            propertiesUtil.storeScriptProperties();
        });
//        监听策略开关
        strategySwitch.initStatusProperty().addListener((observable, oldValue, newValue) -> {
            scriptConfiguration.setProperty(STRATEGY.getKey(), String.valueOf(newValue));
            propertiesUtil.storeScriptProperties();
        });
//        监听安全验证开关
        verifySwitch.initStatusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                String psw = this.psw.getText();
                if (Strings.isNotBlank(psw)){
                    scriptConfiguration.setProperty(VERIFY_PASSWORD.getKey(), DigestUtils.md5DigestAsHex(psw.getBytes(StandardCharsets.UTF_8)));
                    WebDashboardController.TOKEN_SET.clear();
//                    TODO
                }else if (Strings.isBlank(scriptConfiguration.getProperty(VERIFY_PASSWORD.getKey()))){
                    verifySwitch.setInitStatus(false);
                    this.psw.requestFocus();
                    return;
                }
            }
            scriptConfiguration.setProperty(ENABLE_VERIFY.getKey(), String.valueOf(newValue));
            propertiesUtil.storeScriptProperties();
        });
//        监听更新开发版开关
        updateDev.initStatusProperty().addListener((observable, oldValue, newValue) -> {
            scriptConfiguration.setProperty(UPDATE_DEV.getKey(), String.valueOf(newValue));
            propertiesUtil.storeScriptProperties();

        });
//        监听自动更新开关
        autoUpdate.initStatusProperty().addListener((observable, oldValue, newValue) -> {
            scriptConfiguration.setProperty(AUTO_UPDATE.getKey(), String.valueOf(newValue));
            propertiesUtil.storeScriptProperties();
        });
    }
}
