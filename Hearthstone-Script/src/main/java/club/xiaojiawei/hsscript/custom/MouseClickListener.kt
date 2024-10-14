package club.xiaojiawei.hsscript.custom

import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.util.function.Consumer

/**
 * @author 肖嘉威
 * @date 2023/10/1 10:12
 */
class MouseClickListener(private val consumer: Consumer<MouseEvent?>) : MouseListener {

    override fun mouseClicked(e: MouseEvent?) {
        consumer.accept(e)
    }

    override fun mousePressed(e: MouseEvent?) {
    }

    override fun mouseReleased(e: MouseEvent?) {
    }

    override fun mouseEntered(e: MouseEvent?) {
    }

    override fun mouseExited(e: MouseEvent?) {
    }
}
