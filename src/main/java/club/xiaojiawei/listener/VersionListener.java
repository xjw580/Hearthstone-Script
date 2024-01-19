package club.xiaojiawei.listener;

import club.xiaojiawei.bean.Release;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.ConfigurationEnum;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static club.xiaojiawei.data.ScriptStaticData.PROJECT_NAME;
import static club.xiaojiawei.enums.ConfigurationEnum.UPDATE_DEV;

/**
 * 脚本版本监听器，定时查看是否需要更新
 * @author 肖嘉威
 * @date 2023/9/17 21:49
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
    @Getter
    @Setter
    private static BooleanProperty canUpdate = new SimpleBooleanProperty(false);

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

    @Scheduled(initialDelay = 500,fixedDelay = 1000 * 60 * 60 * 12)
    public void checkVersion(){
//        在idea中启动时就不要检查更新了
        if (!Objects.equals(Objects.requireNonNull(this.getClass().getResource("")).getProtocol(), "jar")){
            return;
        }
        boolean updateDev = Objects.equals(scriptConfiguration.getProperty(UPDATE_DEV.getKey()), "true");
        log.info("开始从Gitee检查更新");
        log.info("更新dev：" + updateDev);
        try {
            if (updateDev){
                latestRelease = restTemplate.getForObject(String.format("https://gitee.com/api/v5/repos/zergqueen/%s/releases/latest", ScriptStaticData.PROJECT_NAME), Release.class);
            }else {
                Release[] releases = restTemplate.getForObject(String.format("https://gitee.com/api/v5/repos/zergqueen/%s/releases", ScriptStaticData.PROJECT_NAME), Release[].class);
                if (releases != null){
                    for (int i = releases.length - 1; i >= 0; i--) {
                        Release release = releases[i];
                        if (!release.isPreRelease()){
                            latestRelease = release;
                        }
                    }
                }
            }
        }catch (RuntimeException e){
            log.warn("从Gitee检查更新异常", e);
            log.info("开始从Github检查更新");
            try {
                if (updateDev){
                    Release[] releases = restTemplate.getForObject(String.format("https://api.github.com/repos/xjw580/%s/releases", ScriptStaticData.PROJECT_NAME), Release[].class);
                    if (releases != null && releases.length > 0){
                        latestRelease = releases[0];
                    }
                }else {
                    latestRelease = restTemplate.getForObject(String.format("https://api.github.com/repos/xjw580/%s/releases/latest", ScriptStaticData.PROJECT_NAME), Release.class);
                }
            }catch (RuntimeException e2){
                log.warn("从Github检查更新异常", e2);
            }
        }
        if (latestRelease != null){
            if (compareVersion(currentVersion, latestRelease.getTagName()) < 0){
                canUpdate.set(true);
                log.info("有更新可用😊，当前版本：" + currentVersion + ", 最新版本：" + latestRelease.getTagName());
                SystemUtil.notice(
                        String.format("发现新版本：%s", VersionListener.getLatestRelease().getTagName()),
                        String.format("更新日志：\n%s", VersionListener.getLatestRelease().getBody()),
                        "查看详情",
                        String.format("https://gitee.com/zergqueen/%s/releases/tag/%s", PROJECT_NAME, VersionListener.getLatestRelease().getTagName())
                );
            }else {
                canUpdate.set(false);
                log.info("已是最新，当前版本：" + currentVersion + ", 最新版本：" + latestRelease.getTagName());
            }
        }else {
            canUpdate.set(false);
            log.warn("没有任何最新版本");
        }
    }

    private static int compareVersion(String version1, String version2){
//        例：匹配v3.2.3.3-DEV中的3.2.3.3
        String regex = "\\d+(\\.\\d+)*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher1 = pattern.matcher(version1);
        Matcher matcher2 = pattern.matcher(version2);
        boolean isFind1 = matcher1.find();
        boolean isFind2 = matcher2.find();
        if (!isFind1 || !isFind2){
            log.warn(String.format("版本号有误，version1：%s，version2：%s", version1, version2));
            return Integer.MAX_VALUE;
        }
        String[] v1 = matcher1.group().split("\\.");
        String[] v2 = matcher2.group().split("\\.");
        int minLength = Math.min(v1.length, v2.length);
        for (int i = 0; i < minLength; i++) {
            String s1 = v1[i];
            String s2 = v2[i];
            int result = Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
            if (result != 0){
                return result;
            }
        }
        return Integer.compare(v1.length, v2.length);
    }

}
