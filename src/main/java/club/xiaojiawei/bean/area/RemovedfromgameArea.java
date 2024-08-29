package club.xiaojiawei.bean.area;

import club.xiaojiawei.bean.Card;

/**
 * 移除区
 * @author 肖嘉威
 * @date 2022/12/3 21:37
 */
public class RemovedfromgameArea extends Area{
    public RemovedfromgameArea() {
        super(Integer.MAX_VALUE);
    }
    @Override
    protected void addZeroCard(Card card) {
        add(card);
    }
}
