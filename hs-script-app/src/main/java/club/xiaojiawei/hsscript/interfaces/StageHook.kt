package club.xiaojiawei.hsscript.interfaces

import javafx.stage.WindowEvent

/**
 * @author 肖嘉威
 * @date 2025/1/21 20:36
 */
interface StageHook {

    fun onShown(){}
    fun onShowing(){}
    fun onHidden(){}
    fun onHiding(){}
    fun onCloseRequest(event: WindowEvent){}

}