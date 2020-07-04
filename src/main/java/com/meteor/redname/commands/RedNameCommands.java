package com.meteor.redname.commands;

import com.meteor.redname.RedName;
import com.meteor.redname.data.PlayerData;
import com.meteor.redname.data.PointsUser;
import com.meteor.redname.tools.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class RedNameCommands implements CommandExecutor {
    RedName plugin;
    public RedNameCommands(RedName plugin){
        this.plugin = plugin;
    }
    private boolean isInteger(String string){
        try {
            int i = Integer.valueOf(string);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String arg, String[] args) {
        MessageManager mes = plugin.getRnData().getMessageManager();
        if(args.length==0||args[0].equalsIgnoreCase("help")){
            if(commandSender.isOp()){
                commandSender.sendMessage("§f§lMeteorRedName - §a红名系统");
                commandSender.sendMessage("§7/mrn kill [add/take/set] [player] [points]");
                commandSender.sendMessage("§3 增加/扣除/设置 指定玩家的杀戮值");
                commandSender.sendMessage("§7/mrn just [add/take/set] [player] [points]");
                commandSender.sendMessage("§3 增加/扣除/设置 指定玩家的正义值");
                commandSender.sendMessage("§7/mrn reload");
                commandSender.sendMessage("§3 重载配置文件");
                return true;
            }
        }
        if(args.length==1&&args[0].equalsIgnoreCase("reload")&&commandSender.isOp()){
            MessageManager messageManager = new MessageManager(YamlConfiguration.loadConfiguration(
                    new File(plugin.getDataFolder()+"/lang.yml")));
            plugin.getRnData().setMessageManager(messageManager);
            plugin.reloadConfig();
            commandSender.sendMessage(mes.getString("reload-config"));
            return true;
        }
        if(args.length==4&&args[0].equalsIgnoreCase("kill")&&commandSender.isOp()){
            if(Bukkit.getPlayerExact(args[2])==null){
                commandSender.sendMessage(mes.getString("no-online"));
                return true;
            }
            if(!isInteger(args[3])){
                commandSender.sendMessage(mes.getString("no-num"));
                return true;
            }
            PlayerData playerData = plugin.getRnData().getPlayerDataMap().get(args[2]);
            switch (args[1]){
                case "add":
                    commandSender.sendMessage(mes.getString("cmd-done"));
                    plugin.getRnData().getMySqlManager().userKill(playerData, PointsUser.ADD,Integer.valueOf(args[3]),false);
                    return true;
                case "take":
                    commandSender.sendMessage(mes.getString("cmd-done"));
                    plugin.getRnData().getMySqlManager().userKill(playerData, PointsUser.TAKE,Integer.valueOf(args[3]),false);
                    return true;
                case "set":
                    commandSender.sendMessage(mes.getString("cmd-done"));
                    plugin.getRnData().getMySqlManager().userKill(playerData, PointsUser.SET,Integer.valueOf(args[3]),false);
                    return true;
                default:
                    commandSender.sendMessage(mes.getString("no-args"));
                    return true;
            }
        }
        if(args.length==4&&args[0].equalsIgnoreCase("just")&&commandSender.isOp()){
            if(Bukkit.getPlayerExact(args[2])==null){
                commandSender.sendMessage(mes.getString("no-online"));
                return true;
            }
            if(!isInteger(args[3])){
                commandSender.sendMessage(mes.getString("no-num"));
                return true;
            }
            PlayerData playerData = plugin.getRnData().getPlayerDataMap().get(args[2]);
            switch (args[1]){
                case "add":
                    commandSender.sendMessage(mes.getString("cmd-done"));
                    plugin.getRnData().getMySqlManager().userJust(playerData, PointsUser.ADD,Integer.valueOf(args[3]));
                    return true;
                case "take":
                    commandSender.sendMessage(mes.getString("cmd-done"));
                    plugin.getRnData().getMySqlManager().userJust(playerData, PointsUser.TAKE,Integer.valueOf(args[3]));
                    return true;
                case "set":
                    commandSender.sendMessage(mes.getString("cmd-done"));
                    plugin.getRnData().getMySqlManager().userJust(playerData, PointsUser.SET,Integer.valueOf(args[3]));
                    return true;
                default:
                    commandSender.sendMessage(mes.getString("no-args"));
                    return true;
            }
        }
        return false;
    }
}
