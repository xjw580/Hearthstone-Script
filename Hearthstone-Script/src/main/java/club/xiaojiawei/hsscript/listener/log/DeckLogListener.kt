package club.xiaojiawei.hsscript.listener.log

import club.xiaojiawei.hsscript.bean.Deck
import club.xiaojiawei.hsscript.config.SpringBeanConfig
import club.xiaojiawei.hsscript.utils.PowerLogUtil
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 套牌日志监听器
 * @author 肖嘉威
 * @date 2023/9/20 16:43
 */

object DeckLogListener : AbstractLogListener(SpringBeanConfig.springData.deckLogName, 0, 1500L, TimeUnit.MILLISECONDS){

    val DECKS = LinkedList<Deck>()

    override fun dealOldLog() {
        var line: String
        while ((innerLogFile!!.readLine().also { line = it }) != null) {
            if (line.contains("Deck Contents Received")) {
                dealReceived()
            } else if (line.contains("Finished Editing Deck")) {
                dealEditing()
            }
        }
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
                d.name = deck.name
                d.code = deck.code
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
