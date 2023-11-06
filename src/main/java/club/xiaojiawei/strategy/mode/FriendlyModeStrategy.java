package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.enums.ConfigurationEnum;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.status.Work;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.SystemUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Properties;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/6 15:40
 */
@Component
public class FriendlyModeStrategy extends AbstractModeStrategy<Object> {
    @Resource
    private TournamentModeStrategy tournamentModeStrategy;
    @Resource
    private Properties scriptConfiguration;
    @Override
    public void wantEnter() {

    }

    @Override
    protected void afterEnter(Object o) {
        if (Work.canWork()){
            SystemUtil.updateGameRect();
            DeckEnum currentDeck = DeckEnum.valueOf(scriptConfiguration.getProperty(ConfigurationEnum.DECK.getKey()));
            tournamentModeStrategy.selectDeck(currentDeck);
            SystemUtil.delayShort();
            tournamentModeStrategy.startMatching();
        }else {
            Work.stopWork();
        }
    }
}
