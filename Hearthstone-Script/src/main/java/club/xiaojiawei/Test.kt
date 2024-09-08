package club.xiaojiawei

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.DefaultCardAction
import club.xiaojiawei.mapper.BaseCardMapper
import club.xiaojiawei.mapper.EntityMapper


/**
 * @author 肖嘉威
 * @date 2024/9/8 14:08
 */
class Test {

    fun test(){
        var sourceCard = Card(DefaultCardAction.DEFAULT)
        sourceCard.entityName = "hello"
        sourceCard.cardId = "dfdsfdfs"
        sourceCard.entityId = "dfs_df"
        sourceCard.isAura = true
        var targetCard = Card(DefaultCardAction.DEFAULT)

        BaseCardMapper.INSTANCE.update(sourceCard, targetCard)
//        EntityMapper.INSTANCE.update(sourceCard, targetCard)
        println("entityId:" + targetCard.entityId)
        println("cardId:" + targetCard.cardId)
        println("entityName:" + targetCard.entityName)
        println("isAura:" + targetCard.isAura)
    }
}