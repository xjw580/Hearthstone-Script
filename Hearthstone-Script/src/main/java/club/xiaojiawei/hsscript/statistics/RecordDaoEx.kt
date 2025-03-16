package club.xiaojiawei.hsscript.statistics

import club.xiaojiawei.hsscript.data.STATISTICS_DB_NAME
import club.xiaojiawei.hsscript.data.STATISTICS_DIR
import club.xiaojiawei.util.isFalse
import java.nio.file.Files
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * @author 肖嘉威
 * @date 2025/3/14 0:40
 */
object RecordDaoEx {

    private val format = DateTimeFormatter.ofPattern("yyyyMM")

    private var prevDateTime: LocalDate? = null

    private var prevRecordDao: RecordDao? = null

    @Synchronized
    fun getRecordDao(dateTime: LocalDate): RecordDao {
        if (prevDateTime == dateTime) {
            return prevRecordDao!!
        }
        prevDateTime = dateTime
        Files.exists(STATISTICS_DIR).isFalse {
            STATISTICS_DIR.toFile().mkdirs()
        }
        prevRecordDao = RecordDao(STATISTICS_DIR.resolve(getDBName(dateTime)).toString())
        return prevRecordDao!!
    }

    private fun getDBName(dateTime: LocalDate): String {
        return String.format(STATISTICS_DB_NAME, format.format(dateTime))
    }

}