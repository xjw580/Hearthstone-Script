package club.xiaojiawei.hsscript.dll;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;

/**
 * @author 肖嘉威
 * @date 2023/9/16 17:34
 */
@SuppressWarnings("all")
public interface ZLaunchDll extends Library {

    ZLaunchDll INSTANCE = Native.load("dll/zlaunch", ZLaunchDll.class);

    void ShowPage(WString bgImgPath, WString programImgPath, WString text, WString version, int width, int height);

    void HidePage();

    void SetProgress(double value);

    void SetText(WString text);

}
