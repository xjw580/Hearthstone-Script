package club.xiaojiawei.strategy.phase

import club.xiaojiawei.bean.log.ExtraEntity
import club.xiaojiawei.bean.log.TagChangeEntity
import club.xiaojiawei.listener.log.PowerLogListener
import club.xiaojiawei.status.War.endWar
import club.xiaojiawei.status.War.isMyTurn
import club.xiaojiawei.strategy.AbstractPhaseStrategy
import club.xiaojiawei.utils.GameUtil.addGameEndTask
import club.xiaojiawei.utils.GameUtil.hidePlatformWindow
import club.xiaojiawei.utils.SystemUtil
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Component
import java.io.IOException
import java.io.RandomAccessFile

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
        hidePlatformWindow()
        isMyTurn = false
        cancelAllTask()
        endWar()
        try {
            SystemUtil.delay(1000)
            val accessFile = PowerLogListener.logFile
            accessFile?.seek(accessFile.length())
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        addGameEndTask()
    }
}
