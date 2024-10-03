package club.xiaojiawei.interfaces

/**
 * @author 肖嘉威
 * @date 2024/9/7 13:50
 */

interface ModeStrategy<T> {

    fun wantEnter()

    fun afterLeave()

    fun entering()

    fun entering(t: T?)

}
