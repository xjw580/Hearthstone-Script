package club.xiaojiawei.hearthstone.listener;

import club.xiaojiawei.hearthstone.utils.SystemUtil;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.*;

/**
 * @author 肖嘉威
 * @date 2022/12/11 11:23
 */
@Slf4j
@Component
public class ExitHotkeyListener implements HotkeyListener {

    @Resource
    private PauseHotkeyListener pauseHotkeyListener;

    private final static int HOT_KEY_EXIT = 111;

    public ExitHotkeyListener(){
        if (JIntellitype.isJIntellitypeSupported()) {
            JIntellitype.getInstance().registerHotKey(HOT_KEY_EXIT, JIntellitype.MOD_ALT, 'P');
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
        //如果是我指定的快捷键就执行指定的操作
        if(i == HOT_KEY_EXIT){
            log.info("捕捉到热键，关闭系统");
            SystemUtil.notice("捕捉到热键，关闭系统");
            JIntellitype.getInstance().removeHotKeyListener(this);
            JIntellitype.getInstance().removeHotKeyListener(pauseHotkeyListener);
            System.exit(0);
        }
    }

}