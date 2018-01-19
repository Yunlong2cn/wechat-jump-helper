package com.github.yunlong2cn;

import java.awt.*;

public class Position {
    private int x;
    private int y;
    private Color color;


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
