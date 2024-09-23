package club.xiaojiawei.starter;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.dll.SystemDll;
import club.xiaojiawei.interfaces.closer.StarterTaskCloser;
import club.xiaojiawei.utils.CMDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * 启动游戏
 * @author 肖嘉威
 * @date 2023/7/5 14:38
 */
@Slf4j
@Component
public class InjectStarter extends AbstractStarter implements StarterTaskCloser {

    @Override
    public void exec() {
        String rootPath = System.getProperty("user.dir");
        String injectUtilName = "injectUtil.exe";
        String dllName = "libHS.dll";
        String dllDir = "dll";

        File injectFile = Path.of(rootPath, injectUtilName).toFile();
        File dllFile;

        if (injectFile.exists()) {
            dllFile = Path.of(rootPath, ScriptStaticData.LIB_DIR, dllDir, dllName).toFile();
            inject(injectFile.getAbsolutePath(), dllFile.getAbsolutePath());
        }else {
            injectFile = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("exe/" + injectUtilName)).getPath());
            if (injectFile.exists()) {
                dllFile = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource(dllDir + "/" + dllName)).getPath());
                if (dllFile.exists()) {
                    inject(injectFile.getAbsolutePath(), dllFile.getAbsolutePath());
                }else {
                    log.error("未找到" + dllName);
                }
            }else {
                log.error("未找到" + injectUtilName);
            }
        }
        SystemDll.INSTANCE.changeWindow(ScriptStaticData.getGameHWND(), true);
        SystemDll.INSTANCE.changeInput(ScriptStaticData.getGameHWND(), true);
        startNextStarter();
    }

    private void inject(String injectUtilPath, String dllPath) {
        try {
            String result = CMDUtil.exec(new String[]{injectUtilPath, ScriptStaticData.GAME_US_NAME + ".exe", dllPath});
            log.info("注入dll" + (result.contains("completed")? "成功" : "失败"));
        } catch (IOException e) {
            log.error("注入dll异常", e);
        }
    }

    @Override
    public void closeStarterTask() {
    }

    @Override
    public int getOrder() {
        return 40;
    }
}
