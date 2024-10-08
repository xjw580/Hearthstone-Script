package club.xiaojiawei.core;

import club.xiaojiawei.config.ThreadPoolConfigKt;
import club.xiaojiawei.controller.javafx.MainController;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.WindowEnum;
import club.xiaojiawei.starter.AbstractStarter;
import club.xiaojiawei.status.Work;
import club.xiaojiawei.utils.SystemUtil;
import club.xiaojiawei.utils.WindowUtil;
import jakarta.annotation.Resource;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 控制脚本的启动
 * @author 肖嘉威
 * @date 2023/7/5 13:15
 */
@Component
@Slf4j
public class Core{

    @Lazy
    @Resource
    private AbstractStarter starter;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private MainController javafxMainController;

    /**
     * 启动脚本
     */
    public synchronized void start(){
        if (Work.isWorking()){
            log.warn("正在工作，无法重复工作");
            return;
        }
        Work.setWorking(true);
        ThreadPoolConfigKt.getCORE_THREAD_POOL().execute(() -> {
            if (!ScriptStaticData.isSetPath()){
                SystemUtil.notice("需要配置" + ScriptStaticData.GAME_CN_NAME + "和" + ScriptStaticData.PLATFORM_CN_NAME + "的路径");
                Platform.runLater(() -> WindowUtil.showStage(WindowEnum.SETTINGS));
                isPause.get().set(true);
            }else if (!isPause.get().get()){
                javafxMainController.expandedLogPane();
                log.info("热键：Ctrl+P 开始/停止程序,Alt+P 关闭程序");
                starter.start();
            }
        });
    }

    /**
     * 重启脚本
     */
    public void restart(){
        ThreadPoolConfigKt.getCORE_THREAD_POOL().execute(() -> {
            isPause.get().set(true);
            SystemUtil.killGame();
            log.info("游戏重启中……");
            isPause.get().set(false);
        });
    }

}
