package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.data.GameRationStaticData;
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
        log.info("点击弹窗（去除任务，活动等）中……");
        for (int i = 0; i < 4; i++) {
            if (isPause.get().get()){
                return;
            }
            SystemUtil.updateRect(ScriptStaticData.getGameHWND(), ScriptStaticData.GAME_RECT);
            mouseUtil.leftButtonClick(
                    (ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left) >> 1,
                    (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * GameRationStaticData.CONFIRM_OR_CLOSE_BUTTON_VERTICAL_TO_BOTTOM_RATION)
            );
            SystemUtil.delay(500);
        }
        log.info("准备进入指定模式");
        RunModeEnum.valueOf(scriptProperties.getProperty(ConfigurationKeyEnum.RUN_MODE_KEY.getKey())).getModeEnum().getAbstractModeStrategy().wantEnter();
    }
}
