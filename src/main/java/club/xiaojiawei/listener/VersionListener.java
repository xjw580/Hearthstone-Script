package club.xiaojiawei.listener;

import club.xiaojiawei.controller.JavaFXDashboardController;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.bean.Release;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Objects;
import java.util.Properties;

import static club.xiaojiawei.enums.ConfigurationEnum.UPDATE_DEV;

/**
 * @author 肖嘉威
 * @date 2023/9/17 21:49
 * @msg
 */
@Component
@Slf4j
public class VersionListener {
    @Getter
    private static Release latestRelease;
    @Getter
    private static String currentVersion;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private SpringData springData;
    @Resource
    private Properties scriptConfiguration;
    @PostConstruct
    void init(){
          /*
            用idea启动时springData.getVersion()能读到正确的值
            打完包后启动this.getClass().getPackage().getImplementationVersion()能读到正确的值
        */
        if ((currentVersion = VersionListener.class.getPackage().getImplementationVersion()) == null){
            currentVersion = springData.getVersion();
        }
    }
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    void checkVersion(){
//        在idea中启动时就不要检查更新了
        if (!Objects.equals(Objects.requireNonNull(this.getClass().getResource("")).getProtocol(), "jar")){
            return;
        }
        log.info("开始检查是否有更新");
        try {
            latestRelease = restTemplate.getForObject("https://gitee.com/api/v5/repos/zergqueen/Hearthstone-Script/releases/latest", Release.class);
        }catch (RuntimeException e){
            try {
//                todo 获取不到Github的预览版
                latestRelease = restTemplate.getForObject("https://api.github.com/repos/xjw580/Hearthstone-Script/releases/latest", Release.class);
            }catch (RuntimeException e2){
                log.warn("获取最新版本信息失败", e2);
            }
        }
        if (latestRelease != null){
            if (currentVersion.compareTo(latestRelease.getTagName()) < 0 && (!latestRelease.isPreRelease() || Objects.equals(scriptConfiguration.getProperty(UPDATE_DEV.getKey()), "true"))){
                JavaFXDashboardController.updateBack.setVisible(true);
                log.info("有更新可用😊，当前版本：" + currentVersion + ", 最新版本：" + latestRelease.getTagName());
            }else {
                log.info("已是最新，当前版本：" + currentVersion);
            }
        }else {
            log.warn("没有任何最新版本");
        }
    }

}
