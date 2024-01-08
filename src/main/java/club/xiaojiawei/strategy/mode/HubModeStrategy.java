package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.data.GameRationStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ConfigurationEnum;
import club.xiaojiawei.enums.RunModeEnum;
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
//        点击弹窗（去除任务，活动等）
        log.info("点击弹窗（去除任务，活动等）中……");
        for (int i = 0; i < 4; i++) {
            if (isPause.get().get()){
                return;
            }
            SystemUtil.updateGameRect();
            mouseUtil.leftButtonClick(
                    (ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left) >> 1,
                    (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * GameRationStaticData.CONFIRM_OR_CLOSE_BUTTON_VERTICAL_TO_BOTTOM_RATION)
            );
            SystemUtil.delay(500);
        }
        log.info("准备进入指定模式");
        RunModeEnum.valueOf(scriptConfiguration.getProperty(ConfigurationEnum.RUN_MODE.getKey())).getModeEnum().getAbstractModeStrategy().wantEnter();
    }
}
