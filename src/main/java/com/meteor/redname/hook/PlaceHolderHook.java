package com.meteor.redname.hook;

import com.meteor.redname.RedName;
import com.meteor.redname.data.PlayerData;
import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlaceHolderHook extends PlaceholderHook {
    private RedName plugin;
    public PlaceHolderHook(RedName plugin){
        this.plugin = plugin;
    }
    private String getRedNameText(int kill,String string){
        ConfigurationSection redname = plugin.getConfig().getConfigurationSection("papi-hook.red-name");
        for(String key : redname.getKeys(false)){
            if(kill>=redname.getInt(key+".kill")){
                return redname.getString(key+"."+string).replace("&","§b");
            }
        }
        return "";
    }
    private String getJustNameText(int just,String string){
        ConfigurationSection redname = plugin.getConfig().getConfigurationSection("papi-hook.just-name");
        for(String key : redname.getKeys(false)){
            if(just>=redname.getInt(key+".just")){
                return redname.getString(key+"."+string).replace("&","§b");
            }
        }
        return "";
    }
    @Override
    public String onPlaceholderRequest(Player p, String params) {
        PlayerData playerData = plugin.getRnData().getPlayerDataMap().get(p.getName());

        switch (params){
            case "killvaul":
                return playerData.getKiller()+"";
            case "justvaul":
                return playerData.getJust()+"";
            case "color":
                String string = getRedNameText(playerData.getKiller(),"color");
                if(!string.equalsIgnoreCase("")){
                    return string;
                }else {
                    String string1 = getJustNameText(playerData.getJust(),"color");
                    if(!string1.equalsIgnoreCase("")){
                        return string1;
                    }
                    return plugin.getConfig().getString("papi-hook.normal.color").replace("&","§b");
                }
            case "prefix":
                String string2 = getRedNameText(playerData.getKiller(),"prefix");
                if(!string2.equalsIgnoreCase("")){
                    return string2;
                }else {
                    String string1 = getJustNameText(playerData.getJust(),"prefix");
                    if(!string1.equalsIgnoreCase("")){
                        return string1;
                    }
                    return plugin.getConfig().getString("papi-hook.normal.prefix").replace("&","§b");
                }
            default:
                break;
        }
        return null;
    }
}
