package club.xiaojiawei.util

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author 肖嘉威
 * @date 2025/6/10 19:59
 */
class CardUtilTest {

    @Test
    fun testGetDamageValue(){
        assertEquals(CardUtil.getDamageValue("造成100点伤害"), 100)
        assertEquals(CardUtil.getDamageValue("造成10点伤害"), 10)
        assertEquals(CardUtil.getDamageValue("造成1点伤害"), 1)
        assertEquals(CardUtil.getDamageValue("造成点伤害"), null)
    }
}