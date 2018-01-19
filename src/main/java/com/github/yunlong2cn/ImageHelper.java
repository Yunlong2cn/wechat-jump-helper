package com.github.yunlong2cn;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageHelper {

    public static Color[][] toColors(String path) throws IOException {
        File file = new File(path);
        BufferedImage bi = ImageIO.read(file);
        int width = bi.getWidth();
        int height = bi.getHeight();

        Color[][] colors = new Color[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixes = bi.getRGB(i, j);
                colors[i][j] = new Color(pixes);
            }
        }

        return colors;

    }

    public static double distance(Color src, Color dest) {
        int r = Math.abs(src.getRed() - dest.getRed());
        int g = Math.abs(src.getGreen() - dest.getGreen());
        int b = Math.abs(src.getBlue() - dest.getBlue());
        return Math.sqrt(Math.pow(r, 2) + Math.pow(g, 2) + Math.pow(b, 2));
    }
}
