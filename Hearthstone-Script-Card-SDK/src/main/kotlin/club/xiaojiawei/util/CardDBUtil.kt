package club.xiaojiawei.util

import club.xiaojiawei.bean.DBCard
import club.xiaojiawei.config.DBConfig
import org.springframework.jdbc.core.BeanPropertyRowMapper

/**
 * @author 肖嘉威
 * @date 2024/11/13 15:56
 */
object CardDBUtil {

    fun queryCardByName(name: String, limit: Int = 100, offset: Int = 0, precise: Boolean = true): MutableList<DBCard> {
        if (precise){
            return DBConfig.CARD_DB.query(
                "select * from cards where name = ? limit ? offset ?",
                BeanPropertyRowMapper(DBCard::class.java),
                name,
                limit,
                offset
            )
        }else{
            return DBConfig.CARD_DB.query(
                "select * from cards where name like ? limit ? offset ?",
                BeanPropertyRowMapper(DBCard::class.java),
                name,
                limit,
                offset
            )
        }
    }

    fun queryCardById(cardId: String, limit: Int = 100, offset: Int = 0, precise: Boolean = true): MutableList<DBCard> {
        if (precise){
            return DBConfig.CARD_DB.query(
                "select * from cards where cardId = ? limit ? offset ?",
                BeanPropertyRowMapper(DBCard::class.java),
                cardId,
                limit,
                offset
            )
        }else{
            return DBConfig.CARD_DB.query(
                "select * from cards where cardId like ? limit ? offset ?",
                BeanPropertyRowMapper(DBCard::class.java),
                cardId,
                limit,
                offset
            )
        }
    }

}