package club.xiaojiawei.hsscript.strategy.phase

import club.xiaojiawei.hsscript.bean.log.ExtraEntity
import club.xiaojiawei.hsscript.bean.log.TagChangeEntity
import club.xiaojiawei.hsscript.bean.single.WarEx
import club.xiaojiawei.hsscript.listener.log.PowerLogListener
import club.xiaojiawei.hsscript.strategy.AbstractPhaseStrategy
import club.xiaojiawei.hsscript.utils.GameUtil.addGameEndTask
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.status.War.isMyTurn
import java.io.IOException

/**
 * 游戏结束阶段
 * @author 肖嘉威
 * @date 2022/11/27 13:44
 */
object GameOverPhaseStrategy : AbstractPhaseStrategy() {
    override fun dealTagChangeThenIsOver(line: String, tagChangeEntity: TagChangeEntity): Boolean {
        over()
        return true
    }

    override fun dealShowEntityThenIsOver(line: String, extraEntity: ExtraEntity): Boolean {
        over()
        return true
    }

    override fun dealFullEntityThenIsOver(line: String, extraEntity: ExtraEntity): Boolean {
        over()
        return true
    }

    override fun dealChangeEntityThenIsOver(line: String, extraEntity: ExtraEntity): Boolean {
        over()
        return true
    }

    override fun dealOtherThenIsOver(line: String): Boolean {
        over()
        return true
    }

    private fun over() {
        isMyTurn = false
        cancelAllTask()
        WarEx.endWar()
        try {
            SystemUtil.delay(1000)
            val accessFile = PowerLogListener.logFile
            accessFile?.seek(accessFile.length())
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        addGameEndTask()
        WarEx.reset()
    }
}
