package com.meteor.redname.data;

import com.meteor.redname.RedName;
import com.meteor.redname.mysql.MySqlManager;
import com.meteor.redname.tools.MessageManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RnData {
    HashMap<String,PlayerData> playerDataMap = new HashMap<String, PlayerData>();
    MessageManager messageManager;
    MySqlManager mySqlManager;

    public RnData(RedName plugin){
        mySqlManager = new MySqlManager(plugin);
        messageManager = new MessageManager(YamlConfiguration.loadConfiguration(
                new File(plugin.getDataFolder()+"/lang.yml")
        ));
    }

    public HashMap<String, PlayerData> getPlayerDataMap() {
        return playerDataMap;
    }

    public void setPlayerDataMap(HashMap<String, PlayerData> playerDataMap) {
        this.playerDataMap = playerDataMap;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public MySqlManager getMySqlManager() {
        return mySqlManager;
    }

    public void setMySqlManager(MySqlManager mySqlManager) {
        this.mySqlManager = mySqlManager;
    }

    public void setMessageManager(MessageManager messageManager) {
        this.messageManager = messageManager;
    }
}
