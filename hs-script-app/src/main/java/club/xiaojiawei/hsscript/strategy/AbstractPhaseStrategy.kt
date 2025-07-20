package club.xiaojiawei.hsscript.strategy

import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscriptbase.enums.StepEnum
import club.xiaojiawei.hsscriptbase.enums.WarPhaseEnum
import club.xiaojiawei.hsscript.bean.log.Block
import club.xiaojiawei.hsscript.bean.log.ExtraEntity
import club.xiaojiawei.hsscript.bean.log.TagChangeEntity
import club.xiaojiawei.hsscript.consts.*
import club.xiaojiawei.hsscript.enums.BlockTypeEnum
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.interfaces.closer.ThreadCloser
import club.xiaojiawei.hsscript.listener.WorkTimeListener
import club.xiaojiawei.hsscript.listener.log.PowerLogListener
import club.xiaojiawei.hsscript.status.TaskManager
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.PowerLogUtil
import club.xiaojiawei.hsscript.utils.PowerLogUtil.dealChangeEntity
import club.xiaojiawei.hsscript.utils.PowerLogUtil.dealFullEntity
import club.xiaojiawei.hsscript.utils.PowerLogUtil.dealShowEntity
import club.xiaojiawei.hsscript.utils.PowerLogUtil.dealTagChange
import club.xiaojiawei.hsscript.utils.PowerLogUtil.isRelevance
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.hsscriptbase.interfaces.PhaseStrategy
import club.xiaojiawei.status.WAR
import club.xiaojiawei.hsscriptbase.util.isTrue
import java.io.IOException

/**
 * 游戏阶段抽象类
 * @author 肖嘉威
 * @date 2022/11/26 17:59
 */
abstract class AbstractPhaseStrategy : PhaseStrategy {

    protected val war = WAR

    override fun deal(line: String) {
        dealing = true
        try {
            beforeDeal()
            dealLog(line)
            afterDeal()
        } finally {
            dealing = false
        }
    }

    private fun dealLog(line: String) {
        val accessFile = PowerLogListener.logFile
        accessFile ?: return
        var l: String? = line
        while (WorkTimeListener.working) {
            try {
                if (l == null) {
                    SystemUtil.delay(100)
                } else if (isRelevance(l)) {
                    log.debug { l }
                    if (l.contains(TAG_CHANGE)) {
                        if (dealTagChangeThenIsOver(
                                l, dealTagChange(l)
                            ) || war.currentTurnStep == StepEnum.FINAL_GAMEOVER
                        ) {
                            break
                        }
                    } else if (l.contains(SHOW_ENTITY)) {
                        if (dealShowEntityThenIsOver(l, dealShowEntity(l, accessFile))) {
                            break
                        }
                    } else if (l.contains(FULL_ENTITY)) {
                        if (dealFullEntityThenIsOver(l, dealFullEntity(l, accessFile))) {
                            break
                        }
                    } else if (l.contains(CHANGE_ENTITY)) {
                        if (dealChangeEntityThenIsOver(l, dealChangeEntity(l, accessFile))) {
                            break
                        }
                    } else if (l.contains(BLOCK_TYPE) || l.contains(BLOCK_START_NULL)) {
                        if (dealBlockIsOver(l, PowerLogUtil.dealBlock(l))) {
                            break
                        }
                    } else if (l.contains(BLOCK_END) || l.contains(BLOCK_END_NULL)) {
                        if (dealBlockEndIsOver(l, PowerLogUtil.dealBlockEnd(l))) {
                            break
                        }
                    } else {
                        if (dealOtherThenIsOver(l)) {
                            break
                        }
                    }
                }
                l = accessFile.readLine()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

    protected fun beforeDeal() {
        WarPhaseEnum.find(this)?.let {
            log.info { "当前处于：" + it.comment }
        }
    }

    protected fun afterDeal() {
        WarPhaseEnum.find(this)?.let {
            log.info { it.comment + " -> 结束" }
        }
    }

    protected open fun dealTagChangeThenIsOver(line: String, tagChangeEntity: TagChangeEntity): Boolean {
        return false
    }

    protected open fun dealShowEntityThenIsOver(line: String, extraEntity: ExtraEntity): Boolean {
        return false
    }

    protected open fun dealFullEntityThenIsOver(line: String, extraEntity: ExtraEntity): Boolean {
        return false
    }

    protected open fun dealChangeEntityThenIsOver(line: String, extraEntity: ExtraEntity): Boolean {
        return false
    }

    protected open fun dealBlockIsOver(line: String, block: Block): Boolean {
        return false
    }

    protected open fun dealBlockEndIsOver(line: String, block: Block?): Boolean {
        if (ConfigUtil.getBoolean(ConfigEnum.KILLED_SURRENDER) && !WAR.isMyTurn && block != null) {
            if (block.blockType === BlockTypeEnum.ATTACK || block.blockType === BlockTypeEnum.POWER) {
                GameUtil.triggerCalcMyDeadLine()
            }
        }
        return false
    }

    protected open fun dealOtherThenIsOver(line: String): Boolean {
        return false
    }

    companion object : ThreadCloser {

        init {
            TaskManager.addTask(this)
        }

        var dealing = false
        private val tasks: MutableList<Thread> = mutableListOf()

        fun addTask(task: Thread) {
            tasks.add(task)
        }

        fun cancelAllTask() {
            val toList = tasks.toList()
            tasks.clear()
            toList.forEach {
                it.isAlive.isTrue {
                    it.interrupt()
                }
            }
        }

        override fun stopAll() {
            cancelAllTask()
        }
    }

}
