package club.xiaojiawei.enums

/**
 * @author 肖嘉威
 * @date 2025/1/18 14:35
 */
enum class TargetEnum(val types: Set<CardTypeEnum>) {

    MINION(setOf(CardTypeEnum.MINION)),

    HERO_MINION(setOf(CardTypeEnum.HERO, CardTypeEnum.MINION)),

    NONE(emptySet()),

}