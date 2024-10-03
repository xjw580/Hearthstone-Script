package club.xiaojiawei.status

import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ModeEnum
import lombok.Getter
import lombok.ToString
import lombok.extern.slf4j.Slf4j

/**
 * 游戏当前模式（界面）
 * @author 肖嘉威
 * @date 2022/11/25 0:09
 */
object Mode {

    var currMode: ModeEnum? = null

    var prevMode: ModeEnum? = null

    fun setCurrMode(currMode: ModeEnum?) {
        Mode.currMode?.modeStrategy?.afterLeave()
        prevMode = Mode.currMode
        Mode.currMode = currMode
        Mode.currMode?.modeStrategy?.entering()
    }

    fun reset() {
        setCurrMode(null)
        log.info { "已重置模式状态" }
    }
}
