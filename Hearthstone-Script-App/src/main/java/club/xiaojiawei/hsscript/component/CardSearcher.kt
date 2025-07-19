package club.xiaojiawei.hsscript.component

import club.xiaojiawei.util.withNotNull
import javafx.scene.layout.VBox

/**
 * @author 肖嘉威
 * @date 2025/2/27 10:00
 */
class CardSearcher : VBox() {

    var cardField: CardField? = null
        set(value) {
            field = value
            if (this.children.size >= 2) {
                this.children[1] = field
            } else {
                this.children.addLast(field)
            }
            joint()
        }

    var cardTableView: CardTableView? = null
        set(value) {
            field = value
            if (this.children.size >= 2) {
                this.children[0] = field
            } else {
                this.children.addFirst(field)
            }
            joint()
        }


    private fun joint() {
        withNotNull(cardField, cardTableView) { cardField, cardTableView ->
            cardField.searchHandler = { name, limit, offset ->
                if (name.isNullOrBlank()) {
                    cardTableView.items.clear()
                } else {
                    cardTableView.setCardByName(name, limit, offset)
                }
            }
        }
    }

}