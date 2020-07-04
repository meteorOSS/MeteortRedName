package com.meteor.redname.tools;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageManager {
    YamlConfiguration yamlConfiguration;
    public MessageManager(YamlConfiguration yamlConfiguration){
        this.yamlConfiguration = yamlConfiguration;
    }
    public String getString(String path){
        if(yamlConfiguration.getString(path)==null){
            return "文本路径错误？未寻找到对应消息文本..";
        }
        String string = yamlConfiguration.getString(path).replace("@prefix@",yamlConfiguration.getString("prefix")).replace("&","§");
        return string;
    }
    public List<String> getList(String path){
        if(yamlConfiguration.getStringList(path)==null){
            return Arrays.asList("文本路径错误","未寻找到对应消息文本");
        }
        List<String> list = new ArrayList<String>();
        yamlConfiguration.getStringList(path).forEach((a)->{
            a = a.replace("@prefix@",yamlConfiguration.getString("prefix")).replace("&","§");
            list.add(a);
        });
        return list;
    }
}
