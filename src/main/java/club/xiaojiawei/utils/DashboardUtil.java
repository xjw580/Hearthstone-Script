package club.xiaojiawei.utils;

import club.xiaojiawei.enums.DeckEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Properties;

import static club.xiaojiawei.enums.ConfigurationKeyEnum.DECK_KEY;
import static club.xiaojiawei.enums.ConfigurationKeyEnum.RUN_MODE_KEY;

/**
 * @author 肖嘉威
 * @date 2023/9/11 22:08
 * @msg
 */
@Component
@Slf4j
public class DashboardUtil {
    @Resource
    private Properties scriptConfiguration;
    @Resource
    private PropertiesUtil propertiesUtil;
    public void changeDeck(String deckComment){
        if (!Objects.equals(DeckEnum.valueOf(scriptConfiguration.getProperty(DECK_KEY.getKey())).getComment(), deckComment)){
            scriptConfiguration.setProperty(RUN_MODE_KEY.getKey(), DeckEnum.valueOf(scriptConfiguration.getProperty(DECK_KEY.getKey())).getRunMode().getValue());
            for (DeckEnum anEnum : DeckEnum.values()) {
                if (Objects.equals(deckComment, anEnum.getComment())){
                    scriptConfiguration.setProperty(DECK_KEY.getKey(), anEnum.getValue());
                    break;
                }
            }
            propertiesUtil.storeScriptProperties();
            SystemUtil.notice("挂机卡组改为：" + deckComment);
            log.info("挂机卡组改为：" + deckComment);
        }
    }
}
