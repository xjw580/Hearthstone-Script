package club.xiaojiawei.hsscript.listener.log

import club.xiaojiawei.hsscript.bean.Deck
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.PowerLogUtil
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 套牌日志监听器
 * @author 肖嘉威
 * @date 2023/9/20 16:43
 */

object DeckLogListener : AbstractLogListener("Decks.log", 0, 1500L, TimeUnit.MILLISECONDS) {

    val DECKS = LinkedList<Deck>()

    var dealing = false

    override fun dealOldLog() {
        if (dealing) return
        dealing = true
        innerLogFile?.let { file ->
            var line: String?
            while (!PauseStatus.isPause && WorkListener.working) {
                line = file.readLine()
                if (line == null || line.isEmpty()) break
                if (line.contains("Deck Contents Received")) {
                    dealReceived()
                } else if (line.contains("Finished Editing Deck")) {
                    dealEditing()
                }
            }
        }
        dealing = false
    }

    private fun dealReceived() {
        DECKS.clear()
        var line: String
        var filePointer = innerLogFile!!.filePointer
        while ((innerLogFile!!.readLine().also { line = it }) != null) {
            if (!line.contains("#")) {
                innerLogFile!!.seek(filePointer)
                break
            }
            DECKS.addFirst(createDeck(line))
            filePointer = innerLogFile!!.filePointer
        }
    }

    private fun dealEditing() {
        val deck = createDeck(innerLogFile!!.readLine())
        var exist = false
        for (d in DECKS) {
            if (d.id == deck.id) {
                d.apply {
                    name = deck.name
                    code = deck.code
                }
                exist = true
                break
            }
        }
        if (!exist) {
            DECKS.addFirst(deck)
        }
    }

    private fun createDeck(line: String): Deck {
        var l = line
        return Deck(
            PowerLogUtil.iso88591ToUtf8(l.substring(l.indexOf("#") + 4)),
            (innerLogFile!!.readLine().also { l = it }).substring(l.indexOf("#") + 11),
            (innerLogFile!!.readLine().also { l = it }).substring(l.lastIndexOf(" ") + 1)
        )
    }

    override fun dealNewLog() {
        dealOldLog()
    }

}
