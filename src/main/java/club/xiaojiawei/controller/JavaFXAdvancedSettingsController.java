package club.xiaojiawei.controller;

import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.utils.PropertiesUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
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
    @FXML
    private Switch verifySwitch;
    @FXML
    private TextField psw;
    @FXML
    private Switch updateDev;
    @Resource
    private Properties scriptConfiguration;
    @Resource
    private PropertiesUtil propertiesUtil;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Objects.equals(scriptConfiguration.getProperty(AUTO_OPEN_WEB.getKey()), "true")){
            webSwitch.setStatus(true);
        }
        if (Objects.equals(scriptConfiguration.getProperty(STRATEGY.getKey()), "true")){
            strategySwitch.setStatus(true);
        }
        if (Objects.equals(scriptConfiguration.getProperty(ENABLE_VERIFY.getKey()), "true")){
            verifySwitch.setStatus(true);
        }
        if (Objects.equals(scriptConfiguration.getProperty(UPDATE_DEV.getKey()), "true")){
            updateDev.setStatus(true);
        }
//        监听web界面开关
        webSwitch.statusProperty().addListener((observable, oldValue, newValue) -> {
            scriptConfiguration.setProperty(AUTO_OPEN_WEB.getKey(), String.valueOf(newValue));
            propertiesUtil.storeScriptProperties();
        });
//        监听策略开关
        strategySwitch.statusProperty().addListener((observable, oldValue, newValue) -> {
            scriptConfiguration.setProperty(STRATEGY.getKey(), String.valueOf(newValue));
            propertiesUtil.storeScriptProperties();
        });
//        监听安全验证开关
        verifySwitch.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                String psw = this.psw.getText();
                if (Strings.isNotBlank(psw)){
                    scriptConfiguration.setProperty(VERIFY_PASSWORD.getKey(), DigestUtils.md5DigestAsHex(psw.getBytes(StandardCharsets.UTF_8)));
                    WebDashboardController.tokenSet.clear();
                    this.psw.setText("设置成功");
                }else if (Strings.isBlank(scriptConfiguration.getProperty(VERIFY_PASSWORD.getKey()))){
                    verifySwitch.setStatus(false);
                    this.psw.requestFocus();
                    return;
                }
            }
            scriptConfiguration.setProperty(ENABLE_VERIFY.getKey(), String.valueOf(newValue));
            propertiesUtil.storeScriptProperties();
        });
//        监听更新开发版开关
        updateDev.statusProperty().addListener((observable, oldValue, newValue) -> {
            scriptConfiguration.setProperty(UPDATE_DEV.getKey(), String.valueOf(newValue));
            propertiesUtil.storeScriptProperties();
        });
    }
}
