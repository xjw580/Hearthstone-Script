package club.xiaojiawei.hsscript.bean

import java.lang.reflect.Modifier

/**
 * @author 肖嘉威
 * @date 2024/10/11 15:26
 */
class HotKey {

    constructor()

    constructor(modifier: Int, keyCode: Int){
        this.modifier = modifier
        this.keyCode = keyCode
    }

    var modifier: Int = 0
    var keyCode: Int = 0
}