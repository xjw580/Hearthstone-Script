package club.xiaojiawei.util

import club.xiaojiawei.enums.ZoneEnum
import club.xiaojiawei.hsscript.bean.log.CommonEntity
import club.xiaojiawei.hsscript.utils.PowerLogUtil
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author 肖嘉威
 * @date 2025/4/3 14:39
 */
class PowerLogUtilTest {

    @Test
    fun testParseCommonEntity() {
        val testData = mapOf(
            "D 18:37:20.8251425 PrintPower() -     FULL_ENTITY - Updating [entityName=火球术 id=80 zone=SETASIDE zonePos=0 cardId=CORE_CS2_029 player=1] CardID=CORE_CS2_029" to CommonEntity().apply {
                entityName = String("火球术".toByteArray(Charsets.ISO_8859_1), Charsets.ISO_8859_1)
                entityId = "80"
                zone = ZoneEnum.SETASIDE
                zonePos = 0
                cardId = "CORE_CS2_029"
                playerId = "1"
            },
            "D 18:37:20.8089830 PrintPower() - BLOCK_START BlockType=PLAY Entity=[entityName=吉安娜的礼物 id=13 zone=HAND zonePos=4 cardId=GIFT_02 player=1] EffectCardId=System.Collections.Generic.List`1[System.String] EffectIndex=0 Target=0 SubOption=-1 " to CommonEntity().apply {
                entityName = String("吉安娜的礼物".toByteArray(Charsets.ISO_8859_1), Charsets.ISO_8859_1)
                entityId = "13"
                zone = ZoneEnum.HAND
                zonePos = 4
                cardId = "GIFT_02"
                playerId = "1"
            },
            "BLOCK_START BlockType=TRIGGER Entity=异灵术#1234 EffectCardId=System.Collections.Generic.List`1[System.String] EffectIndex=-1 Target=0 SubOption=-1 TriggerKeyword=TAG_NOT_SET" to CommonEntity().apply {
                entity = "异灵术#1234"
            },
            "D 18:37:29.3413958 PrintPower() -     TAG_CHANGE Entity=[entityName=寒冰箭 id=78 zone=SETASIDE zonePos=0 cardId=CS2_024 player=1] tag=LAST_AFFECTED_BY value=13 " to CommonEntity().apply {
                entityName = String("寒冰箭".toByteArray(Charsets.ISO_8859_1), Charsets.ISO_8859_1)
                entityId = "78"
                zone = ZoneEnum.SETASIDE
                zonePos = 0
                cardId = "CS2_024"
                playerId = "1"
            },
        )
        for (testDatum in testData) {
            val commonEntity = CommonEntity()
            PowerLogUtil.parseCommonEntity(commonEntity, testDatum.key)
            val rightCommonEntity = testDatum.value
            assertEquals(commonEntity.entity, rightCommonEntity.entity)
            assertEquals(commonEntity.zone, rightCommonEntity.zone)
            assertEquals(commonEntity.zonePos, rightCommonEntity.zonePos)
            assertEquals(commonEntity.playerId, rightCommonEntity.playerId)
            assertEquals(commonEntity.logType, rightCommonEntity.logType)
            assertEquals(commonEntity.entityId, rightCommonEntity.entityId)
            assertEquals(commonEntity.entityName, rightCommonEntity.entityName)
            assertEquals(commonEntity.cardId, rightCommonEntity.cardId)
        }
    }

}