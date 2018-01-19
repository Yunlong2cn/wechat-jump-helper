package com.github.yunlong2cn;

import java.awt.*;
import java.io.IOException;
import java.util.Random;

public class App {

    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length == 0) {
            System.out.println("[-] 输入跳一跳截图存储路径");
            return;
        }
        Runtime runtime = Runtime.getRuntime();

        boolean isJump = true;

        String path = args[0];

        while (isJump) {
            runtime.exec("adb shell screencap /sdcard/jump.png");
            Thread.sleep(2000);
            runtime.exec("adb pull /sdcard/jump.png " + path);
            Thread.sleep(3000);

            Color[][] colors = ImageHelper.toColors(path + "/" + "jump.png");

            Color startColor = colors[0][0];
            if(startColor.equals(new Color(51,46,44))) {
                isJump = false;
                continue;
            }

            Color backColor = colors[0][288];

            Position blockStart = new Position(); // 色块起始位置
            Position blockLeft = new Position(); // 色块左侧位置

            // 计算色块中心坐标
            // 从上往下，从左往右
            for (int y = 288; y < colors[0].length; y++) {
                for (int x = 0; x < colors.length; x++) {
                    Color color = colors[x][y];
                    if (!color.equals(backColor)) {
                        double sim = ImageHelper.sim(color, backColor);
                        if(sim > 30) {// 说明当前像素可能是我想要的位置
                            if(blockStart.getX() == 0 && blockStart.getY() == 0) {// 说明色块开始位置
                                blockStart.setX(x);
                                blockStart.setY(y);
                                blockStart.setColor(color);

                                blockLeft.setX(x);
                                blockLeft.setY(y);
                            } else {
                                if(color.equals(blockStart.getColor())) {// 如果当前 color 与 start 位置的 color 相同，说明是 left 的起始位置
                                    if(blockLeft.getX() >= x && blockLeft.getY() < y) {// 上一次保存的 left 位置如果小于这次的 x 位置，则说明 left 并未取到最左侧
                                        blockLeft.setX(x);
                                        blockLeft.setY(y);
                                    }
                                }
                            }

                            break;// 一旦在当前行发现了想要的位置，则记录后跳出并进行下一行处理
                        }

                    }
                }
            }

            Position blockCenter = new Position();
            blockCenter.setY(blockLeft.getY());
            blockCenter.setX(blockStart.getX());


            System.out.println("[+] blockCenter = " + blockCenter);


            // 计算棋子坐标
            // 自下往上，自右往左（因为棋子是分开的，自下往上能快速定位到棋子底部，自右往左，是为了避免棋子阴影造成的干扰）
            Position pieceCenter = new Position();
            Color pieceColor = new Color(57,57,99); // 认为棋子的RGB颜色值应为 pieceColor
            for (int y = colors[0].length - 1; y >= 0; y--) {
                if(pieceCenter.getX() > 0) {
                    break;
                } else {
                    for (int x = colors.length - 1; x >= 0; x--) {
                        double sim = ImageHelper.sim(pieceColor, colors[x][y]);
                        if(sim < 1) {
                            pieceCenter.setY(y - 9);
                            pieceCenter.setX(x - 21);
                            break;
                        }
                    }
                }
            }

            System.out.println("[+] pieceCenter = " + pieceCenter);

            int x = Math.abs(pieceCenter.getX() - blockCenter.getX());
            int y = Math.abs(pieceCenter.getY() - blockCenter.getY());
            double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

            double time = distance * 1.4;
            System.out.println("[+] press time = " + time);

            runtime.exec("adb shell input swipe 10 10 11 11 " + (int)time);

            Random random = new Random();
            int second = random.nextInt(5) + 2;
            System.out.println("[+] wait for "+ second +" senconds");
            Thread.sleep(second * 1000);

            isJump = true;
        }

        System.out.println("[+] quit");
    }
}
