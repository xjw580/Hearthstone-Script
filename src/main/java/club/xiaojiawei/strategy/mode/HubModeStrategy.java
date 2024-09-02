package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.bean.GameRect;
import club.xiaojiawei.data.GameRationStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ConfigurationEnum;
import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.enums.RunModeEnum;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 主界面
 * @author 肖嘉威
 * @date 2022/11/25 12:36
 */
@Slf4j
@Component
public class HubModeStrategy extends AbstractModeStrategy<Object> {

    public static final GameRect TOURNAMENT_MODE_RECT = new GameRect(-0.0790D, 0.0811D, -0.2090D, -0.1737D);
    //    TODO ADD
    public static final GameRect CLOSE_AD1_RECT = new GameRect(-0.0790D, 0.0811D, -0.2090D, -0.1737D);
    //    TODO ADD
    public static final GameRect CLOSE_AD2_RECT = new GameRect(-0.0790D, 0.0811D, -0.2090D, -0.1737D);

    @Resource
    private Properties scriptConfiguration;
    @Override
    public void wantEnter() {

    }

    @Override
    protected void beforeEnter() {
        super.beforeEnter();
    }

    @Override
    protected void afterEnter(Object o) {
        if (Mode.getPrevMode() != ModeEnum.COLLECTIONMANAGER) {
            log.info("点击弹窗（去除任务，活动等）");
            for (int i = 0; i < 4; i++) {
                if (isPause.get().get()){
                    return;
                }
                CLOSE_AD1_RECT.lClick();
                SystemUtil.delay(500);
            }
            CLOSE_AD2_RECT.lClick();
            SystemUtil.delay(200);
        }
        log.info("准备进入指定模式");
        RunModeEnum.valueOf(scriptConfiguration.getProperty(ConfigurationEnum.RUN_MODE.getKey())).getModeEnum().getAbstractModeStrategy().wantEnter();
    }

}
