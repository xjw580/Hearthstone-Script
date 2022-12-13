package club.xiaojiawei.hearthstone.listener;

import club.xiaojiawei.hearthstone.constant.SystemConst;
import club.xiaojiawei.hearthstone.entity.WsResult;
import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import club.xiaojiawei.hearthstone.ws.WebSocketServer;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;

import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;

/**
 * @author 肖嘉威
 * @date 2022/12/11 14:11
 */
@Slf4j
@Component
public class PauseHotkeyListener implements HotkeyListener {
    private final static int HOT_KEY_PAUSE = 222;

    public PauseHotkeyListener(){
        if (JIntellitype.isJIntellitypeSupported()) {
            JIntellitype.getInstance().registerHotKey(HOT_KEY_PAUSE, JIntellitype.MOD_CONTROL, 'P');
            JIntellitype.getInstance().addHotKeyListener(this);
        }
    }

    //添加托盘显示：1.先判断当前平台是否支持托盘显示
    @SneakyThrows
    public void setTray() {
        if(SystemTray.isSupported()){//判断当前平台是否支持托盘功能
            //创建托盘实例
            SystemTray tray = SystemTray.getSystemTray();
            //创建托盘图标：1.显示图标Image 2.停留提示text 3.弹出菜单popupMenu 4.创建托盘图标实例
            //1.创建Image图像
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            //2.停留提示text
            String text = "IT学问网";
            //创建托盘图标
            TrayIcon trayIcon = new TrayIcon(image,text);
            trayIcon.setImageAutoSize(true);
            //将托盘图标加到托盘上
            try {
                tray.add(trayIcon);
            } catch (AWTException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 快捷键组合键按键事件
     * @param i
     */
    @Override
    public void onHotKey(int i) {
        if (i == HOT_KEY_PAUSE){
            if (Core.getPause()){
                SystemUtil.notice("当前正在暂停");
                return;
            }
            Core.setPause(true);
            log.info("暂停中，" + SystemConst.REST_TIME + "s后恢复");
            SystemUtil.notice("暂停中，" + SystemConst.REST_TIME + "s后恢复");
            for (int j = SystemConst.REST_TIME - 1; j >= 0; j--) {
                ROBOT.delay(1000);
                WebSocketServer.sendAllMessage(WsResult.ofScriptLog("暂停中，" + j + "s后恢复"));
            }
            log.info("已恢复");
            SystemUtil.notice("已恢复");
            Core.setPause(false);
        }
    }

}