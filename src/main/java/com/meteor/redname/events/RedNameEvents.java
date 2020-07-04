package com.meteor.redname.events;

import com.meteor.redname.RedName;
import com.meteor.redname.data.PlayerData;
import com.meteor.redname.data.PointsUser;
import com.meteor.redname.tools.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class RedNameEvents implements Listener {
    RedName plugin;
    public RedNameEvents(RedName plugin){
        this.plugin  = plugin;
    }
    private int[] getRan(int min,int max,int n){
        if(n>max-min+1||max<min){
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while(count < n){
            int num = (int) (Math.random()*(max-min))+min;
            boolean flag = true;
            for(int j =0;j<n;j++){
                if(num == result[j]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                result[count] = num;
                count++;
            }
        }
        return result;
    }
    @EventHandler
    void joinServer(PlayerJoinEvent joinEvent){
        String name = joinEvent.getPlayer().getName();
        Bukkit.getScheduler().runTaskAsynchronously(plugin,()->{
           plugin.getRnData().getPlayerDataMap().put(name,plugin.getRnData().getMySqlManager().getPlayerData(name));
           PlayerData playerData = plugin.getRnData().getPlayerDataMap().get(name);
           if(playerData.getKiller()>=plugin.getConfig().getInt("Setting.redname-points")){
               MessageManager messageManager = plugin.getRnData().getMessageManager();
               plugin.getServer().broadcastMessage(messageManager.getString("join-game.message").replace("@player@",name));
               Bukkit.getOnlinePlayers().forEach((player)->{player.sendTitle(messageManager.getString("join-game.title").replace("@p@",name),messageManager.getString("join-game.subtitle"));});
           }
        });
    }
    @EventHandler
    void quitServer(PlayerQuitEvent quitEvent){
        String name = quitEvent.getPlayer().getName();
        PlayerData playerData = plugin.getRnData().getPlayerDataMap().get(name);
        Bukkit.getScheduler().runTaskAsynchronously(plugin,()->{
            plugin.getRnData().getMySqlManager().saveData(playerData);
           plugin.getRnData().getPlayerDataMap().remove(name);
        });
    }
    @EventHandler
    void killPlayer(PlayerDeathEvent deathEvent){
        if(deathEvent.getEntity() instanceof Player &&deathEvent.getEntity().getKiller() instanceof Player){
            Player deathp = deathEvent.getEntity();
            Player killer = deathEvent.getEntity().getKiller();
            PlayerData deathpd = plugin.getRnData().getPlayerDataMap().get(deathp.getName());
            PlayerData killerd = plugin.getRnData().getPlayerDataMap().get(killer.getName());
            if(!deathp.getName().equalsIgnoreCase(killer.getName())){
                MessageManager messageManager = plugin.getRnData().getMessageManager();
                //如被杀者身份为红名玩家
                if(deathpd.getKiller()>=plugin.getConfig().getInt("Setting.redname-points")){
                    //掉落物品,金币逻辑代码
                    PlayerInventory playerInventory = deathp.getInventory();
                    ItemStack[] items = playerInventory.getContents();
                    int slot = 0;
                    for(int i = 9;i<items.length;i++){
                        if(items[i] !=null ){
                            slot++;
                        }
                    }
                    int[] slots = new int[slot];
                    int j =0;
                    int j1;
                    for(j1=9;j1<items.length;j1++){
                        if(items[j1]!=null){
                            slots[j++] = j1;
                        }
                    }
                    String mes = messageManager.getString("dropitem.message");
                    String Title = messageManager.getString("dropitem.title");
                    String SubTitle = messageManager.getString("dropitem.subtitle");
                    if((plugin.getConfig().getInt("Setting.drop-chance"))>=100){
                        for(j1 = 9;j1<items.length;j1++){
                            if(items[j1]!=null){
                                deathp.getWorld().dropItem(deathp.getLocation(),playerInventory.getItem(j1));
                                playerInventory.setItem(j1,null);
                                if(j1 == items.length){
                                    deathp.sendMessage(mes);
                                    deathp.sendTitle(Title,SubTitle);
                                }
                            }
                        }
                    }else{
                        int dropNum = (int) (slot * plugin.getConfig().getInt("Setting.drop-chance") / 100.0F);
                        int[] ran = getRan(0,slot,dropNum);
                        Boolean drop = false;
                        for(int temp=0;temp<ran.length;temp++){
                            deathp.getWorld().dropItem(deathp.getLocation(), playerInventory.getItem(slots[ran[temp]]));
                            playerInventory.setItem(slots[ran[temp]],null);
                            drop = true;
                        }
                        if(drop){
                            deathp.sendMessage(mes);
                            deathp.sendTitle(Title,SubTitle);
                        }
                    }
                    int takemoney = (int) (RedName.economy.getBalance(deathp.getName())*plugin.getConfig().getDouble("Setting.drop-money"));
                    RedName.economy.withdrawPlayer(deathp.getName(),takemoney);
                    deathp.sendMessage(messageManager.getString("drop-money").replace("@money@",takemoney+""));
                    int addjust = plugin.getConfig().getInt("Setting.kill-redname-points");
                    plugin.getRnData().getMySqlManager().userJust(killerd,PointsUser.ADD,addjust);
                    plugin.getRnData().getMySqlManager().userKill(deathpd,PointsUser.ADD,0,true);
                    killer.sendMessage(messageManager.getString("kill-redname").replace("@player@",deathp.getName()).replace("@just@",addjust+""));
                    return;
                }
                int justkillplayer = plugin.getConfig().getInt("Setting.just-kill-player");
                //拥有定额正义值击杀非红名玩家
                if(killerd.getJust()>justkillplayer){
                    int takejust = plugin.getConfig().getInt("Setting.just-kill-player");
                    killer.sendMessage(messageManager.getString("just-kill-noredname").replace("@just@",takejust+""));
                    plugin.getRnData().getMySqlManager().userJust(killerd,PointsUser.TAKE,takejust);
                    return;
                }
                //击杀非红名玩家逻辑代码
                int addkill = plugin.getConfig().getInt("Setting.kill-player-points");
                plugin.getRnData().getMySqlManager().userKill(killerd, PointsUser.ADD,addkill,false);
                killer.sendMessage(messageManager.getString("kill-noredname").replace("@kill@",addkill+""));
                return;
            }
        }
    }
}
