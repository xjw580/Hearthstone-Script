package club.xiaojiawei.initializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import static club.xiaojiawei.data.ScriptStaticData.TEMP_DIR;
import static club.xiaojiawei.data.ScriptStaticData.TEMP_PATH;

/**
 * 删除下载的新版本等临时文件
 * @author 肖嘉威
 * @date 2023/9/17 1:59
 */
@Component
@Slf4j
public class DelTempInitializer extends AbstractInitializer{
    @Override
    protected void exec() {
        if (new File(TEMP_PATH).exists()){
            try {
                Runtime.getRuntime().exec("cmd /c start rd /s /Q " + TEMP_DIR);
                log.info("临时文件删除成功");
            } catch (IOException e) {
                throw new RuntimeException("删除临时文件发生错误", e);
            }
        }else {
            log.info("没有临时文件，无需删除");
        }
    }
}
