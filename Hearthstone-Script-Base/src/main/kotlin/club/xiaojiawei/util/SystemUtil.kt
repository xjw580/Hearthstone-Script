package club.xiaojiawei.util

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

/**
 * @author 肖嘉威
 * @date 2025/1/22 17:34
 */
object SystemUtil {

    /**
     * 将文本复制到剪切板
     */
    fun pasteTextToClipboard(text: String) {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
    }

}