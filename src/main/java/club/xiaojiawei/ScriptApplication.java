package club.xiaojiawei;

import javafx.application.Application;
import lombok.Getter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public static void main(String[] args) throws URISyntaxException {
        ScriptApplication.args = List.of(args);
        Application.launch(UIApplication.class, args);
    }

}
