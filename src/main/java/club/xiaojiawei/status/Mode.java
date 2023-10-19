package club.xiaojiawei.status;

import club.xiaojiawei.enums.ModeEnum;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 游戏当前模式（界面）
 * @author 肖嘉威
 * @date 2022/11/25 0:09
 */
@ToString
@Slf4j
public class Mode{

    @Getter
    private static ModeEnum currMode;
    @Getter
    private static ModeEnum prevMode;

    public static void setCurrMode(ModeEnum currMode) {
        if (currMode == null){
            return;
        }
        if (Mode.currMode != null){
            Mode.currMode.getAbstractModeStrategy().afterLeave();
        }
        Mode.prevMode = Mode.currMode;
        Mode.currMode = currMode;
        Mode.currMode.getAbstractModeStrategy().entering();
    }

    public static void reset(){
        currMode = null;
        prevMode = null;
        log.info("已重置模式状态");
    }

}
