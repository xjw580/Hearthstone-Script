package club.xiaojiawei.util

import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.GameUtil.getChooseOneCardRect
import club.xiaojiawei.hsscript.utils.GameUtil.updateGameRect
import kotlin.test.Test

/**
 * @author 肖嘉威
 * @date 2025/6/23 19:37
 */
class GameUtilTest {

    @Test
    fun testGetChooseOneCardRect() {
        ScriptStatus.testMode = true
        updateGameRect()
        getChooseOneCardRect(0).lClick()
    }

}