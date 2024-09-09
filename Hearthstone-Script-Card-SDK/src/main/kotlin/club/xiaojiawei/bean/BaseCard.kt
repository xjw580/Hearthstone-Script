package club.xiaojiawei.bean

import club.xiaojiawei.custom.CustomToStringGenerator.generateToString
import club.xiaojiawei.enums.CardRaceEnum
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.mapper.BaseCardMapper
import kotlin.concurrent.Volatile

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/8/29 10:25
 */
open class BaseCard : Entity(), Cloneable {

    @Volatile
    var cardType: CardTypeEnum = CardTypeEnum.UNKNOWN

    @Volatile
    var cost = 0

    @Volatile
    var atc = 0

    @Volatile
    var health = 0

    @Volatile
    var armor = 0

    @Volatile
    var damage = 0

    /**
     * 相邻增益
     */
    @Volatile
    var isAdjacentBuff = false

    /**
     * 剧毒
     */
    @Volatile
    var isPoisonous = false

    /**
     * 亡语
     */
    @Volatile
    var isDeathRattle = false

    /**
     * 创建者id
     */
    @Volatile
    var creatorEntityId: String = ""

    /**
     * 嘲讽
     */
    @Volatile
    var isTaunt = false

    /**
     * 圣盾
     */
    @Volatile
    var isDivineShield = false

    /**
     * 光环
     */
    @Volatile
    var isAura = false

    /**
     * 潜行
     */
    @Volatile
    var isStealth = false

    /**
     * 冰冻
     */
    @Volatile
    var isFrozen = false

    /**
     * 疲劳
     */
    @Volatile
    var isExhausted = false

    /**
     * 风怒
     */
    @Volatile
    var isWindFury = false

    /**
     * 战吼
     */
    @Volatile
    var isBattlecry = false

    /**
     * 发现
     */
    @Volatile
    var isDiscover = false

    /**
     * 不能被法术指向
     */
    @Volatile
    var isCantBeTargetedBySpells = false

    /**
     * 不能被英雄技能指向
     */
    @Volatile
    var isCantBeTargetedByHeroPowers = false

    /**
     * 刷出时间计数
     */
    @Volatile
    var isSpawnTimeCount = false

    /**
     * 休眠
     */
    @Volatile
    var isDormantAwakenConditionEnchant = false

    /**
     * 免疫
     */
    @Volatile
    var isImmune = false

    /**
     * 种族
     */
    @Volatile
    var cardRace: CardRaceEnum = CardRaceEnum.UNKNOWN

    /**
     * 磁力
     */
    @Volatile
    var isModular = false

    @Volatile
    var creator: String = ""

    /**
     * 衍生物
     */
    @Volatile
    var isPremium = false

    @Volatile
    var controller: String = ""

    /**
     * 泰坦
     */
    @Volatile
    var isTitan = false

    @Volatile
    var spellPower = 0

    @Volatile
    var isDormant = false

    public override fun clone(): BaseCard {
        try {
            val card = super.clone() as BaseCard
            BaseCardMapper.INSTANCE.update(this, card)

            return card
        } catch (e: CloneNotSupportedException) {
            throw RuntimeException(e)
        }

    }

    override fun toString(): String {
        return generateToString(this, true)
    }

    fun toSimpleString(): String {
        return "【entityId:$entityId，entityName:$entityName，cardId:${cardId}】"
    }
}
