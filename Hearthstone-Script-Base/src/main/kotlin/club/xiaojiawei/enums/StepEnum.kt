package club.xiaojiawei.enums

import club.xiaojiawei.config.log

/**
 * 游戏回合阶段
 * @author 肖嘉威
 * @date 2022/11/29 20:28
 */
enum class StepEnum(val comment: String) {

    BEGIN_MULLIGAN("调度开始"),
    MAIN_READY("主游戏-准备"),
    MAIN_START_TRIGGERS("主游戏-触发"),
    MAIN_START("主游戏-步骤开始"),
    MAIN_ACTION("主游戏-动作"),
    MAIN_END("主游戏-步骤结束"),
    MAIN_CLEANUP("主游戏-清除"),
    MAIN_NEXT("主游戏-下一步骤"),
    FINAL_WRAPUP("最后阶段-收尾"),
    FINAL_GAMEOVER("最后阶段-游戏结束");

    companion object {
        fun fromString(string: String): StepEnum? {
            return try {
                StepEnum.valueOf(string)
            } catch (_: Exception) {
                log.warn { "未适配${string}" }
                null
            }
        }
    }

}
