package club.xiaojiawei.hsscript.interfaces

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.area.Area
import club.xiaojiawei.hsscript.bean.log.TagChangeEntity

/**
 * 处理这种日志：TAG_CHANGE Entity=Echo#32508 tag=TIMEOUT value=75 和 TAG_CHANGE Entity=[entityName=图腾召唤 id=70 zone=PLAY zonePos=0 cardId=HERO_02bp player=2] tag=EXHAUSTED value=0
 * @author 肖嘉威
 * @date 2023/9/18 13:26
 */
fun interface TagChangeHandler {

    fun handle(tagChangeEntity: TagChangeEntity){
        handle(null, tagChangeEntity, null, null)
    }

    fun handle(card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area?)

}
