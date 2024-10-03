package club.xiaojiawei.strategy

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.isValid
import club.xiaojiawei.bean.log.ExtraEntity
import club.xiaojiawei.bean.log.TagChangeEntity
import club.xiaojiawei.config.log
import club.xiaojiawei.data.ScriptStaticData
import club.xiaojiawei.enums.StepEnum
import club.xiaojiawei.enums.WarPhaseEnum
import club.xiaojiawei.interfaces.PhaseStrategy
import club.xiaojiawei.listener.log.PowerLogListener
import club.xiaojiawei.status.PauseStatus
import club.xiaojiawei.status.War.currentPhase
import club.xiaojiawei.status.War.currentTurnStep
import club.xiaojiawei.status.War.me
import club.xiaojiawei.strategy.DeckStrategyActuator.discoverChooseCard
import club.xiaojiawei.util.isTrue
import club.xiaojiawei.utils.PowerLogUtil.dealChangeEntity
import club.xiaojiawei.utils.PowerLogUtil.dealFullEntity
import club.xiaojiawei.utils.PowerLogUtil.dealShowEntity
import club.xiaojiawei.utils.PowerLogUtil.dealTagChange
import club.xiaojiawei.utils.PowerLogUtil.isRelevance
import club.xiaojiawei.utils.SystemUtil
import java.io.IOException

/**
 * 游戏阶段抽象类
 * @author 肖嘉威
 * @date 2022/11/26 17:59
 */
abstract class AbstractPhaseStrategy : PhaseStrategy {

    private var lastDiscoverEntityId: String? = null

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
        var l:String? = line
        var mark: Long
        while (!PauseStatus.isPause) {
            try {
                if (l == null) {
                    mark = accessFile.filePointer
                    SystemUtil.delay(1000)
                    if (accessFile.length() <= mark && me.isValid()) {
                        val cards: List<Card> = me.setasideArea.cards
                        val size = cards.size
                        if (size >= 3 && lastDiscoverEntityId != cards.last().entityId && cards[size - 1].creator == cards[size - 2].creator
                            && cards[size - 1].creator == cards[size - 3].creator
                        ) {
                            lastDiscoverEntityId = cards.last().entityId
                            if (currentPhase != WarPhaseEnum.REPLACE_CARD) {
                                log.info { "触发发现动作" }
                                discoverChooseCard(
                                    cards[size - 3],
                                    cards[size - 2],
                                    cards[size - 1]
                                )
                            }
                        }
                    }
                } else if (isRelevance(l)) {
                    log.debug { l }
                    if (l.contains(ScriptStaticData.TAG_CHANGE)) {
                        if (dealTagChangeThenIsOver(
                                l,
                                dealTagChange(l)
                            ) || currentTurnStep == StepEnum.FINAL_GAMEOVER
                        ) {
                            break
                        }
                    } else if (l.contains(ScriptStaticData.SHOW_ENTITY)) {
                        if (dealShowEntityThenIsOver(l, dealShowEntity(l, accessFile))) {
                            break
                        }
                    } else if (l.contains(ScriptStaticData.FULL_ENTITY)) {
                        if (dealFullEntityThenIsOver(l, dealFullEntity(l, accessFile))) {
                            break
                        }
                    } else if (l.contains(ScriptStaticData.CHANGE_ENTITY)) {
                        if (dealChangeEntityThenIsOver(l, dealChangeEntity(l, accessFile))) {
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

    protected open fun dealOtherThenIsOver(line: String): Boolean {
        return false
    }

    companion object{
        var dealing = false
        private val tasks:MutableList<Thread> = mutableListOf()

        fun addTask(task:Thread) {
            tasks.add(task)
        }

        fun cancelAllTask(){
            val toList = tasks.toList()
            tasks.clear()
            toList.forEach {
                it.isAlive.isTrue {
                    it.interrupt()
                }
            }
        }
    }

}
