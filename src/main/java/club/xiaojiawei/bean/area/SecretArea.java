package club.xiaojiawei.bean.area;

import club.xiaojiawei.bean.Card;

/**
 * 奥秘区
 * @author 肖嘉威
 * @date 2022/11/28 20:02
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
