package club.xiaojiawei.hsscript.bean

import com.melloware.jintellitype.JIntellitypeConstants

/**
 * @author 肖嘉威
 * @date 2024/10/11 15:26
 */
class HotKey {

    constructor()

    constructor(modifier: Int, keyCode: Int) {
        this.modifier = modifier
        this.keyCode = keyCode
    }

    var modifier: Int = 0
    var keyCode: Int = 0

    override fun toString(): String {
        if (keyCode == 0) return ""
        val modifierStr =
            (if (modifier and JIntellitypeConstants.MOD_ALT == JIntellitypeConstants.MOD_ALT) "Alt+" else "") +
                    (if (modifier and JIntellitypeConstants.MOD_CONTROL == JIntellitypeConstants.MOD_CONTROL) "Ctrl+" else "") +
                    (if (modifier and JIntellitypeConstants.MOD_SHIFT == JIntellitypeConstants.MOD_SHIFT) "Shift+" else "") +
                    (if (modifier and JIntellitypeConstants.MOD_WIN == JIntellitypeConstants.MOD_WIN) "Win+" else "")
        return modifierStr + keyCode.toChar()
    }
}