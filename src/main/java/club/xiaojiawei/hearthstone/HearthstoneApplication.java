package club.xiaojiawei.hearthstone;

import club.xiaojiawei.hearthstone.constant.SystemConst;
import club.xiaojiawei.hearthstone.entity.WsResult;
import club.xiaojiawei.hearthstone.listener.ExitHotkeyListener;
import club.xiaojiawei.hearthstone.utils.InitUtil;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import club.xiaojiawei.hearthstone.ws.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;

import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;

/**
 * @author 肖嘉威
 * @date 2022/11/24 13:34
 */
@SpringBootApplication
@EnableScheduling
@Slf4j
public class HearthstoneApplication {

    public static void main(String[] args) {
        InitUtil.init();
        SpringApplicationBuilder builder = new SpringApplicationBuilder(HearthstoneApplication.class);
        builder.headless(false).run(args);
        SystemUtil.openBrowser();
    }

}
