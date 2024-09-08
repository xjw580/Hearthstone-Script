package club.xiaojiawei

/**
 * @author 肖嘉威
 * @date 2024/9/8 16:37
 */
interface Plugin {

    fun description(): String

    fun author(): String

    fun version(): String

    fun id(): String

    fun name(): String

}