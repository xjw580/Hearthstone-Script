package club.xiaojiawei.hearthstone.utils;

import java.util.Random;

/**
 * @author 肖嘉威
 * @date 2022/11/24 19:41
 */
public class RandomUtil {



    private static final Random random = new Random();

    public static int getRandom(int min, int max){
        return (int) (random.nextDouble() * (max - min + 1) + min);
    }

    public static int getLongRandom(){
        return getRandom(1800, 2200);
    }
    public static int getMediumRandom(){
        return getRandom(800, 1200);
    }

    public static int getShortRandom(){
        return getRandom(200, 600);
    }

    public static int getHumanRandom(){
        return getRandom(400, 2400);
    }
}
