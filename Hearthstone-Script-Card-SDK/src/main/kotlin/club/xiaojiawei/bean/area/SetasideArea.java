package club.xiaojiawei.bean.area;

import club.xiaojiawei.bean.Card;
import club.xiaojiawei.bean.Player;

/**
 * 除外区（各种效果在此生成）,发现时，最新的三张就是需要选择的牌
 * @author 肖嘉威
 * @date 2022/11/30 14:36
 */
public class SetasideArea extends Area{
    public SetasideArea(Player player) {
        super(Integer.MAX_VALUE, player);
    }
    @Override
    public void addZeroCard(Card card) {
        add(card);
    }

}
