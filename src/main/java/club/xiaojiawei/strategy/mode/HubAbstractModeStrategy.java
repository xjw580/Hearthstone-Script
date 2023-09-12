package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.data.GameStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ConfigurationKeyEnum;
import club.xiaojiawei.enums.RunModeEnum;
import club.xiaojiawei.listener.PowerFileListener;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Properties;

/**
 * @author 肖嘉威
 * @date 2022/11/25 12:36
 */
@Slf4j
@Component
public class HubAbstractModeStrategy extends AbstractModeStrategy<Object> {

    @Resource
    private Properties scriptProperties;
    @Override
    public void wantEnter() {

    }

    @Override
    protected void beforeEnter() {
        PowerFileListener.cancelListener();
        super.beforeEnter();
    }

    @Override
    protected void afterEnter(Object o) {
//        点击弹窗（去除任务，活动等）
        for (int i = 0; i < 4; i++) {
            if (isPause.get().get()){
                return;
            }
            SystemUtil.frontWindow(ScriptStaticData.getGameHWND());
            SystemUtil.updateRect(ScriptStaticData.getGameHWND(), ScriptStaticData.GAME_RECT);
            mouseUtil.leftButtonClick(
                    (ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left) >> 1,
                    (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * GameStaticData.CONFIRM_OR_CLOSE_BUTTON_VERTICAL_TO_BOTTOM_RATION)
            );
            log.info("点击弹窗（去除任务，活动等）中……");
            ScriptStaticData.ROBOT.delay(500);
        }
        SystemUtil.frontWindow(ScriptStaticData.getGameHWND());
        log.info("准备进入指定模式");
        RunModeEnum.valueOf(scriptProperties.getProperty(ConfigurationKeyEnum.RUN_MODE_KEY.getKey())).getModeEnum().getAbstractModeStrategy().wantEnter();
    }
}
