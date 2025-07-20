package club.xiaojiawei.dll

import club.xiaojiawei.hsscript.dll.CSystemDll
import kotlin.test.Test
import kotlin.test.assertFalse

/**
 * @author 肖嘉威
 * @date 2025/4/20 16:11
 */
class CSystemDllTest {

    @Test
    fun testIsDebug() {
        assertFalse ("dll不能以debug打包"){ CSystemDll.INSTANCE.isDebug() }
    }

}