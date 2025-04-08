package club.xiaojiawei.hsscript.enums

import club.xiaojiawei.hsscript.consts.GAME_CN_NAME
import club.xiaojiawei.hsscript.consts.PLATFORM_CN_NAME

/**
 * @author 肖嘉威
 * @date 2025/4/8 15:19
 */
enum class TimeOperateEnum(val value: String) {

    SLEEP_SYSTEM("睡眠"),
    OFF_SCREEN("关闭屏幕"),
    LOCK_SCREEN("锁屏"),
    CLOSE_GAME("关闭${GAME_CN_NAME}"),
    CLOSE_PLATFORM("关闭${PLATFORM_CN_NAME}"),
    ;

}