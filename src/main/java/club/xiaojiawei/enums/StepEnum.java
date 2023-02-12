package club.xiaojiawei.enums;

/**
 * @author 肖嘉威
 * @date 2022/11/29 20:28
 */
public enum StepEnum {
    BEGIN_MULLIGAN("BEGIN_MULLIGAN", "调度开始"),
    MAIN_READY("MAIN_READY", "主游戏-准备"),
    MAIN_START_TRIGGERS("MAIN_START_TRIGGERS", "主游戏-触发"),
    MAIN_START("MAIN_START", "主游戏-步骤开始"),
    MAIN_ACTION("MAIN_ACTION", "主游戏-动作"),
    MAIN_END("MAIN_END", "主游戏-步骤结束"),
    MAIN_CLEANUP("MAIN_CLEANUP", "主游戏-清除"),
    MAIN_NEXT("MAIN_NEXT", "主游戏-下一步骤"),
    FINAL_GAMEOVER("FINAL_GAMEOVER", "最后阶段-游戏结束"),
    FINAL_WRAPUP("FINAL_WRAPUP", "最后阶段-收尾")
    ;
    private final String value;
    private final String comment;

    StepEnum(String value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    public String getValue() {
        return value;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "StepEnum{" +
                "value='" + value + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
