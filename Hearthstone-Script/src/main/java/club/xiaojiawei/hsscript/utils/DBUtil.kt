package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.hsscript.bean.DBCard
import club.xiaojiawei.hsscript.config.DBConfig
import org.springframework.jdbc.core.BeanPropertyRowMapper

/**
 * @author 肖嘉威
 * @date 2024/11/13 15:56
 */
object DBUtil {

    fun queryCardByName(name: String, limit: Int = 100, offset: Int = 0): MutableList<DBCard> {
        return DBConfig.DB.query(
            "select * from cards where name like ? limit ? offset ?",
            BeanPropertyRowMapper(DBCard::class.java),
            name,
            limit,
            offset
        )
    }

}