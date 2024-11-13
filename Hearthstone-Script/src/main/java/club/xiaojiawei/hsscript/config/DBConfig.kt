package club.xiaojiawei.hsscript.config

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.nio.file.Path

/**
 * @author 肖嘉威
 * @date 2024/11/13 15:55
 */
object DBConfig {

    val DB: JdbcTemplate

    init {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("org.sqlite.JDBC")
        val dbPath = Path.of(System.getProperty("user.dir"), "hs_cards.db").toString()
        dataSource.url = "jdbc:sqlite:${dbPath}"
        DB = JdbcTemplate(dataSource)
    }

}