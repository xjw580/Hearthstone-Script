package club.xiaojiawei.util

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

/**
 * @author 肖嘉威
 * @date 2025/3/20 14:28
 */
class CardDBUtilTest {

    @Test
    fun testQueryCardByName() {
        val res = CardDBUtil.queryCardByName("考达拉幼龙")
        assertTrue(res.isNotEmpty(), "查不到指定卡牌")
    }

}