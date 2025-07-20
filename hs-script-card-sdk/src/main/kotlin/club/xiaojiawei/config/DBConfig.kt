package club.xiaojiawei.config

import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscriptbase.util.isFalse
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.nio.file.Path
import kotlin.io.path.exists

/**
 * @author 肖嘉威
 * @date 2024/11/13 15:55
 */
object DBConfig {

    val CARD_DB: JdbcTemplate

    const val DB_NAME = "hs_cards.db"

    init {
        val dataSource = DriverManagerDataSource().apply {
            setDriverClassName("org.sqlite.JDBC")
            var dbPath = Path.of(System.getProperty("user.dir"), DB_NAME)
            dbPath.exists().isFalse {
                dbPath = Path.of(System.getProperty("user.dir")).parent.resolve(DB_NAME)
            }
            dbPath.exists().isFalse {
                dbPath = Path.of(System.getProperty("user.dir"), DB_NAME)
                log.warn { "不存在默认的卡牌数据库" }
            }
            url = "jdbc:sqlite:${dbPath}"
        }
        CARD_DB = JdbcTemplate(dataSource)
    }

}