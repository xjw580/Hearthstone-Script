package club.xiaojiawei.hsscript.service

/**
 * @author 肖嘉威
 * @date 2025/4/3 15:13
 */
abstract class BoolService : Service<Boolean>() {

    override fun execIntelligentStartStop(value: Boolean): Boolean {
        return value
    }

}