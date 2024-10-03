package club.xiaojiawei.utils;

import lombok.Getter;

import java.util.Random;

/**
 * 随机数生成工具
 * @author 肖嘉威
 * @date 2022/11/24 19:41
 */
public class RandomUtil {

    @Getter
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static int getRandom(int min, int max){
        if (min > max){
            return getRandom(max, min);
        }
        return (int) (RANDOM.nextDouble() * (max - min + 1) + min);
    }

    public static double getRandom(double min, double max){
        if (min > max){
            return getRandom(max, min);
        }
        return RANDOM.nextDouble(min, max);
    }

    public static int getHugeRandom(){
        return getRandom(3000, 5000);
    }
    public static int getLongRandom(){
        return getRandom(2000, 3000);
    }
    public static int getMediumRandom(){
        return getRandom(1000, 1600);
    }
    public static int getShortMediumRandom(){
        return getRandom(500, 800);
    }
    public static int getShortRandom(){
        return getRandom(250, 450);
    }
    public static int getTinyRandom(){
        return getRandom(100, 200);
    }
    public static int getHumanRandom(){
        return getRandom(200, 2000);
    }
}
