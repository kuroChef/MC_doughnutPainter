package com.chef.painter.doughnutPainter.commands;

import com.chef.painter.doughnutPainter.PluginMain;
import com.chef.painter.doughnutPainter.painter.ImageUtils;
import com.chef.painter.doughnutPainter.painter.MapPainter;
import com.chef.painter.doughnutPainter.painter.BlockPainter;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.List;

public class MainCommands implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            PluginMain.logger.info("发送者必须是一个玩家！");
            PluginMain.logger.info("sender must be a player!");
            return false;
        }
//        if (!player.isOp()) {
//            MsgSender.send(player, MsgSender.MsgLevel.ERROR, player.getName() + " 你不能使用这个命令，因为你不是管理员。op:" + player.isOp());
//            return false;
//        }
        if (args.length > 0) {
            switch (args[0]) {
                case "help":
                    comHelp(player);
                    break;
                case "op":
                    comOp(player, args);
                    break;
                case "bp":
                    comBP(player, args);
                    break;
                case "map":
                    comMap(player, args);
                    break;
                default:
                    inputArgsErr(player);
                    break;
            }
        } else {
            comHelp(player);
        }
        return true;
    }

    private void comOp(Player player, String[] args) {
        if (args.length == 1) {
            MsgSender.send(player, MsgSender.MsgLevel.NOTICE, "当前op状态：" + player.isOp());
        } else if (args.length == 2 && (Objects.equals(args[1], "1") || Objects.equals(args[1], "0"))) {
            player.setGameMode(GameMode.CREATIVE);
            player.setOp(Objects.equals(args[1], "1"));
            MsgSender.send(player, MsgSender.MsgLevel.WARNING, "op状态更改：" + player.isOp());
        } else {
            inputArgsErr(player);
        }
    }

    private void comBP(Player player, String[] args) {
        //player.getLocation().getYaw()   左负右正(-180,180)    左右朝向
        //player.getLocation().getPitch() 上负下正(-90,90)      上下朝向
        //根据玩家视角的朝向决定方向，还没ixe
        boolean res;
        Block startBlock = player.getLocation().getBlock();
        int posArgsLength = 0;
        if (args.length >= 4) {
            if ("position".equals(args[args.length - 4])) {
                try {
                    startBlock = player.getWorld().getBlockAt(Double.valueOf(args[args.length - 3]).intValue(), Double.valueOf(args[args.length - 2]).intValue(), Double.valueOf(args[args.length - 1]).intValue());
                    MsgSender.send(player, MsgSender.MsgLevel.WARNING, "block: " + startBlock.getLocation());
                    posArgsLength = 4;
                } catch (NumberFormatException e) {
                    inputArgsErr(player);
                    return;
                }
            }
            if ("ratio".equals(args[2])) {  // 按比例缩小
                double ratio;
                try {  // 判断数据是否合法
                    ratio = Double.parseDouble(args[3]);
                    if (ratio > 2) {
                        MsgSender.send(player, MsgSender.MsgLevel.ERROR, "ratio数值过大，极可能导致服务器宕机！");
                        return;
                    } else if (ratio <= 0) {
                        MsgSender.send(player, MsgSender.MsgLevel.ERROR, "ratio不可取值小于等于0！");
                        return;
                    }
                } catch (NumberFormatException e) {
                    inputArgsErr(player);
                    return;
                }
                if (args.length == 5 + posArgsLength) {  // 指定裁剪图片方式
                    int index = Arrays.binarySearch(ImageUtils.styleList, args[2].toUpperCase());
                    if (index < 0) {
                        inputArgsErr(player);
                        return;
                    }
                    res = BlockPainter.DrawBlocks(startBlock, args[1], ratio, index);
                } else if (args.length == 4 + posArgsLength) {  // 不指定图片裁剪方式
                    res = BlockPainter.DrawBlocks(startBlock, args[1], ratio, -1);
                } else {
                    inputArgsErr(player);
                    return;
                }
            } else if ("size".equals(args[2])) {  // 按给定长宽缩小
                try {  // 判断数据是否合法
                    int w = Integer.parseInt(args[3]);
                    int h = Integer.parseInt(args[4]);
//                    if (w > 800 || h > 800) {
//                        MsgSender.send(player, MsgSender.MsgLevel.ERROR, "width或height数值过大，极可能导致服务器宕机！width或height数值请小于等于800");
//                        return;
//                    } else
                    if (w <= 0 || h <= 0) {
                        MsgSender.send(player, MsgSender.MsgLevel.ERROR, "width和height不可取值小于等于0！");
                        return;
                    }
                } catch (NumberFormatException e) {
                    inputArgsErr(player);
                    return;
                }
                if (args.length == 6 + posArgsLength) {  // 指定裁剪图片方式
                    int index = Arrays.binarySearch(ImageUtils.styleList, args[5].toUpperCase());
                    if (index < 0) {
                        inputArgsErr(player);
                        return;
                    }
                    res = BlockPainter.DrawBlocks(startBlock, args[1], Integer.parseInt(args[3]), Integer.parseInt(args[4]), index);
                } else if (args.length == 5 + posArgsLength) {  // 不指定图片裁剪方式
                    res = BlockPainter.DrawBlocks(startBlock, args[1], Integer.parseInt(args[3]), Integer.parseInt(args[4]), -1);
                } else {
                    inputArgsErr(player);
                    return;
                }
            } else {
                inputArgsErr(player);
                return;
            }
        } else {
            inputArgsErr(player);
            return;
        }
        if (res) {
            MsgSender.send(player, MsgSender.MsgLevel.NOTICE, "已创建方块画，起始坐标：{x=" + startBlock.getX() + ", y=" + startBlock.getY() + ", z=" + startBlock.getZ() + "}");
        } else {
            MsgSender.send(player, MsgSender.MsgLevel.ERROR, "方块画创建失败");

        }
    }

    private void comMap(Player player, String[] args) {
        ItemStack map;
        if (args.length == 2) {  // 不指定图片裁剪方式，默认不裁剪直接缩小
            map = MapPainter.createMap(player, args[1], -1);
        } else if (args.length == 3) {  // 指定图片裁剪方式
            int index = Arrays.binarySearch(ImageUtils.styleList, args[2].toUpperCase());  // 检查样式名是否存在
            if (index < 0) {
                inputArgsErr(player);
                return;
            }
            map = MapPainter.createMap(player, args[1], index);  // 获取地图画
        } else {
            inputArgsErr(player);
            return;
        }
        if (map == null) {
            MsgSender.send(player, MsgSender.MsgLevel.ERROR, "图片文件可能不存在！");
            return;
        }
        player.getInventory().addItem(map);  // 将地图画放入玩家物品栏
        MsgSender.send(player, MsgSender.MsgLevel.NOTICE, "已给予 " + player.getName() + " 地图画");
    }

    private void comHelp(Player player) {
        MsgSender.send(player, MsgSender.MsgLevel.HELP, "-------------帮助手册beta-------------", true);
        MsgSender.send(player, MsgSender.MsgLevel.HELP, "/cep op {0|1} (获取op状态、设置op)", true);
        MsgSender.send(player, MsgSender.MsgLevel.HELP, "以下图片路径使用绝对路径，目前只支持jpg,png文件(png不支持alpha通道)；", true);
        MsgSender.send(player, MsgSender.MsgLevel.HELP, "/cep map [imgPath] {center|top|bottom|left|right} (创建地图画；路径使用绝对路径，目前只支持jpg文件；裁剪图片为正方形后缩小，长宽锁定128*128)", true);
        MsgSender.send(player, MsgSender.MsgLevel.HELP, "/cep bp [imgPath] ratio|size [ratio|width height] {center|top|bottom|left|right} {position [x] [y] [z]} (创建方块画(慎用，目前只支持创建灰度图)；过大可能会卡服；默认起始点在玩家所在位置)", true);
        MsgSender.send(player, MsgSender.MsgLevel.HELP, "-------------------------------------", true);
        MsgSender.send(player, MsgSender.MsgLevel.HELP, "输入 /cep help 获取此帮助");
    }

    private void inputArgsErr(Player player) {
        MsgSender.send(player, MsgSender.MsgLevel.ERROR, "输入有误");
        MsgSender.send(player, MsgSender.MsgLevel.HELP, "输入 /cep help 来获取帮助");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
//        if (!sender.isOp()) {
//            return null;
//        }
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.add("op");
            list.add("map");
            list.add("bp");
//            list.add("help");
        } else if (args.length > 1) {
            if (args.length == 3 && "map".equals(args[0])) {
                list.add("center");
                list.add("top");
                list.add("bottom");
                list.add("left");
                list.add("right");
            } else if ("bp".equals(args[0])) {
                if (args.length == 3) {
                    list.add("size");
                    list.add("ratio");
                } else if (args.length == 5 && "ratio".equals(args[2])) {
                    list.add("center");
                    list.add("top");
                    list.add("bottom");
                    list.add("left");
                    list.add("right");
                    list.add("position");
                } else if (args.length == 6 && "size".equals(args[2])) {
                    list.add("center");
                    list.add("top");
                    list.add("bottom");
                    list.add("left");
                    list.add("right");
                    list.add("position");
                } else if (args.length == 7 && "size".equals(args[2])) {
                    list.add("center");
                    list.add("top");
                    list.add("bottom");
                    list.add("left");
                    list.add("right");
                    list.add("position");
                }
            }
        }
        return list;
    }
}
