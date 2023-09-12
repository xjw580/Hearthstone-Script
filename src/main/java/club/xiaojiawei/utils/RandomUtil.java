package club.xiaojiawei.utils;

import java.util.Random;

/**
 * @author 肖嘉威
 * @date 2022/11/24 19:41
 * @msg 随机数生成工具
 */
public class RandomUtil {

    private static final Random random = new Random();

    public static int getRandom(int min, int max){
        return (int) (random.nextDouble() * (max - min + 1) + min);
    }

    public static int getHugeRandom(){
        return getRandom(3000, 5000);
    }
    public static int getLongRandom(){
        return getRandom(2000, 2500);
    }
    public static int getMediumRandom(){
        return getRandom(1000, 1500);
    }

    public static int getShortRandom(){
        return getRandom(250, 500);
    }

    public static int getTinyRandom(){
        return getRandom(100, 200);
    }

    public static int getHumanRandom(){
        return getRandom(400, 2400);
    }
}
