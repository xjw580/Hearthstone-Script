package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.bean.GameRect;
import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 游戏界面
 *
 * @author 肖嘉威
 * @date 2022/11/25 12:43
 */
@Slf4j
@Component
public class GameplayModeStrategy extends AbstractModeStrategy<Object> {

    public static final GameRect CONFIRM_RECT = new GameRect(-0.0546D, 0.0601D, 0.2709D, 0.3222D);

    public static final GameRect[] FOUR_DISCOVER_RECTS = new GameRect[]{
            new GameRect(-0.3332D, -0.1911D, -0.1702D, 0.1160D),
            new GameRect(-0.1570D, -0.0149D, -0.1702D, 0.1160D),
            new GameRect(0.0182D, 0.1603D, -0.1702D, 0.1160D),
            new GameRect(0.1934D, 0.3355D, -0.1702D, 0.1160D),

    };

    public static final GameRect[] THREE_DISCOVER_RECTS = new GameRect[]{
            new GameRect(-0.3037D, -0.1595D, -0.1702D, 0.1160D),
            new GameRect(-0.0666D, 0.0741D, -0.1702D, 0.1160D),
            new GameRect(0.1656D, 0.3106D, -0.1702D, 0.1160D),
    };

    public static final GameRect[][] HAND_DECK_RECTS = new GameRect[][]{
            new GameRect[]{
                    new GameRect(-0.0693D, 0.0136D, 0.3675D, 0.5000D),
            },
            new GameRect[]{
                    new GameRect(-0.1149D, -0.0316D, 0.3675D, 0.5000D),
                    new GameRect(-0.0242D, 0.0590D, 0.3675D, 0.5000D),
            },
            new GameRect[]{
                    new GameRect(-0.1599D, -0.0767D, 0.3675D, 0.5000D),
                    new GameRect(-0.0693D, 0.0140D, 0.3675D, 0.5000D),
                    new GameRect(0.0214D, 0.1047D, 0.3675D, 0.5000D),
            },
            new GameRect[]{
                    new GameRect(-0.1930D, -0.1307D, 0.3855D, 0.5000D),
                    new GameRect(-0.1092D, -0.0347D, 0.3742D, 0.5000D),
                    new GameRect(-0.0208D, 0.0507D, 0.3814D, 0.4995D),
                    new GameRect(0.0744D, 0.1425D, 0.4158D, 0.5000D),
            },
            new GameRect[]{
                    new GameRect(-0.2034D, -0.1471D, 0.4116D, 0.5000D),
                    new GameRect(-0.1338D, -0.0704D, 0.3888D, 0.5000D),
                    new GameRect(-0.0704D, -0.0071D, 0.3698D, 0.5000D),
                    new GameRect(0.0077D, 0.0604D, 0.3935D, 0.5000D),
                    new GameRect(0.0858D, 0.1456D, 0.4144D, 0.5000D),
            },
            new GameRect[]{
                    new GameRect(-0.2115D, -0.1672D, 0.4144D, 0.5000D),
                    new GameRect(-0.1514D, -0.1028D, 0.3964D, 0.5000D),
                    new GameRect(-0.0975D, -0.0448D, 0.3755D, 0.5000D),
                    new GameRect(-0.0384D, 0.0087D, 0.3755D, 0.5000D),
                    new GameRect(0.0270D, 0.0671D, 0.3812D, 0.4990D),
                    new GameRect(0.0903D, 0.1579D, 0.4240D, 0.5000D),
            },
            new GameRect[]{
                    new GameRect(-0.2179D, -0.1799D, 0.4192D, 0.5000D),
                    new GameRect(-0.1640D, -0.1232D, 0.4040D, 0.5000D),
                    new GameRect(-0.1155D, -0.0690D, 0.3869D, 0.5000D),
                    new GameRect(-0.0712D, -0.0233D, 0.3717D, 0.5000D),
                    new GameRect(-0.0152D, 0.0235D, 0.3755D, 0.5000D),
                    new GameRect(0.0418D, 0.0727D, 0.3821D, 0.5000D),
                    new GameRect(0.0956D, 0.1617D, 0.4211D, 0.5000D),
            },
            new GameRect[]{
                    new GameRect(-0.2210D, -0.1901D, 0.4259D, 0.5000D),
                    new GameRect(-0.1746D, -0.1394D, 0.4125D, 0.5000D),
                    new GameRect(-0.1324D, -0.0916D, 0.3973D, 0.5000D),
                    new GameRect(-0.0912D, -0.0490D, 0.3745D, 0.5000D),
                    new GameRect(-0.0469D, -0.0103D, 0.3688D, 0.5000D),
                    new GameRect(0.0038D, 0.0326D, 0.3745D, 0.5000D),
                    new GameRect(0.0534D, 0.0759D, 0.4040D, 0.5000D),
                    new GameRect(0.1030D, 0.1536D, 0.4163D, 0.4990D),
            },
            new GameRect[]{
                    new GameRect(-0.2274D, -0.1964D, 0.4335D, 0.5000D),
                    new GameRect(-0.1820D, -0.1496D, 0.4335D, 0.5000D),
                    new GameRect(-0.1429D, -0.1099D, 0.4059D, 0.5000D),
                    new GameRect(-0.1060D, -0.0687D, 0.3888D, 0.5000D),
                    new GameRect(-0.0712D, -0.0346D, 0.3698D, 0.5000D),
                    new GameRect(-0.0268D, 0.0034D, 0.3745D, 0.5000D),
                    new GameRect(0.0186D, 0.0502D, 0.3764D, 0.4563D),
                    new GameRect(0.0639D, 0.0942D, 0.3878D, 0.4610D),
                    new GameRect(0.1083D, 0.1653D, 0.4125D, 0.5000D),
            },
            new GameRect[]{
                    new GameRect(-0.2305D, -0.2024D, 0.4401D, 0.5000D),
                    new GameRect(-0.1894D, -0.1598D, 0.4401D, 0.5000D),
                    new GameRect(-0.1524D, -0.1250D, 0.4097D, 0.5000D),
                    new GameRect(-0.1176D, -0.0859D, 0.3964D, 0.5000D),
                    new GameRect(-0.0859D, -0.0522D, 0.3726D, 0.5000D),
                    new GameRect(-0.0511D, -0.0208D, 0.3726D, 0.5000D),
                    new GameRect(-0.0089D, 0.0207D, 0.3740D, 0.4501D),
                    new GameRect(0.0302D, 0.0583D, 0.3783D, 0.4515D),
                    new GameRect(0.0692D, 0.0974D, 0.3926D, 0.4610D),
                    new GameRect(0.1093D, 0.1677D, 0.4163D, 0.5000D),
            },
    };

    public static final GameRect[][] PLAY_DECK_RECTS = new GameRect[][]{
//            偶数
            new GameRect[]{
                    new GameRect(-0.2689D, -0.2111D, -0.0033D, 0.1050D),
                    new GameRect(-0.1731D, -0.1153D, -0.0033D, 0.1050D),
                    new GameRect(-0.0773D, -0.0195D, -0.0033D, 0.1050D),
                    new GameRect(0.0195D, 0.0773D, -0.0033D, 0.1050D),
                    new GameRect(0.1153D, 0.1731D, -0.0033D, 0.1050D),
                    new GameRect(0.2111D, 0.2689D, -0.0033D, 0.1050D),
            },
//            奇数
            new GameRect[]{
                    new GameRect(-0.3156D, -0.2578D, -0.0041D, 0.1043D),
                    new GameRect(-0.2204D, -0.1626D, -0.0041D, 0.1043D),
                    new GameRect(-0.1257D, -0.0691D, -0.0041D, 0.1043D),
                    new GameRect(-0.0299D, 0.0267D, -0.0041D, 0.1043D),
                    new GameRect(0.0691D, 0.1257D, -0.0041D, 0.1043D),
                    new GameRect(0.1626D, 0.2204D, -0.0041D, 0.1043D),
                    new GameRect(0.2578D, 0.3156D, -0.0041D, 0.1043D),
            },
    };


    @Override
    public void wantEnter() {
    }

    @Override
    protected void afterEnter(Object o) {
        if (Mode.getPrevMode() == ModeEnum.LOGIN || Mode.getPrevMode() == null) {
            log.info("当前对局不完整，准备投降");
            gameUtil.surrender();
        }
    }

}
