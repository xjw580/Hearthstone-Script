package club.xiaojiawei.hsscript.enums

/**
 * @author 肖嘉威
 * @date 2022/11/30 17:56
 */
enum class BlockTypeEnum {
    /**
     * 触发
     */
    TRIGGER,

    /**
     * 打出
     */
    PLAY,

    /**
     * 卡牌效果结算
     */
    POWER,

    /**
     * 攻击
     */
    ATTACK,

    /**
     * 死亡
     */
    DEATHS,

    /**
     * 疲劳
     */
    FATIGUE,

    /**
     * 行动
     */
    ACTION,

    /**
     * 连续
     */
    CONTINUOUS,

    /**
     * 游戏复位
     */
    GAME_RESET,

    /**
     * 无效
     */
    INVALID,
}
