package club.xiaojiawei.entity.area;

import club.xiaojiawei.entity.Card;

/**
 * @author 肖嘉威
 * @date 2022/11/28 20:02
 * @msg 奥秘区
 */
public class SecretArea extends Area{
    public SecretArea() {
        super(5);
    }
    @Override
    public void addZeroCard(Card card) {
        add(card);
    }
}
