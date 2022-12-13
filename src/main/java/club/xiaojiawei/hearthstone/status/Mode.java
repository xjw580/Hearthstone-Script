package club.xiaojiawei.hearthstone.status;

import club.xiaojiawei.hearthstone.enums.ModeEnum;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

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
        Mode.prevMode = Mode.currMode;
        Mode.currMode = currMode;
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
    }

}
