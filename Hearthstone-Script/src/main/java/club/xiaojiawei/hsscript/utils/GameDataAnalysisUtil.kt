package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.War
import club.xiaojiawei.enums.CardTypeEnum
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font

/**
 * @author 肖嘉威
 * @date 2025/1/21 14:56
 */
object GameDataAnalysisUtil {
    private const val offset = 15
    private val cardColor = Paint.valueOf("#f1f1f1")
    private val areaColor = Paint.valueOf("#80B1FB")
    private val bgColor = Paint.valueOf("#E8E8FF")
    private val cardEnabledColor = Paint.valueOf("#00d600")
    private val areaFont = Font("Arial", 30.0)
    private val cardFont = Font("Arial", 20.0)
    private val cardNameFont = Font("Arial", 14.0)
    private val cardIdFont = Font("Arial", 10.0)
    private val cardTypeFont = Font("Arial", 10.0)
    private const val ITEM_RATION = 0.166
    private const val CARD_WIDTH_RATION = 0.075
    private const val PADDING_RATION = 0.02

    private val RIVAL_HAND_AREA_VERTICAL_RATION = arrayOf(0.0, ITEM_RATION)
    private val RIVAL_HERO_AREA_VERTICAL_RATION =
        arrayOf(RIVAL_HAND_AREA_VERTICAL_RATION[1], RIVAL_HAND_AREA_VERTICAL_RATION[1] + ITEM_RATION)
    private val RIVAL_PLAY_AREA_VERTICAL_RATION =
        arrayOf(RIVAL_HERO_AREA_VERTICAL_RATION[1], RIVAL_HERO_AREA_VERTICAL_RATION[1] + ITEM_RATION)

    private val MY_PLAY_AREA_VERTICAL_RATION =
        arrayOf(RIVAL_PLAY_AREA_VERTICAL_RATION[1], RIVAL_PLAY_AREA_VERTICAL_RATION[1] + ITEM_RATION)
    private val MY_HERO_AREA_VERTICAL_RATION =
        arrayOf(MY_PLAY_AREA_VERTICAL_RATION[1], MY_PLAY_AREA_VERTICAL_RATION[1] + ITEM_RATION)
    private val MY_HAND_AREA_VERTICAL_RATION =
        arrayOf(MY_HERO_AREA_VERTICAL_RATION[1], MY_HERO_AREA_VERTICAL_RATION[1] + ITEM_RATION)

    fun init(canvas: Canvas) {
//        canvas.setOnMouseMoved { event ->
//            println("onMouseMoved")
//        }
    }

    fun draw(war: War, canvas: Canvas) {
        val gc = canvas.graphicsContext2D

        drawBackground(gc)
        drawRivalHand(war, gc)
        drawRivalHero(war, gc)
        drawRivalPlay(war, gc)

        drawMyPlay(war, gc)
        drawMyHero(war, gc)
        drawMyHand(war, gc)

        drawWhoTurn(war, gc)
    }

    private fun drawBackground(graphicsContext: GraphicsContext) {
        graphicsContext.apply {
            val height = canvas.height
            val width = canvas.width
            clearRect(0.0, 0.0, width, height)

            fill = bgColor
            fillRect(0.0, 0.0, width, height)
        }
    }

    private fun drawRivalHero(war: War, graphicsContext: GraphicsContext) {
        graphicsContext.apply {
            val height = canvas.height
            val width = canvas.width

            val w = width * CARD_WIDTH_RATION
            val y = (RIVAL_HERO_AREA_VERTICAL_RATION[0] + PADDING_RATION) * height
            val h = (RIVAL_HERO_AREA_VERTICAL_RATION[1] - RIVAL_HERO_AREA_VERTICAL_RATION[0] - PADDING_RATION) * height
            strokeArea(this, y, width, h, "对方英雄区")
            font = cardFont
            fill = Color.GRAY
            fillText("牌库剩余${war.rival.deckArea.cardSize()}张", width - 125, y)
            fillText(
                "水晶: ${war.rival.usableResource}/${war.rival.resources + war.me.tempResources}",
                width - 125,
                y + h / 2
            )
            fillText("疲劳: ${war.rival.fatigue}", width - 125, y + h)

            val cards = listOf(war.rival.playArea.weapon, war.rival.playArea.hero, war.rival.playArea.power)
            val size = cards.count { it != null }
            if (size == 0) return
            val padding = ((1.0 - CARD_WIDTH_RATION * size) / (size + 1)) * width
            var startX = padding
            cards.forEach { card ->
                card?.let {
                    val x = startX
                    fillCard(card, this, x, y, w, h, true)
                    startX += w + padding
                }
            }
        }
    }

    private fun drawRivalPlay(war: War, graphicsContext: GraphicsContext) {
        graphicsContext.apply {
            val height = canvas.height
            val width = canvas.width
            val w = width * CARD_WIDTH_RATION
            val y = (RIVAL_PLAY_AREA_VERTICAL_RATION[0] + PADDING_RATION) * height
            val h =
                (RIVAL_PLAY_AREA_VERTICAL_RATION[1] - RIVAL_PLAY_AREA_VERTICAL_RATION[0] - PADDING_RATION) * height
            strokeArea(this, y, width, h, "对方战场区")
            val cards = war.rival.playArea.cards.toList()
            if (cards.isEmpty()) return
            val padding = ((1.0 - CARD_WIDTH_RATION * cards.size) / (cards.size + 1)) * width
            var startX = padding
            cards.forEach { card ->
                val x = startX
                fillCard(card, this, x, y, w, h, true)
                startX += w + padding
            }
        }
    }

    private fun drawRivalHand(war: War, graphicsContext: GraphicsContext) {
        graphicsContext.apply {
            val height = canvas.height
            val width = canvas.width
            val w = width * CARD_WIDTH_RATION
            val y = (RIVAL_HAND_AREA_VERTICAL_RATION[0] + PADDING_RATION) * height
            val h =
                (RIVAL_HAND_AREA_VERTICAL_RATION[1] - RIVAL_HAND_AREA_VERTICAL_RATION[0] - PADDING_RATION) * height
            strokeArea(this, y, width, h, "对方手牌区")
            val cards = war.rival.handArea.cards.toList()
            if (cards.isEmpty()) return
            val padding = ((1.0 - CARD_WIDTH_RATION * cards.size) / (cards.size + 1)) * width
            var startX = padding
            cards.forEach { card ->
                val x = startX
                fillCard(card, this, x, y, w, h)
                startX += w + padding
            }
        }
    }

    private fun drawMyHero(war: War, graphicsContext: GraphicsContext) {
        graphicsContext.apply {
            val height = canvas.height
            val width = canvas.width

            val w = width * CARD_WIDTH_RATION
            val y = (MY_HERO_AREA_VERTICAL_RATION[0] + PADDING_RATION) * height
            val h = (MY_HERO_AREA_VERTICAL_RATION[1] - MY_HERO_AREA_VERTICAL_RATION[0] - PADDING_RATION) * height
            strokeArea(this, y, width, h, "我方英雄区")
            font = cardFont
            fill = Color.GRAY
            fillText("牌库剩余${war.me.deckArea.cardSize()}张", width - 125, y)
            fillText(
                "水晶: ${war.me.usableResource}/${war.me.resources + war.me.tempResources}",
                width - 125,
                y + h / 2
            )
            fillText("疲劳: ${war.me.fatigue}", width - 125, y + h)

            val cards = listOf(war.me.playArea.weapon, war.me.playArea.hero, war.me.playArea.power)
            val size = cards.count { it != null }
            if (size == 0) return
            val padding = ((1.0 - CARD_WIDTH_RATION * size) / (size + 1)) * width
            var startX = padding
            cards.forEach { card ->
                card?.let {
                    val x = startX
                    fillCard(card, this, x, y, w, h, true)
                    startX += w + padding
                }
            }
        }
    }

    private fun drawMyPlay(war: War, graphicsContext: GraphicsContext) {
        graphicsContext.apply {
            val height = canvas.height
            val width = canvas.width
            val w = width * CARD_WIDTH_RATION
            val y = (MY_PLAY_AREA_VERTICAL_RATION[0] + PADDING_RATION) * height
            val h = (MY_PLAY_AREA_VERTICAL_RATION[1] - MY_PLAY_AREA_VERTICAL_RATION[0] - PADDING_RATION) * height
            strokeArea(this, y, width, h, "我方战场区")
            val cards = war.me.playArea.cards.toList()
            if (cards.isEmpty()) return
            val padding = ((1.0 - CARD_WIDTH_RATION * cards.size) / (cards.size + 1)) * width
            var startX = padding
            cards.forEach { card ->
                val x = startX
                fillCard(card, this, x, y, w, h, true)
                startX += w + padding
            }
        }
    }

    private fun drawMyHand(war: War, graphicsContext: GraphicsContext) {
        graphicsContext.apply {
            val height = canvas.height
            val width = canvas.width
            val w = width * CARD_WIDTH_RATION
            val y = (MY_HAND_AREA_VERTICAL_RATION[0] + PADDING_RATION) * height
            val h = (MY_HAND_AREA_VERTICAL_RATION[1] - MY_HAND_AREA_VERTICAL_RATION[0] - PADDING_RATION) * height
            strokeArea(this, y, width, h, "我方手牌区")
            val cards = war.me.handArea.cards.toList()
            if (cards.isEmpty()) return
            val padding = ((1.0 - CARD_WIDTH_RATION * cards.size) / (cards.size + 1)) * width
            var startX = padding
            cards.forEach { card ->
                val x = startX
                fillCard(card, this, x, y, w, h)
                startX += w + padding
            }
        }
    }

    private fun strokeArea(graphicsContext: GraphicsContext, y: Double, w: Double, h: Double, text: String) {
        graphicsContext.apply {
            stroke = areaColor
            strokeRect(1.0, y, w - 1.0, h)

            fill = Color.GRAY
            font = areaFont
            fillText(text, 2.0, y + 10.0)
        }
    }

    private fun drawWhoTurn(war: War, graphicsContext: GraphicsContext) {
        graphicsContext.apply {
            val height = canvas.height
            val width = canvas.width
            val w = 57.0
            val x = width - w
            val h = 20.0
            val y = height * 0.5 - h / 2
            if (war.isMyTurn) {
                fill = cardEnabledColor
            } else {
                fill = Color.YELLOW
            }
            fillRect(x, y, w, h)
            stroke = Color.GRAY
            strokeRect(x, y, w, h)
            fill = Color.BLACK
            font = cardNameFont
            if (war.isMyTurn) {
                fillText("我方回合", x, y + offset)
            } else {
                fillText("对方回合", x, y + offset)
            }
        }
    }


    private fun fillCard(
        card: Card,
        graphicsContext: GraphicsContext,
        x: Double,
        y: Double,
        w: Double,
        h: Double,
        drawStatus: Boolean = false
    ) {
        graphicsContext.apply {
            fill = cardColor
            fillRect(x, y, w, h)

            if (drawStatus && (card.canAttack() || card.canPower())) {
                stroke = cardEnabledColor
            } else {
                stroke = if (card.cardType === CardTypeEnum.HERO) {
                    Color.GOLD
                } else {
                    Color.GRAY
                }
            }
            lineWidth = 2.0
            strokeRect(x, y, w, h)

            font = cardFont
            fill = Color.BLACK

            val bottomY = y + h
            val topY = y + offset
            val rightX = x + w
            val leftX = x + 1
            fill = Color.BLUE
            fillText(card.cost.toString(), leftX, topY)
            fill = Color.BLACK
            fillText(card.entityId, rightX - offset, topY)

            font = cardTypeFont
            fillText(card.cardType.name, leftX + 20, bottomY - 2)

            if (card.cardType !== CardTypeEnum.SPELL && card.cardType !== CardTypeEnum.HERO_POWER) {
                font = cardFont
                fill = Color.ORANGE
                fillText(card.atc.toString(), leftX, bottomY)
                fill = Color.RED
                fillText(card.blood().toString(), rightX - offset, bottomY)
            }
            fill = Color.BLACK
            font = cardNameFont
            fillText(card.entityName, leftX, (bottomY + topY) / 2 - offset)
            font = cardIdFont
            fillText(card.cardId, leftX, (bottomY + topY) / 2)
        }
    }

}