package club.xiaojiawei;

import club.xiaojiawei.utils.InitUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author 肖嘉威
 * @date 2022/11/24 13:34
 */
@SpringBootApplication
@EnableScheduling
@Slf4j
public class SpringApplication{
    public static void main(String[] args) {
        InitUtil.init();
        SpringApplicationBuilder builder = new SpringApplicationBuilder(SpringApplication.class);
        builder.headless(false).run(args);
        JavaFXApplication.main(args);
    }
}
