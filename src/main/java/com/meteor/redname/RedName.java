package com.meteor.redname;

import com.meteor.redname.commands.RedNameCommands;
import com.meteor.redname.data.PlayerData;
import com.meteor.redname.data.PointsUser;
import com.meteor.redname.data.RnData;
import com.meteor.redname.events.RedNameEvents;
import com.meteor.redname.hook.PlaceHolderHook;
import com.meteor.redname.tools.MessageManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class RedName extends JavaPlugin {
    RnData rnData;
    public static RedName INSTANCE;
    public static Economy economy = null;
    public RedName(){
        INSTANCE = this;
    }
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new RedNameEvents(this),this);
        rnData = new RnData(this);
        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
            getLogger().info("已成功hook - PlaceholderApi");
            PlaceholderAPI.registerPlaceholderHook(this,new PlaceHolderHook(this));
        }
        if(getServer().getPluginManager().isPluginEnabled("Vault")){
            getLogger().info("已成功hook - vault");
            RegisteredServiceProvider<Economy> ecoapi = getServer().getServicesManager()
                    .getRegistration(Economy.class);
            economy = (Economy)ecoapi.getProvider();
        }
        getServer().getPluginCommand("MeteorRedName").setExecutor(new RedNameCommands(this));
        timeTakeKill();
        getLogger().info("红名插件已载入,meteor.");
    }

    @Override
    public void onDisable() {
        rnData.getMySqlManager().closeMysql();
        super.onDisable();
    }

    @Override
    public void saveDefaultConfig() {
        String[] ymls = {"config.yml","lang.yml"};
        for(String yml: ymls){
            File file = new File(getDataFolder()+"/"+yml);
            if(!file.exists()){
                saveResource(yml,false);
            }
        }
    }
    void timeTakeKill(){
        Bukkit.getScheduler().runTaskLater(this,()->{
            int i =getConfig().getInt("Setting.time-take-points");
            Bukkit.getOnlinePlayers().forEach((player -> {
                PlayerData playerData = rnData.getPlayerDataMap().get(player.getName());
                if(playerData.getKiller()>=i){
                    rnData.getMySqlManager().userKill(playerData, PointsUser.TAKE,i,false);
                }
            }));
            timeTakeKill();
        },getConfig().getInt("Setting.time-take-kill")*60*20);
    }
    public RnData getRnData() {
        return rnData;
    }
}
