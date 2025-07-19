package club.xiaojiawei.util

import club.xiaojiawei.hsscript.enums.MouseControlModeEnum
import club.xiaojiawei.hsscript.initializer.GamePathInitializer
import club.xiaojiawei.hsscript.starter.GameStarter
import club.xiaojiawei.hsscript.starter.PlatformStarter
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.strategy.mode.TournamentModeStrategy
import club.xiaojiawei.hsscript.utils.ConfigExUtil
import club.xiaojiawei.hsscript.utils.GameUtil.getChooseOneCardRect
import club.xiaojiawei.hsscript.utils.GameUtil.updateGameRect
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.test.Test

/**
 * @author 肖嘉威
 * @date 2025/6/23 19:37
 */
@ExtendWith(GameUtilTest.Callback::class)
class GameUtilTest {

    private class Callback : BeforeAllCallback, AfterAllCallback {
        override fun beforeAll(p0: ExtensionContext?) {
//            注意拷贝script.ini
            ScriptStatus.testMode = true
            GamePathInitializer().init()
            ConfigExUtil.storeMouseControlMode(MouseControlModeEnum.MESSAGE)
            val starter = PlatformStarter()
            starter.setNextStarter(GameStarter())
            starter.start()
            updateGameRect()
        }

        override fun afterAll(p0: ExtensionContext?) {
        }
    }

    @Disabled
    @Test
    fun testGetChooseOneCardRect() {
        getChooseOneCardRect(0).lClick()
    }

    @Test
    fun testClick(){
        TournamentModeStrategy.TOURNAMENT_MODE_RECT.lClick()
    }

}