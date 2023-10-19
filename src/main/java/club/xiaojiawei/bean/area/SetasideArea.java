package club.xiaojiawei.bean.area;

import club.xiaojiawei.bean.entity.Card;

/**
 * 除外区（各种效果在此生成）,发现时，最新的三张就是需要选择的牌
 * @author 肖嘉威
 * @date 2022/11/30 14:36
 */
public class SetasideArea extends Area{
    public SetasideArea() {
        super(Integer.MAX_VALUE);
    }
    @Override
    public void addZeroCard(Card card) {
        add(card);
    }

}
