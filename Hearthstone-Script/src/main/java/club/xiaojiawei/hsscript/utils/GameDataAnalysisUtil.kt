package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.bean.Card
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.status.War
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

    private val cardColor = Paint.valueOf("#f1f1f1")
    private val areaColor = Paint.valueOf("#80B1FB")
    private val cardEnabledColor = Paint.valueOf("#00d600")
    private val areaFont = Font("Arial", 30.0)
    private val cardFont = Font("Arial", 20.0)
    private val cardNameFont = Font("Arial", 14.0)
    private const val ITEM_RATION = 0.16666
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
        drawMyHero(war, gc)
        drawMyPlay(war, gc)
        drawMyHand(war, gc)
        drawRivalHero(war, gc)
        drawRivalPlay(war, gc)
        drawRivalHand(war, gc)
    }

    private fun drawBackground(graphicsContext: GraphicsContext) {
        graphicsContext.apply {
            val height = canvas.height
            val width = canvas.width

            fill = Color.WHITE
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
            val cards = war.rival.playArea.cards.toTypedArray()
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
            val cards = war.rival.handArea.cards.toTypedArray()
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
            val cards = war.me.playArea.cards.toTypedArray()
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
            val cards = war.me.handArea.cards.toTypedArray()
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

            fill = Color.BLACK
            font = areaFont
            fillText(text, 10.0, y + 10.0)
        }
    }

    private fun drawWhoTurn(war: War, graphicsContext: GraphicsContext) {

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
            val offset = 15
            val bottomY = y + h
            val topY = y + offset
            val rightX = x + w
            val leftX = x + 1
            fillText(card.cost.toString(), rightX - offset, topY)
            fillText(card.entityId, leftX, topY)

            if (card.cardType !== CardTypeEnum.SPELL && card.cardType === CardTypeEnum.HERO_POWER) {
                fillText(card.atc.toString(), leftX, bottomY)
                fillText(card.blood().toString(), rightX - offset, bottomY)
            }

            font = cardNameFont
            fillText(card.entityName, leftX, (bottomY + topY) / 2 - offset)
            fillText(card.cardId, leftX, (bottomY + topY) / 2)
        }
    }

}