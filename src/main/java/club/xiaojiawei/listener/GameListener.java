package club.xiaojiawei.listener;

import club.xiaojiawei.run.Core;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author 肖嘉威
 * @date 2022/12/11 9:06
 */
@Component
@Slf4j
public class GameListener {

    @Scheduled(fixedRate=30_000, initialDelay = 30_000)
    public void listenGame(){
        if (SystemUtil.getHWND(Core.getGameName()) == null){
            SystemUtil.reStart();
        }
    }

}
