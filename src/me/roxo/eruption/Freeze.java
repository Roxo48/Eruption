package me.roxo.eruption;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Freeze extends BukkitRunnable {

    private int tick = 0;
    private Player player;
    private Location loc;

    private Location loc2;
    private Location loc3;

    private boolean isFreezed;

    private Eruption eruption;

    private int time;

    public Freeze(Eruption eruption, Player player, int time) {
        this.eruption = eruption;
        this.player = player;
        this.loc = player.getLocation();
        this.isFreezed = false;
        this.loc2 = loc.add(0,150,0);
        this.time = time;
    }

    @Override
    public void run() {

        if(tick == time){
            Bukkit.getServer().getWorld(player.getWorld().getName()).createExplosion(eruption.getLocations().get(0), 2 , false ,false);
            Bukkit.getServer().getWorld(player.getWorld().getName()).createExplosion(eruption.getLocations().get(1), 2 , false ,false);
            Bukkit.getServer().getWorld(player.getWorld().getName()).createExplosion(eruption.getLocations().get(2), 2 , false ,false);
            eruption.progressShoot();
            cancel();
            return;
        }
        player.playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 1,1);

        tick++;


    }

    public boolean isFreezed(){
        return isFreezed;

    }
}

