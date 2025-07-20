package club.xiaojiawei.hsscriptbasecard

import club.xiaojiawei.CardPlugin
import club.xiaojiawei.config.PluginScope
import club.xiaojiawei.hsscriptbase.util.SystemUtil
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox

/**
 * @author 肖嘉威
 * @date 2024/9/8 14:57
 */
class HsBaseCardPlugin : CardPlugin {
    override fun version(): String = VersionInfo.VERSION

    override fun author(): String = "XiaoJiawei"

    override fun graphicDescription(): Pane? {
        val deckCode = "AAECAQcC4+YG5eYGDp6fBJ+fBLSfBIagBIigBImgBI7UBJDUBJzUBJ/UBKPUBLT4BbX4Bd3zBgAA"
        val vBox =
            VBox().apply {
                spacing = 5.0
                children.addAll(
                    Label("包含以下卡组中卡牌的适配：(点击卡组代码复制)"),
                    Label("沙包战：").apply {
                        contentDisplay = ContentDisplay.RIGHT
                        graphic =
                            Label(deckCode).apply {
                                cursor = Cursor.HAND
                            }
                        onMouseClicked =
                            EventHandler {
                                SystemUtil.pasteTextToClipboard(deckCode)
                            }
                    },
                )
            }
        return vBox
    }

    override fun description(): String =
        """
        捆绑。包含对基础英雄技能的适配
        """.trimIndent()

    override fun id(): String = "xjw-base-plugin"

    override fun name(): String = "基础"

    override fun homeUrl(): String = "https://github.com/xjw580/Hearthstone-Script"

    override fun sdkVersion(): String = VersionInfo.SDK_VERSION

    override fun pluginScope(): Array<String> {
        return PluginScope.PUBLIC
    }
}