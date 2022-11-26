package club.xiaojiawei.hearthstone;

import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.utils.InitUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;
import java.time.LocalTime;

import static club.xiaojiawei.hearthstone.constant.SystemConst.PROPERTIES;

/**
 * @author 肖嘉威
 * @date 2022/11/24 13:34
 */
@SpringBootApplication
@EnableScheduling
@Slf4j
public class HearthstoneApplication {


    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(HearthstoneApplication.class);
        builder.headless(false).run(args);

        if ("true".equals(PROPERTIES.getProperty("switch"))){
            String date = PROPERTIES.getProperty("date");
            String s = String.valueOf(LocalDate.now().getDayOfWeek().getValue());
            String[] dates = date.split(",");
            for (String w : dates) {
                if (w.equals(s)){
                    String time = PROPERTIES.getProperty("time");
                    int hour = LocalTime.now().getHour();
                    String[] times = time.split(",");
                    for (String time1 : times) {
                        String[] split = time1.split("-");
                        int start = Integer.parseInt(split[0]), end = Integer.parseInt(split[1]);
                        if (hour >= start && hour < end){
                            InitUtil.openPowerLog();
                            log.info("脚本准备运行中");
                            Core.openGame();
                            return;
                        }
                    }
                    break;
                }
            }
            log.info("未到指定时间，脚本暂停中");
        }else {
            log.info("脚本暂停中");
        }

    }

}
