package club.xiaojiawei.bean.area;

import club.xiaojiawei.bean.Card;
import club.xiaojiawei.bean.Player;

/**
 * 墓地
 * @author 肖嘉威
 * @date 2022/11/27 15:02
 */
public class GraveyardArea extends Area {

    public GraveyardArea(Player player) {
        super(Integer.MAX_VALUE, player);
    }

    @Override
    protected void addZeroCard(Card card) {
        add(card);
    }

}
