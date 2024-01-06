package club.xiaojiawei;

import club.xiaojiawei.initializer.AbstractInitializer;
import javafx.application.Application;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;

/**
 * 启动类
 * @author 肖嘉威
 * @date 2022/11/24 13:34
 */
@SpringBootApplication
@EnableScheduling
@ServletComponentScan
@Order(520)
public class ScriptApplication implements ApplicationRunner {

    @Lazy
    @Resource
    private AbstractInitializer initializer;

    public static void main(String[] args) {
        Application.launch(UIApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        initializer.init();
    }
}
