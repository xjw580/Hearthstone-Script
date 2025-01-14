package club.xiaojiawei.hsscript.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.area.PlayArea
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.status.WAR
import club.xiaojiawei.util.isTrue
import kotlin.math.min

/**
 * 通用卡牌操作
 * @author 肖嘉威
 * @date 2024/9/5 22:42
 */
class CommonCardAction : CardAction(false) {

    private var lastRect: GameRect? = null

    private fun getCardRect(card: Card?): GameRect {
        if (card == null) {
            return GameRect.INVALID
        }
        val me = WAR.me
        val rival = WAR.rival
        val area = card.area
        var index: Int
        if (card === me.playArea.hero) {
            return GameUtil.MY_HERO_RECT
        } else if (card === rival.playArea.hero) {
            return GameUtil.RIVAL_HERO_RECT
        } else if (card === me.playArea.power) {
            return GameUtil.MY_POWER_RECT
        } else if (card === rival.playArea.power) {
            return GameUtil.RIVAL_POWER_RECT
        } else if (area === me.playArea) {
            if ((area.indexOfCard(card).also { index = it }) >= 0) {
                return GameUtil.getMyPlayCardRect(index, area.cardSize())
            }
        } else if (area === rival.playArea) {
            if ((area.indexOfCard(card).also { index = it }) >= 0) {
                return GameUtil.getRivalPlayCardRect(index, area.cardSize())
            }
        } else if (area === me.handArea) {
            if ((area.indexOfCard(card).also { index = it }) >= 0) {
                return GameUtil.getMyHandCardRect(index, area.cardSize())
            }
        }
        return GameRect.INVALID
    }

    override fun getCardId(): Array<String> {
        return emptyArray()
    }

    public override fun execPower(): Boolean {
        return WAR.me.let {
            execPower(min((it.playArea.cardSize()), it.playArea.maxSize - 1))
        }
    }

    public override fun execPower(card: Card): Boolean {
        var startRect: GameRect
        if ((GameUtil.getMyHandCardRect(WAR.me.handArea.indexOfCard(belongCard), belongCard!!.area!!.cardSize())
                .also { startRect = it }).isValid()
        ) {
            if (card.area is PlayArea) {
                val endRect = getCardRect(card)
                if (endRect.isValid()) {
                    startRect.lClickMoveLClick(endRect)
                    lastRect = endRect
                    return true
                }
            }
        }
        return false
    }

    public override fun execPower(index: Int): Boolean {
        var startRect: GameRect
        val me = WAR.me
        if ((GameUtil.getMyHandCardRect(me.handArea.indexOfCard(belongCard), belongCard!!.area!!.cardSize())
                .also { startRect = it }).isValid()
        ) {
            val endRect = GameUtil.getMyPlayCardRect(
                index,
                me.playArea.cardSize()
            )
            if (endRect.isValid()) {
                startRect.lClickMoveLClick(endRect)
                lastRect = endRect
                return true
            }
        }
        return false
    }

    public override fun execAttack(card: Card): Boolean {
        val me = WAR.me
        val rival = WAR.rival
        val startRect = if (belongCard === me.playArea.hero) {
            GameUtil.MY_HERO_RECT
        } else {
            GameUtil.getMyPlayCardRect(me.playArea.indexOfCard(belongCard), belongCard!!.area!!.cardSize())
        }
        if (startRect.isValid()) {
            if (card.area === rival.playArea) {
                val endRect = getCardRect(card)
                if (endRect.isValid()) {
                    startRect.lClickMoveLClick(endRect)
                    lastRect = endRect
                    return true
                }
            }
        }
        return false
    }

    public override fun execAttackHero(): Boolean {
        val me = WAR.me
        val startRect = if (belongCard === me.playArea.hero) {
            GameUtil.MY_HERO_RECT
        } else {
            GameUtil.getMyPlayCardRect(me.playArea.indexOfCard(belongCard), belongCard!!.area!!.cardSize())
        }
        if (startRect.isValid()) {
            if (belongCard!!.area == me.playArea) {
                startRect.lClickMoveLClick(GameUtil.RIVAL_HERO_RECT)
                lastRect = GameUtil.RIVAL_HERO_RECT
                return true
            }
        }
        return false
    }

    public override fun execPointTo(card: Card, click: Boolean): Boolean {
        var startRect: GameRect? = null
        var endRect: GameRect? = null
        lastRect?.let {
            if (it.isValid()) {
                startRect = it
            }
        }
        if (startRect == null) {
            startRect = getCardRect(belongCard)
        }
        startRect.let {
            if (it.isValid()) {
                var cardRect = belongCard?.area?.let { area ->
                    if (area === WAR.me.handArea && belongCard?.cardType === CardTypeEnum.MINION) {
                        var index = -1
                        if ((area.indexOfCard(card).also { i -> index = i }) >= 0) {
                            GameUtil.getMyHandCardRect(index, area.cardSize() + if (depth > 0) 1 else 0)
                        }
                    }
                    GameRect.INVALID
                } ?: GameRect.INVALID
                if (!cardRect.isValid()) {
                    cardRect = getCardRect(card)
                }
                if (cardRect.isValid()) {
                    endRect = cardRect
                    it.move(endRect)
                    click.isTrue {
                        cardRect.lClick(false)
                    }
                }
            }
        }
        lastRect = endRect
        return endRect != null && endRect.isValid()
    }

    override fun execPointTo(index: Int, click: Boolean): Boolean {
        var startRect: GameRect? = null
        var endRect: GameRect? = null
        lastRect?.let {
            if (it.isValid()) {
                startRect = it
            }
        }
        if (startRect == null) {
            startRect = getCardRect(belongCard)
        }
        startRect.let {
            if (it.isValid()) {
                var cardRect = belongCard?.area?.let { area ->
                    if (area === WAR.me.handArea && belongCard?.cardType === CardTypeEnum.MINION) {
                        GameUtil.getMyHandCardRect(index, area.cardSize() + if (depth > 0) 1 else 0)
                    }
                    GameRect.INVALID
                } ?: GameRect.INVALID
                if (cardRect.isValid()) {
                    endRect = cardRect
                    it.move(endRect)
                    click.isTrue {
                        cardRect.lClick(false)
                    }
                }
            }
        }
        lastRect = endRect
        return endRect != null && endRect.isValid()
    }

    override fun createNewInstance(): CardAction {
        return CommonCardAction()
    }

    override fun lClick(): Boolean {
        val cardRect = getCardRect(belongCard)
        if (cardRect.isValid()) {
            cardRect.lClick()
            return true
        }
        return false
    }

    companion object {
        val DEFAULT: CardAction = CommonCardAction()

        fun reload() {
            mouseActionInterval = ConfigUtil.getInt(ConfigEnum.MOUSE_ACTION_INTERVAL)
        }

        init {
            reload()
        }
    }
}
