package com.meteor.redname.data;

public class PlayerData {
    String name;
    int just;
    int killer;

    public PlayerData(String name, int just, int killer) {
        this.name = name;
        this.just = just;
        this.killer = killer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getJust() {
        return just;
    }

    public void setJust(int just) {
        this.just = just;
    }

    public int getKiller() {
        return killer;
    }

    public void setKiller(int killer) {
        this.killer = killer;
    }
}
