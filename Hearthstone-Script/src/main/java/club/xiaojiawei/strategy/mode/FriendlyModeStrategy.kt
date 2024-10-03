package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.status.DeckStrategyManager;
import club.xiaojiawei.status.Work;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

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
        if (Work.isDuringWorkDate()){
            SystemUtil.updateGameRect();
            tournamentModeStrategy.selectDeck(DeckStrategyManager.CURRENT_DECK_STRATEGY.get());
            SystemUtil.delayShort();
            tournamentModeStrategy.startMatching();
        }else {
            Work.stopWork();
        }
    }

}
