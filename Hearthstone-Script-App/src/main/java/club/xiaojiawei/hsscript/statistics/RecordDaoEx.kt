package club.xiaojiawei.hsscript.statistics

import club.xiaojiawei.hsscript.consts.DATA_DIR
import club.xiaojiawei.hsscript.consts.STATISTICS_DB_NAME

/**
 * @author 肖嘉威
 * @date 2025/3/14 0:40
 */
object RecordDaoEx {

    val RECORD_DAO: RecordDao by lazy {
        RecordDao(DATA_DIR.resolve(STATISTICS_DB_NAME).toString())
    }

}