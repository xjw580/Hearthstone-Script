package club.xiaojiawei.initializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import static club.xiaojiawei.data.ScriptStaticData.TEMP_DIR;

/**
 * @author 肖嘉威
 * @date 2023/9/17 1:59
 * @msg
 */
@Component
@Slf4j
public class DelTempInitializer extends AbstractInitializer{
    @Override
    protected void exec() {
        if (new File(TEMP_DIR).exists()){
            try {
                Runtime.getRuntime().exec("cmd /c rd /s /Q " + TEMP_DIR);
                log.info("临时文件删除成功");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (nextInitializer != null){
            nextInitializer.init();
        }
    }
}
