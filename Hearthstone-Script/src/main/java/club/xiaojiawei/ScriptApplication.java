package club.xiaojiawei;

import javafx.application.Application;
import lombok.Getter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

/**
 * 启动类
 * @author 肖嘉威
 * @date 2022/11/24 13:34
 */
@SpringBootApplication
@EnableScheduling
@ServletComponentScan
public class ScriptApplication{

    @Getter
    private static List<String> args;

    public static void main(String[] args) throws Exception {
//        Test test = new Test();
//        test.test();
        ScriptApplication.args = List.of(args);
        Application.launch(UIApplication.class, args);
    }

}
