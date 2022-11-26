package club.xiaojiawei.hearthstone.utils;

import lombok.Data;

/**
 * @author 肖嘉威
 * @date 2022/11/25 19:58
 */
public class CircleUtil {

    private final Coordinates cc = new Coordinates();

    @Data

    public static class Coordinates{

        private double x;

        private double y;

        public Coordinates() {
        }

        public Coordinates(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private Coordinates c1;

    private Coordinates c2;

    public void setC(Coordinates c1, Coordinates c2){
        this.c1 = c1;
        this.c2 = c2;
        calcCC();
    }

    private final double r = 400;


    /**
     * 根据圆上两点和半径求出圆心坐标
     */
    private void calcCC(){
        double c1, c2, A, B, C, cy1, cx1, cx2, cy2, x1 = this.c1.getX(), y1 = this.c1.getY(), x2 = this.c2.getX(), y2 = this.c2.getY();
        c1 = (Math.pow(x2, 2) - Math.pow(x1, 2) + Math.pow(y2, 2) - Math.pow(y1, 2)) / 2 / (x2 - x1);
        c2 = (y2 - y1) / (x2 - x1);

        A = 1.0 + Math.pow(c2, 2);
        B = 2 * (x1 - c1) * c2 - 2 * y1;
        C = Math.pow((x1 - c1), 2) + Math.pow(y1, 2) - Math.pow(r, 2);

        double sqrt = Math.sqrt(B * B - 4 * A * C);
        cy1 = (-B + sqrt) / 2 / A;
        cx1 = c1 - c2 * cy1;

        cy2 = (-B - sqrt) / 2 / A;
        cx2 = c1 - c2 * cy2;
        cc.setX(cx2);
        cc.setY(cy2);
    }

    public int getY(double x){
        return (int) (Math.sqrt(Math.pow(r, 2) - Math.pow(x, 2) + cc.getX() * x * 2 - Math.pow(cc.getX(), 2)) + cc.getY());
    }

    public int getX(double y){
        return (int) (Math.sqrt(Math.pow(r, 2) - Math.pow(y, 2) + cc.getY() * y * 2 - Math.pow(cc.getY(), 2)) + cc.getX());
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
}
