package club.xiaojiawei.bean.area;

import club.xiaojiawei.bean.Card;
import club.xiaojiawei.bean.Player;

/**
 * 牌库区
 * @author 肖嘉威
 * @date 2022/11/27 15:02
 */
public class DeckArea extends Area {

    public DeckArea(Player player) {
        super(60, player);
    }

    @Override
    protected void addZeroCard(Card card) {
        add(card);
    }

}
