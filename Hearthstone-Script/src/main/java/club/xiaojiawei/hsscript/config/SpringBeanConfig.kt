package club.xiaojiawei.hsscript.config

import club.xiaojiawei.hsscript.consts.SpringData
import jakarta.annotation.Resource
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

/**
 * @author 肖嘉威
 * @date 2024/9/28 20:50
 */

@Component
class SpringBeanConfig : ApplicationRunner {

    @Resource
    private val context: ApplicationContext? = null

    override fun run(args: ApplicationArguments?) {
        context?.let {
            springData = it.getBean(SpringData::class.java)
        }
    }

    companion object {
        lateinit var springData: SpringData
    }

}