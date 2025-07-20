package club.xiaojiawei.enums

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.War
import club.xiaojiawei.util.CardUtil
import club.xiaojiawei.hsscriptbase.util.randomSelect
import club.xiaojiawei.hsscriptbase.util.randomSelectOrNull

/**
 * @author 肖嘉威
 * @date 2025/6/9 15:22
 */
enum class CardActionEnum(
    val comment: String,
    val playExec: (Card, CardEffectTypeEnum, War) -> Boolean = invalidExec,
    val powerExec: (Card, CardEffectTypeEnum, War) -> Boolean = invalidExec,
) {

    POINT_MY_HERO("指向我方英雄", { card, cardEffectTypeEnum, war ->
        var res = false
        if (card.cardType === CardTypeEnum.SPELL) {
            if (war.me.playArea.hero?.canBeTargetedByMySpells() == true) {
                res = card.action.power(war.me.playArea.hero) != null
            }
        } else {
            res = card.action.power(false)?.pointTo(war.me.playArea.hero, true) != null
        }
        res
    }, { card, cardEffectTypeEnum, war ->
        card.action.lClick(false)?.pointTo(war.me.playArea.hero) != null
    }),
    POINT_RIVAL_HERO("指向敌方英雄", { card, cardEffectTypeEnum, war ->
        var res = false
        if (card.cardType === CardTypeEnum.SPELL) {
            if (war.rival.playArea.hero?.canBeTargetedByRivalSpells() == true) {
                res = card.action.power(war.rival.playArea.hero) != null
            }
        } else {
            res = card.action.power(false)?.pointTo(war.rival.playArea.hero, true) != null
        }
        res
    }, { card, cardEffectTypeEnum, war ->
        card.action.lClick(false)?.pointTo(war.rival.playArea.hero) != null
    }),
    POINT_MY_MINION("指向我方随从", { card, cardEffectTypeEnum, war ->
        val cards =
            if (card.cardType === CardTypeEnum.SPELL) war.me.playArea.cards.filter { it.canBeTargetedByMySpells() } else war.me.playArea.cards
        if (cards.isNotEmpty()) {

            if (card.cardType === CardTypeEnum.SPELL) {
                card.action.power(cards.randomSelect()) != null
            } else {
                card.action.power(false)?.pointTo(cards.randomSelect(), true) != null
            }
        } else {
            false
        }
    }, { card, cardEffectTypeEnum, war ->
        val action = war.me.playArea.cards.randomSelectOrNull()?.let { targetCard ->
            card.action.lClick(false)?.pointTo(targetCard)
        } ?: let {
            card.action.lClick(false)
        }
        action != null
    }),
    POINT_RIVAL_MINION("指向敌方随从", { card, cardEffectTypeEnum, war ->
        val cards =
            if (card.cardType === CardTypeEnum.SPELL) war.rival.playArea.cards.filter { it.canBeTargetedByRivalSpells() } else war.rival.playArea.cards
        if (cards.isNotEmpty()) {
            var damage = -1
            CardUtil.getCardText(card.cardId)?.let { text ->
                CardUtil.getDamageValue(text)?.let {
                    damage = it
                }
            }
            val target = if (damage == -1) {
                cards.randomSelect()
            } else {
                cards.filter { it.blood() <= damage }.maxByOrNull { it.blood() + it.atc }
            }
            if (card.cardType === CardTypeEnum.SPELL) {
                card.action.power(target) != null
            } else {
                card.action.power(false)?.pointTo(target, true) != null
            }
        } else {
            false
        }
    }, { card, cardEffectTypeEnum, war ->
        val action = war.rival.playArea.cards.randomSelectOrNull()?.let { targetCard ->
            card.action.lClick(false)?.pointTo(targetCard)
        } ?: let {
            card.action.lClick(false)
        }
        action != null
    }),
    POINT_MY("指向我方", { card, cardEffectTypeEnum, war ->
        val cards =
            (if (card.cardType === CardTypeEnum.SPELL) war.me.playArea.cards.filter { it.canBeTargetedByMySpells() } else war.me.playArea.cards).toMutableList()

        war.me.playArea.hero?.let {
            if (card.cardType !== CardTypeEnum.SPELL || it.canBeTargetedByMySpells()) {
                cards.add(it)
            }
        }

        if (cards.isNotEmpty()) {
            if (card.cardType === CardTypeEnum.SPELL) {
                card.action.power(cards.randomSelect()) != null
            } else {
                card.action.power(false)?.pointTo(cards.randomSelect(), true) != null
            }
        } else {
            false
        }
    }, { card, cardEffectTypeEnum, war ->
        val targetCards = war.me.playArea.cards
        war.me.playArea.hero?.let { hero ->
            targetCards.add(hero)
        }
        val action = targetCards.randomSelectOrNull()?.let { targetCard ->
            card.action.lClick(false)?.pointTo(targetCard)
        } ?: let {
            card.action.lClick(false)
        }
        action != null
    }),
    POINT_RIVAL("指向敌方", { card, cardEffectTypeEnum, war ->
        val cards =
            (if (card.cardType === CardTypeEnum.SPELL) war.rival.playArea.cards.filter { it.canBeTargetedByRivalSpells() } else war.rival.playArea.cards).toMutableList()

        war.rival.playArea.hero?.let {
            if (card.cardType !== CardTypeEnum.SPELL || it.canBeTargetedByRivalSpells()) {
                cards.add(it)
            }
        }

        var damage = -1
        CardUtil.getCardText(card.cardId)?.let {
            CardUtil.getDamageValue(it)?.let {
                damage = it
            }
        }
        val target = if (damage == -1) {
            cards.randomSelect()
        } else {
            cards.filter { it.blood() <= damage }
                .maxByOrNull { if (it.cardType === CardTypeEnum.HERO) Int.MAX_VALUE else (it.blood() + it.atc) }
        }

        if (cards.isNotEmpty()) {
            if (card.cardType === CardTypeEnum.SPELL) {
                card.action.power(target) != null
            } else {
                card.action.power(false)?.pointTo(target, true) != null
            }
        } else {
            false
        }
    }, { card, cardEffectTypeEnum, war ->
        val targetCards = war.rival.playArea.cards
        war.rival.playArea.hero?.let { hero ->
            targetCards.add(hero)
        }
        val action = targetCards.randomSelectOrNull()?.let { targetCard ->
            card.action.lClick(false)?.pointTo(targetCard)
        } ?: let {
            card.action.lClick(false)
        }
        action != null
    }),

    POINT_MINION("指向随从", { card, cardEffectTypeEnum, war ->
        if (cardEffectTypeEnum === CardEffectTypeEnum.BUFF) {
            POINT_MY_MINION.playExec(card, cardEffectTypeEnum, card.area.player.war)
        } else {
            POINT_RIVAL_MINION.playExec(card, cardEffectTypeEnum, card.area.player.war)
        }
    }, { card, cardEffectTypeEnum, war ->
        val targetCards = war.me.playArea.cards + war.rival.playArea.cards
        val action = targetCards.randomSelectOrNull()?.let { targetCard ->
            card.action.lClick(false)?.pointTo(targetCard)
        } ?: let {
            card.action.lClick(false)
        }
        action != null
    }),

    POINT_HERO(
        "指向英雄", { card, cardEffectTypeEnum, war ->
            if (cardEffectTypeEnum === CardEffectTypeEnum.BUFF) {
                POINT_MY_HERO.playExec(card, cardEffectTypeEnum, card.area.player.war)
            } else {
                POINT_RIVAL_HERO.playExec(card, cardEffectTypeEnum, card.area.player.war)
            }
        },
        { card, cardEffectTypeEnum, war ->
            var action: CardAction? = null
            listOf(war.me.playArea.hero, war.rival.playArea.hero).randomSelect()?.let {
                action = card.action.lClick(false)?.pointTo(it)
            }
            action != null
        }),

    POINT_WHATEVER("都可指向", { card, cardEffectTypeEnum, war ->
        if (cardEffectTypeEnum === CardEffectTypeEnum.BUFF) {
            POINT_MY.playExec(card, cardEffectTypeEnum, card.area.player.war)
        } else {
            POINT_RIVAL.playExec(card, cardEffectTypeEnum, card.area.player.war)
        }
    }, { card, cardEffectTypeEnum, war ->
        val targetCards = (war.me.playArea.cards + war.rival.playArea.cards).toMutableList()
        war.me.playArea.hero?.let { hero ->
            targetCards.add(hero)
        }
        war.rival.playArea.hero?.let { hero ->
            targetCards.add(hero)
        }

        val action = targetCards.randomSelectOrNull()?.let { targetCard ->
            card.action.lClick(false)?.pointTo(targetCard)
        } ?: let {
            card.action.lClick(false)
        }
        action != null
    }),

    NO_POINT(
        "无指向",
        { card, cardEffectTypeEnum, war ->
            card.action.power() != null
        },
        { card, cardEffectTypeEnum, war ->
            card.action.lClick() != null
        }), ;

    override fun toString(): String {
        return comment
    }

}

val invalidExec: (Card, CardEffectTypeEnum, War) -> Boolean = { card, type, war ->
    false
}

fun ((Card, CardEffectTypeEnum, War) -> Boolean).isValid(): Boolean {
    return this !== invalidExec
}

fun ((Card, CardEffectTypeEnum, War) -> Boolean).isInvalid(): Boolean {
    return this === invalidExec
}