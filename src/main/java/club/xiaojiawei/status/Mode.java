package club.xiaojiawei.status;

import club.xiaojiawei.enums.ModeEnum;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 肖嘉威
 * @date 2022/11/25 0:09
 * 当前模式
 */
@ToString
@Slf4j
public class Mode{

    private static ModeEnum currMode;
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

    public static ModeEnum getCurrMode() {
        return currMode;
    }

    public static ModeEnum getPrevMode() {
        return prevMode;
    }

    public static void reset(){
        currMode = null;
        prevMode = null;
        log.info("已重置模式状态");
    }

}
