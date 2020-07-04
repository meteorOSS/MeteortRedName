package com.meteor.redname.mysql;

public enum MySqlCommand {
    //sql命令枚举
    UPDATE("update redname set KILLER = ?,JUST = ? where player=?"),
    ADD_DATA("insert into redname (`player`,`KILLER`,`JUST`)VALUES (?,?,?)"),
    SELECT_DATA("select * from redname where player = ?"),
    CREATE_TABLE("create table if not exists `redname` (`player` varchar(30),`KILLER` int,`JUST` int,primary key (`player`))");
    private String command;
    MySqlCommand(String command){
        this.command = command;
    }
    public String getCommand(){
        return this.command;
    }

}
