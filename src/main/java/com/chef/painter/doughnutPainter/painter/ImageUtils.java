package com.chef.painter.doughnutPainter.painter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {
    public static String[] styleList = new String[]{"BOTTOM", "CENTER", "LEFT", "RIGHT", "TOP"};

    public static int[] IntDataToRGB(int pixel) {
        return new int[]{(pixel & 0xff0000) >> 16, (pixel & 0xff00) >> 8, (pixel & 0xff)};
    }

//    public static int RGBToIntData(int[] rgb) {
//        return 0xff000000 | (rgb[0] << 16) | (rgb[1] << 8) | rgb[2]; // 前八位alpha通道，jpg都为1
//    }
//
//    static int GrayToIntData(int gray) {
//        return 0xff000000 | (gray << 16) | (gray << 8) | gray;
//    }

    public static BufferedImage resizeImage(BufferedImage img, int width, int height, int styleCode) {
        if (styleCode != -1) {
            img = getSquareImage(img, styleCode);
        }
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        bufferedImage.getGraphics().drawImage(img.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        return bufferedImage;
    }

    public static BufferedImage resizeImage(BufferedImage img, double ratio, int styleCode) {
        if (styleCode != -1) {
            img = getSquareImage(img, styleCode);
        }
        int width = Double.valueOf(img.getWidth() * ratio).intValue();
        int height = Double.valueOf(img.getHeight() * ratio).intValue();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        bufferedImage.getGraphics().drawImage(img.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        return bufferedImage;
    }

    public static BufferedImage getSquareImage(BufferedImage img, int styleCode) {
        int w = img.getWidth();
        int h = img.getHeight();
        int x = 0;
        int y = 0;
        int min = Math.min(w, h);
        switch (styleCode) {
            case 0:  // bottom
                if (w > h) {
                    x = (w - h) / 2;
                } else {
                    y = (h - w);
                }
                break;
            case 1: // center
                if (w > h) {
                    x = (w - h) / 2;
                } else {
                    y = (h - w) / 2;
                }
                break;
            case 2:  // left
                if (h > w) {
                    y = (h - w) / 2;
                }
                break;
            case 3:  // right
                if (w > h) {
                    x = (w - h);
                } else {
                    y = (h - w) / 2;
                }
                break;
            case 4:  // top
                if (w > h) {
                    x = (w - h) / 2;
                }
                break;
        }
        return img.getSubimage(x, y, min, min);
    }

    public static int[][] ImageToGray(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
//        BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        int[][] imgArr = new int[width][height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int[] rgb = IntDataToRGB(img.getRGB(j, i));
                int gray = (rgb[0] * 299 + rgb[1] * 587 + rgb[2] * 114 + 500) / 1000;
                if (gray < 63) {
                    gray = 0;
                } else if (gray < 127) {
                    gray = 1;
                } else if (gray < 191) {
                    gray = 2;
                } else {
                    gray = 3;
                }
//                newImg.setRGB(j, i, GrayToIntData(gray));
                imgArr[j][i] = gray;
            }
        }
        return imgArr;
    }
}
