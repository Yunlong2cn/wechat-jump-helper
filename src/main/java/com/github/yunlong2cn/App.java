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

        System.out.println("[+] Game start");

        while (isJump) {

            // 随机等待几秒钟，增加真实性
            Random random = new Random();
            int second = random.nextInt(6);
            second = second < 1 ? 1 : second;
            System.out.println("++++++++");
            System.out.println("[+] Wait for "+ second +" senconds");
            Thread.sleep(second * 1000);

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
            // 自右往左，自下往上（因为棋子是分开的，自下往上能快速定位到棋子底部，自右往左，是为了避免棋子阴影造成的干扰）
            // 先计算棋子坐标，再计算色块坐标（由于棋子有可能高于色块位置，所以需要用棋子的 x 坐标来判断色块坐标的正确性）
            Color expectPieceColor = new Color(60, 56, 83); // 期望边界颜色
            Position pieceCenter = new Position();
            for (int x = colors.length - 1; x >= 0; x--) {
                if(pieceCenter.getX() > 0) {
                    break;
                }
                for (int y = colors[0].length - 1; y >= 0; y--) {
                    if(pieceCenter.getX() > 0) {
                        break;
                    }
                    Color color = colors[x][y];
                    if(ImageHelper.distance(color, expectPieceColor) < 15) {
                        pieceCenter.setX(x - 38);
                        pieceCenter.setY(y);
                        pieceCenter.setColor(color);
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
            Position blockRight = new Position(); // 色块右侧位置

            // 计算色块中心坐标
            // 从上往下，从右往左（避免阴影干扰）
            for (int y = 288; y < colors[0].length; y++) {
                // 防止跨色块
                if(blockRight.getY() > 0 && y - blockRight.getY() > 80) {
                    break;
                }
                for (int x = colors.length - 1; x >= 0; x--) {
                    Color color = colors[x][y];
                    double distance = ImageHelper.distance(color, backColor);// 取得当前颜色值与画面颜色的距离

                    if(blockStart.getX() == 0) {
                        if(distance > 30 && Math.abs(x - pieceCenter.getX()) > 80) {// 距离大于 30，说明颜色变化明显且距离棋子的位置较远，则有可能为想要获得的位置
                            // 开始位置
                            blockStart.setX(x);
                            blockStart.setY(y);
                            blockStart.setColor(color);

                            // 认定的右侧位置
                            blockRight.setX(x);
                            blockRight.setY(y);
                            blockRight.setColor(color);

                            break;
                        }
                    } else if(x > blockRight.getX() && ImageHelper.distance(blockRight.getColor(), color) < 10) {// 取到的右侧位置与当前位置距离较近且当前位置大于上次取到的右侧坐标x值，则重置右侧坐标
                        blockRight.setX(x);
                        blockRight.setY(y);
                        break;
                    }
                }
            }

            Position blockCenter = new Position();
            blockCenter.setY(blockRight.getY());
            blockCenter.setX(blockStart.getX());


            System.out.println("[+] blockCenter = " + blockCenter);

            int x = Math.abs(pieceCenter.getX() - blockCenter.getX());
            int y = Math.abs(pieceCenter.getY() - blockCenter.getY());
            double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

            // 按压时长 time 有两种算法
            // 1. 根据跳跃距离 * 系数
            double time = distance * 1.38; // 需要根据分辨率确定系数
            // 2. 根据跳跃宽度
            int totalTime = 1700; //跳过整个屏幕宽度所需时长
            int distanceX = Math.abs(blockCenter.getX() - pieceCenter.getX());
            time = Math.round((float)distanceX/colors.length * totalTime);// 此方法计算结果可无视分辨率
            System.out.println(String.format("[+] Distance: %s/%s press time = %s", distanceX, colors.length, time));

            process = runtime.exec("adb shell input swipe 10 10 11 11 " + (int)time);
            if(process.waitFor() == 0) {
                System.out.println("[+] press success");
            }
        }

        System.out.println("[+] Quit");
    }
}
