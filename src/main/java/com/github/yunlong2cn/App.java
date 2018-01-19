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
        Process process;

        boolean isJump = true;

        boolean isAutoRestart = false;

        if(args.length == 2) {
            isAutoRestart = true;
        }

        String path = args[0];

        while (isJump) {
            String cmd = "adb shell screencap /sdcard/jump.png";
            int exitCode = -1;
            process = runtime.exec(cmd);
            exitCode = process.waitFor();
            if(exitCode != 0) {
                System.out.println("[-] jump.png generate failed");
                isJump = false;
                continue;
            }
            cmd = "adb pull /sdcard/jump.png " + path;
            process = runtime.exec(cmd);
            exitCode = process.waitFor();
            if(exitCode != 0) {
                System.out.println("[-] jump.png pulled failed");
                isJump = false;
                continue;
            }

            Color[][] colors = ImageHelper.toColors(path + "/" + "jump.png");

            Color backColor = colors[0][288];

            if(backColor.equals(new Color(51,49,36))) {
                isJump = false;
                System.out.println("[+] Game over");
                continue;
            }


            // 计算棋子坐标
            // 自下往上，自右往左（因为棋子是分开的，自下往上能快速定位到棋子底部，自右往左，是为了避免棋子阴影造成的干扰）
            // 先计算棋子坐标，再计算色块坐标（由于棋子有可能高于色块位置，所以需要用棋子的 x 坐标来判断色块坐标的正确性）
            Position pieceCenter = new Position();
            Color pieceColor = new Color(57,57,99); // 认为棋子的RGB颜色值应为 pieceColor
            for (int y = colors[0].length - 1; y >= 0; y--) {
                if(pieceCenter.getX() > 0) {
                    break;
                } else {
                    for (int x = colors.length - 1; x >= 0; x--) {
                        double sim = ImageHelper.distance(pieceColor, colors[x][y]);
                        int distance = Math.abs(pieceCenter.getX() - x);// 从 x 坐标看距离棋子的长度
                        if(sim < 1 && distance > 50) {
                            pieceCenter.setY(y - 9);
                            pieceCenter.setX(x - 21);
                            break;
                        }
                    }
                }
            }

            System.out.println("[+] pieceCenter = " + pieceCenter);

            if(pieceCenter.getX() == 0 || pieceCenter.getY() == 0) {
                System.out.println("[-] Have no piece on dashboard");
                if(isAutoRestart) {
                    cmd = "adb shell input tap 555 1575";
                    if(runtime.exec(cmd).waitFor() == 0) {
                        System.out.println("[+] Restart Game success");
                    } else {
                        System.out.println("[-] Restart Game failed");
                    }
                } else {
                    isJump = false;
                }

                continue;
            }





            Position blockStart = new Position(); // 色块起始位置
            Position blockLeft = new Position(); // 色块左侧位置

            // 计算色块中心坐标
            // 从上往下，从左往右
            for (int y = 288; y < colors[0].length; y++) {
                for (int x = 0; x < colors.length; x++) {
                    Color color = colors[x][y];
                    if (!color.equals(backColor)) {
                        double sim = ImageHelper.distance(color, backColor);
                        if(sim > 30) {// 说明当前像素可能是我想要的位置
                            if(blockStart.getX() == 0 && blockStart.getY() == 0) {// 说明色块开始位置
                                blockStart.setX(x);
                                blockStart.setY(y);
                                blockStart.setColor(color);

                                blockLeft.setX(x);
                                blockLeft.setY(y);

                                break;// 一旦在当前行发现了想要的位置，则记录后跳出并进行下一行处理
                            } else {
                                if(color.equals(blockStart.getColor())) {// 如果当前 color 与 start 位置的 color 相同，说明是 left 的起始位置
                                    int yDistance = y - blockLeft.getY();// 当前颜色值的 y 坐标与上一次定义为左侧位置的 y 坐标的距离
                                    // 认为：当 yDistance < 阈值时，则是同一个色块
                                    // 认为：上一次保存位置 x 坐标应大于或等于当前位置 x 坐标
                                    // 认为：上一次保存位置 y 坐标应小于当前位置 y 坐标
                                    if(blockLeft.getX() >= x && blockLeft.getY() < y && yDistance < 200) {// 上一次保存的 left 位置如果小于这次的 x 位置，则说明 left 并未取到最左侧
                                        blockLeft.setX(x);
                                        blockLeft.setY(y);

                                        break;// 一旦在当前行发现了想要的位置，则记录后跳出并进行下一行处理
                                    }
                                }
                            }


                        }

                    }
                }
            }

            Position blockCenter = new Position();
            blockCenter.setY(blockLeft.getY());
            blockCenter.setX(blockStart.getX());


            System.out.println("[+] blockCenter = " + blockCenter);

            int x = Math.abs(pieceCenter.getX() - blockCenter.getX());
            int y = Math.abs(pieceCenter.getY() - blockCenter.getY());
            double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

            double time = distance * 1.4;
            System.out.println("[+] press time = " + time);

            process = runtime.exec("adb shell input swipe 10 10 11 11 " + (int)time);
            if(process.waitFor() == 0) {
                System.out.println("[+] press success");
            }

            Random random = new Random();
            int second = random.nextInt(6);
            second = second < 3 ? 3 : second;
            System.out.println("[+] wait for "+ second +" senconds");
            Thread.sleep(second * 1000);
        }

        System.out.println("[+] Quit");
    }
}
