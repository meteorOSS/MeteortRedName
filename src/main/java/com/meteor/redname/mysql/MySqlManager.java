package com.meteor.redname.mysql;

import com.meteor.redname.RedName;
import com.meteor.redname.data.PlayerData;
import com.meteor.redname.data.PointsType;
import com.meteor.redname.data.PointsUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;

public class MySqlManager {
    Connection connection;
    RedName plugin;
    public MySqlManager(RedName plugin){
        this.plugin = plugin;
        try {
            connection = DriverManager.getConnection(plugin.getConfig().getString("mysql.url"),
                    plugin.getConfig().getString("mysql.user"),plugin.getConfig().getString("mysql.password"));
            PreparedStatement ps = connection.prepareStatement(MySqlCommand.CREATE_TABLE.getCommand());
            ps.execute();
            plugin.getLogger().info("已成功连接数据库...");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private void doCommand(PreparedStatement ps){
        try {
            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
    public void closeMysql(){
        try {
            connection.close();
            plugin.getLogger().info("已断开数据库连接...");
        } catch (SQLException throwables) {
            plugin.getLogger().info("数据库连接断开出现错误,请尝试手动关闭...");
        }
    }
    //获取PlayerData数据
    public boolean isExistPlayerData(String player){
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(MySqlCommand.SELECT_DATA.getCommand());
            ps.setString(1,player);
            resultSet = ps.executeQuery();
            if(resultSet.next()){
                return true;
            }else {
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                ps.close();
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }
    public PlayerData getPlayerData(String player){
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
        if(!isExistPlayerData(player)){
                ps = connection.prepareStatement(MySqlCommand.ADD_DATA.getCommand());
                ps.setString(1,player);
                ps.setInt(2,0);
                ps.setInt(3,0);
                doCommand(ps);
            }
        ps = connection.prepareStatement(MySqlCommand.SELECT_DATA.getCommand());
        ps.setString(1,player);
        resultSet = ps.executeQuery();
        if(resultSet.next()){
            return new PlayerData(player,resultSet.getInt(3),resultSet.getInt(2));
        }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return null;
    }
    //保存点数数据.
    public void saveData(PlayerData playerData){
        try {
            PreparedStatement ps = connection.prepareStatement(MySqlCommand.UPDATE.getCommand());
            ps.setInt(1,playerData.getKiller());
            ps.setInt(2,playerData.getJust());
            ps.setString(3,playerData.getName());
            doCommand(ps);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    //操作点数
    public void userJust(PlayerData playerData,PointsUser pointsUser,int amount){
        int old = playerData.getJust();
        switch (pointsUser){
            case ADD:
                playerData.setJust(old+amount);
                return;
            case SET:
                playerData.setJust(amount);
                return;
            case TAKE:
                playerData.setJust(old-amount);
                return;
            default:
                return;
        }
    }
    public void userKill(PlayerData playerData,PointsUser pointsUser,int amount,boolean bool){
        int old = playerData.getKiller();
        switch (pointsUser){
            case ADD:
                playerData.setKiller(old+amount);
                break;
            case TAKE:
                playerData.setKiller(old-amount);
                break;
            case SET:
                playerData.setKiller(amount);
                break;
            default:
                return;
        }
        if(bool){
            if(playerData.getKiller()>=RedName.INSTANCE.getConfig().getInt("Setting.jail-points")){
                Player player = Bukkit.getPlayerExact(playerData.getName());
                if(player!=null){
                    Bukkit.getScheduler().runTask(RedName.INSTANCE,()->{
                        RedName.INSTANCE.getConfig().getStringList("Setting.jail-cmd").forEach((cmd)->{
                            cmd = cmd.replace("@p@",player.getName());
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),cmd);
                        });
                        player.sendMessage(RedName.INSTANCE.getRnData().getMessageManager().getString(""));
                    });
                }
            }
        }
    }

}
