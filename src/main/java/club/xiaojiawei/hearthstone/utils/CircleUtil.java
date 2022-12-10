package club.xiaojiawei.hearthstone.utils;

import lombok.Data;

import static java.lang.Math.*;

/**
 * @author 肖嘉威
 * @date 2022/11/25 19:58
 */
public class CircleUtil {

    private static final Coordinates cc = new Coordinates();

    @Data
    public static class Coordinates{

        private double x;

        private double y;

        private Coordinates() {
        }

        public Coordinates(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private static Coordinates c1;

    private static Coordinates c2;

    public static boolean replace(Coordinates c1, Coordinates c2){
        if (Math.abs(c1.x - c2.x) > 2 * r || Math.abs(c1.y - c2.y) > 2 * r){
            return false;
        }
        CircleUtil.c1 = c1;
        CircleUtil.c2 = c2;
        calcCC();
        return true;
    }

    private static final double r = 800;


    /**
     * 根据圆上两点和半径求出圆心坐标
     */
    private static void calcCC(){
        double c1, c2, A, B, C, cy1, cx1, cx2, cy2, x1 = CircleUtil.c1.getX(), y1 = CircleUtil.c1.getY(), x2 = CircleUtil.c2.getX(), y2 = CircleUtil.c2.getY();
        c1 = (pow(x2, 2) - pow(x1, 2) + pow(y2, 2) - pow(y1, 2)) / 2 / (x2 - x1);
        c2 = (y2 - y1) / (x2 - x1);

        A = 1.0 + pow(c2, 2);
        B = 2 * (x1 - c1) * c2 - 2 * y1;
        C = pow((x1 - c1), 2) + pow(y1, 2) - pow(r, 2);

        double sqrt = Math.sqrt(B * B - 4 * A * C);
//        能算出两个圆心
//        圆心1
        cy1 = (-B + sqrt) / 2 / A;
        cx1 = c1 - c2 * cy1;
        System.out.println(cx1);
        System.out.println(cy1);
//        圆心2，使用此圆心
        cy2 = (-B - sqrt) / 2 / A;
        cx2 = c1 - c2 * cy2;
        cc.setX(cx2);
        cc.setY(cy2);
        System.out.println(cx2);
        System.out.println(cy2);
    }

    public static int getBigY(double x){
        return (int) (Math.sqrt(pow(r, 2) - pow(x, 2) + cc.getX() * x * 2 - pow(cc.getX(), 2)) + cc.getY());
    }

    public static int getSmallY(double x){
        return (int) (-Math.sqrt(pow(r, 2) - pow(x, 2) + cc.getX() * x * 2 - pow(cc.getX(), 2)) + cc.getY());
    }

    public static int getBigX(double y){
        return (int) (Math.sqrt(pow(r, 2) - pow(y, 2) + cc.getY() * y * 2 - pow(cc.getY(), 2)) + cc.getX());
    }

    public static int getSmallX(double y){
        return (int) (-Math.sqrt(pow(r, 2) - pow(y, 2) + cc.getY() * y * 2 - pow(cc.getY(), 2)) + cc.getX());
    }

    public Coordinates getC1() {
        return c1;
    }

    public Coordinates getC2() {
        return c2;
    }

    public double getR() {
        return r;
    }

    public static void main(String[] args) {
        double k, x1 = 0, y1 = 0, x2 = 0, y2 = 800, R = 400;
        if (x2 - x1 == 0){
        }

    }
}
