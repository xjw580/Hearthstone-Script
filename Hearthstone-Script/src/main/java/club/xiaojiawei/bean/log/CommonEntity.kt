package club.xiaojiawei.bean.log

import club.xiaojiawei.bean.Entity
import club.xiaojiawei.enums.ZoneEnum
import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString

/**
 * @author 肖嘉威
 * @date 2022/11/30 12:38
 */

open class CommonEntity : Entity() {

    var entity: String = ""

    var zone: ZoneEnum? = null

    var zonePos = 0

    var playerId: String = ""

    var logType: String = ""

}
