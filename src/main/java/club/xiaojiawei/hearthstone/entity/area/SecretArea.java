package club.xiaojiawei.hearthstone.entity.area;

import club.xiaojiawei.hearthstone.entity.Card;

/**
 * @author 肖嘉威
 * @date 2022/11/28 20:02
 */
public class SecretArea extends Area{
    public SecretArea() {
        super(5);
    }

    @Override
    public void putZeroAreaCard(Card card) {
        add(card);
    }
}
