package com.chef.painter.doughnutPainter.painter;

import com.chef.painter.doughnutPainter.PluginMain;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BlockPainter {
    // 实际gray约为 210、120、60、10
    static Material[] blockList_gray = new Material[]{Material.BLACK_CONCRETE, Material.GRAY_CONCRETE, Material.LIGHT_GRAY_CONCRETE, Material.WHITE_CONCRETE};
//    static Material[] blockList_all = new Material[]{Material.WHITE_CONCRETE, Material.LIGHT_GRAY_CONCRETE, Material.GRAY_CONCRETE, Material.BLACK_CONCRETE,Material.RED_CONCRETE, Material.MAGENTA_CONCRETE, Material.PINK_CONCRETE,Material.GREEN_CONCRETE, Material.LIME_CONCRETE, Material.YELLOW_CONCRETE, Material.ORANGE_CONCRETE, Material.BROWN_CONCRETE, Material.LIGHT_BLUE_CONCRETE,Material.CYAN_CONCRETE, Material.BLUE_CONCRETE, Material.PURPLE_CONCRETE};

    static public boolean DrawBlocks(Block startBlock, String imgPath, double ratio, int styleCode) {
        int[][] imgArr;
        File file = new File(imgPath);
        if (!file.exists()) {
            PluginMain.logger.info("file not exists!");
            return false;
        }
        try {
            BufferedImage img = ImageIO.read(file);  //原始图片
            imgArr = ImageUtils.ImageToGray(ImageUtils.resizeImage(img, ratio, styleCode));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // 开始绘制
        return blocksCreate(imgArr, startBlock);
    }

    static public boolean DrawBlocks(Block startBlock, String imgPath, int width, int height, int styleCode) {
        int[][] imgArr;
        File file = new File(imgPath);
        if (!file.exists()) {
            PluginMain.logger.info("file not exists!");
            return false;
        }
        try {
            BufferedImage img = ImageIO.read(file);  //原始图片
            imgArr = ImageUtils.ImageToGray(ImageUtils.resizeImage(img, width, height, styleCode));  // 处理图片
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // 开始绘制
        return blocksCreate(imgArr, startBlock);
    }

    static private boolean blocksCreate(int[][] imgArr, Block startBlock) {
        int w = imgArr.length;
        int h = imgArr[0].length;
//        if (w > 800 || h > 800) {
//            PluginMain.logger.info("试图绘制的方块画过大！width: " + w + "height: " + h);
//            return false;
//        }
        int x = startBlock.getX();
        int y = startBlock.getY();
        int z = startBlock.getZ();
        World world = startBlock.getWorld();
        for (int i = z; i < z + w; i++) {
            for (int j = x; j < x + h; j++) {
                world.getBlockAt(j, y, i).setType(blockList_gray[imgArr[j - x][i - z]]);
            }
        }
        return true;
    }

//    static double colorDistance(int[] rgb1, int[] rgb2) {
//        // 获取两个颜色的相似性
//        double avg = (rgb1[0] + rgb1[0]) >> 1;
//        return Math.sqrt((2 + avg / 256) * Math.pow(rgb1[0] - rgb2[0], 2) + 4 * Math.pow(rgb1[1] - rgb2[1], 2) + (2 + (255 - avg) / 256) * Math.pow(rgb1[2] - rgb2[2], 2));
//    }
}
