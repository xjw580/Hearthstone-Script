package club.xiaojiawei.custom;

import club.xiaojiawei.entity.Card;
import club.xiaojiawei.entity.Player;
import club.xiaojiawei.entity.TagChangeEntity;
import club.xiaojiawei.entity.area.Area;

/**
 * @author 肖嘉威
 * @date 2023/9/18 13:26
 * @msg 处理这种日志：TAG_CHANGE Entity=Echo#32508 tag=TIMEOUT value=75
 * 和 TAG_CHANGE Entity=[entityName=图腾召唤 id=70 zone=PLAY zonePos=0 cardId=HERO_02bp player=2] tag=EXHAUSTED value=0
 */
@FunctionalInterface
public interface DealTagChange {
    default void dealTagChange(TagChangeEntity tagChangeEntity){
        dealTagChange(null, tagChangeEntity, null, null);
    }
    void dealTagChange(Card card, TagChangeEntity tagChangeEntity, Player player, Area area);
}
