package club.xiaojiawei.hsscript.controller.javafx;

import club.xiaojiawei.hsscript.enums.WindowEnum;
import club.xiaojiawei.hsscript.utils.WindowUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static club.xiaojiawei.hsscript.data.ScriptDataKt.SCRIPT_NAME;

/**
 * @author 肖嘉威
 * @date 2023/10/14 12:43
 */
public class StartupController implements Initializable {

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Text tip;
    private static int count;
    private static ProgressBar staticProgressBar;
    private static Timer timer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        staticProgressBar = progressBar;
        tip.setText(SCRIPT_NAME + "启动中......");
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (++count >= 100) {
                    timer.cancel();
                } else {
                    Platform.runLater(() -> progressBar.setProgress((double) count / 100));
                }
            }
        }, 0, 20);
    }

    /**
     * 完成进度条并隐藏此窗口
     */
    public static void complete() {
        timer.cancel();
        staticProgressBar.setProgress(1D);
        WindowUtil.INSTANCE.hideStage(WindowEnum.STARTUP);
    }
}