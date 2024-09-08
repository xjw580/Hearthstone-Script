package club.xiaojiawei.bean.area;

import club.xiaojiawei.bean.Card;
import club.xiaojiawei.bean.Player;

/**
 * 奥秘区
 *
 * @author 肖嘉威
 * @date 2022/11/28 20:02
 */
public class SecretArea extends Area {
    public SecretArea(Player player) {
        super(5, player);
    }

    @Override
    public void addZeroCard(Card card) {
        add(card);
    }
}
