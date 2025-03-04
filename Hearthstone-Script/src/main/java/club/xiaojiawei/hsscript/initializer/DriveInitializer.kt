package club.xiaojiawei.hsscript.initializer

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.data.INSTALL_DRIVE_FILE
import club.xiaojiawei.hsscript.data.MOUSE_DRIVE_PATH
import club.xiaojiawei.hsscript.dll.SystemDll
import club.xiaojiawei.hsscript.enums.MouseControlModeEnum
import club.xiaojiawei.hsscript.utils.ConfigExUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.util.isFalse
import java.io.File

/**
 * @author 肖嘉威
 * @date 2023/7/4 11:33
 */
class DriveInitializer : AbstractInitializer() {

    @Suppress("DEPRECATION")
    override fun exec() {
        val mouseControlMode = ConfigExUtil.getMouseControlMode()
        if (mouseControlMode === MouseControlModeEnum.DRIVE){
            File(MOUSE_DRIVE_PATH).exists().isFalse {
                if (SystemDll.INSTANCE.IsRunAsAdministrator()) {
                    SystemUtil.getExeFilePath(INSTALL_DRIVE_FILE)?.let {
                        Runtime.getRuntime().exec("$it /install").waitFor()
                        Thread.sleep(1000)
                        SystemUtil.messageOk("需要重启系统")
                    } ?: let {
                        val text = "找不到${INSTALL_DRIVE_FILE}"
                        log.info { text }
                        SystemUtil.messageError(text)
                    }
                } else {
                    SystemUtil.messageError("请以管理员权限运行本软件")
                }
            }
        }
    }

}
