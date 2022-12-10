package club.xiaojiawei.hearthstone;

import club.xiaojiawei.hearthstone.entity.WsResult;
import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.utils.InitUtil;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import club.xiaojiawei.hearthstone.ws.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
        InitUtil.init();
        SpringApplicationBuilder builder = new SpringApplicationBuilder(HearthstoneApplication.class);
        builder.headless(false).run(args);
        if(java.awt.Desktop.isDesktopSupported()){
            try{
                //创建一个URI实例,注意不是URL
                java.net.URI uri=java.net.URI.create("http://localhost:8888");
                //获取当前系统桌面扩展
                java.awt.Desktop dp=java.awt.Desktop.getDesktop();
                //判断系统桌面是否支持要执行的功能
                if(dp.isSupported(java.awt.Desktop.Action.BROWSE)){
                    //获取系统默认浏览器打开链接
                    dp.browse(uri);
                }
            }catch(java.lang.NullPointerException e){
                log.error("uri为空", e);
            }catch(java.io.IOException e){
                //此为无法获取系统默认浏览器
                log.error("无法获取系统默认浏览器", e);
            }
        }
    }

}
