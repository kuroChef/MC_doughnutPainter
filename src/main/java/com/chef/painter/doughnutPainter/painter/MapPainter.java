package com.chef.painter.doughnutPainter.painter;

import com.chef.painter.doughnutPainter.PluginMain;
import com.chef.painter.doughnutPainter.commands.MsgSender;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;

// 2022 08 29  先写入map dat文件，在通过id获取该地图（解决服务器重启后地图画失效）
// 清除地图画，重置id：销毁世界里的地图画，关闭服务器，删除map dat文件
public class MapPainter {
    static public ItemStack createMap(Player player, String imgPath, int styleCode) {
        //处理图片
        BufferedImage im;
        File file = new File(imgPath);
        if (!file.exists()) {
            PluginMain.logger.info("file not exists!");
            return null;
        }
        try {
            BufferedImage img = ImageIO.read(file);  //原始图片
            if (img == null) {
                PluginMain.logger.info("image read error!");
                return null;
            }
            im = ImageUtils.resizeImage(img, 128, 128, styleCode);  // 地图最大128*128
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        int mapID = writeToDat(player.getWorld(), im);
        MapView view = Bukkit.getMap(mapID);
        ItemStack mapItem = new ItemStack(Material.FILLED_MAP, 1);
        MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();
        mapMeta.setMapView(view);
        mapMeta.setColor(Color.fromRGB(50, 255, 255));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_AQUA + "[doughnutPainter] MAP IMAGE");
        if (styleCode != -1) {
            lore.add(ChatColor.DARK_AQUA + ImageUtils.styleList[styleCode]);
        }
        mapMeta.setLore(lore);
        mapItem.setItemMeta(mapMeta);
        MsgSender.send(player, MsgSender.MsgLevel.NOTICE, "已创建ID为 " + mapID + " 的地图画");
        return mapItem;
    }

    public static int writeToDat(World world, Image im) {
        byte[] baseBytes = {10, 0, 0, 3, 0, 11, 68, 97, 116, 97, 86, 101, 114, 115, 105, 111, 110, 0, 0, 10, -86, 10, 0, 4, 100, 97, 116, 97, 1, 0, 17, 117, 110, 108, 105, 109, 105, 116, 101, 100, 84, 114, 97, 99, 107, 105, 110, 103, 0, 9, 0, 6, 102, 114, 97, 109, 101, 115, 0, 0, 0, 0, 0, 1, 0, 6, 108, 111, 99, 107, 101, 100, 1, 3, 0, 7, 120, 67, 101, 110, 116, 101, 114, 0, 0, 1, -64, 4, 0, 8, 85, 85, 73, 68, 77, 111, 115, 116, 57, -32, -60, 123, -78, 126, 64, -23, 4, 0, 9, 85, 85, 73, 68, 76, 101, 97, 115, 116, -122, -75, -66, 101, -112, 115, -4, 13, 9, 0, 7, 98, 97, 110, 110, 101, 114, 115, 0, 0, 0, 0, 0, 8, 0, 9, 100, 105, 109, 101, 110, 115, 105, 111, 110, 0, 19, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 111, 118, 101, 114, 119, 111, 114, 108, 100, 1, 0, 16, 116, 114, 97, 99, 107, 105, 110, 103, 80, 111, 115, 105, 116, 105, 111, 110, 0, 3, 0, 7, 122, 67, 101, 110, 116, 101, 114, -1, -1, -3, -64, 1, 0, 5, 115, 99, 97, 108, 101, 3, 7, 0, 6, 99, 111, 108, 111, 114, 115, 0, 0, 64, 0};
        byte[] bytes = new byte[16616];
        System.arraycopy(baseBytes, 0, bytes, 0, baseBytes.length);
        System.arraycopy(MapPalette.imageToBytes(im), 0, bytes, baseBytes.length, 16384);  // 写入图片byte数据

        int mapId = 2147483647;  // 起始id
        String mapDataFolderPath = System.getProperty("user.dir") + "\\" + world.getName() + "\\data";
        String datPath = mapDataFolderPath + "\\map_" + mapId + ".dat";
        while (new File(datPath).exists()) {
//            PluginMain.logger.info(datPath + " 已存在...");
            mapId--;
            datPath = mapDataFolderPath + "\\map_" + mapId + ".dat";
        }
        try {  // 写入文件
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream(datPath));
            gzipOutputStream.write(bytes);
            gzipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapId;
    }
}
