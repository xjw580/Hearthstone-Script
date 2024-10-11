package club.xiaojiawei.hsscript.bean

/**
 * @author 肖嘉威
 * @date 2024/10/11 16:34
 */
class WorkDay {

    constructor()

    constructor(day: String, enabled: Boolean){
        this.day = day
        this.enabled = enabled
    }

    var day: String = ""
    var enabled: Boolean = false

}