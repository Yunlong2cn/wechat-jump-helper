package com.github.yunlong2cn;

import java.awt.*;

/**
 * 坐标点对象
 *
 * @param x 横坐标
 * @param y 纵坐标
 * @param color RGB 颜色值
 *
 */
public class Position {
    private int x;
    private int y;
    private Color color;

    public Position() {}

    public Position(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "x = "+ x +", y = " + y;
    }
}
