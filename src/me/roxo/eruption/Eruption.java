package me.roxo.eruption;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.sql.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Eruption extends EarthAbility implements AddonAbility {

    private Location location;
    private Vector direction;

    private static final double SOURCE_RANGE = 8;
    private static final double RANGE = 20;
    private static final double SPEED = 1.5;

    private Block sourceBlock;
    private List<TempBlock> tempBlocks;


    private State state;

    public Eruption(Player player) {
        super(player);
        //this.location = player.getLocation();
       // this.direction = location.getDirection();


    }

    @Override
    public void progress() {

        if (!bPlayer.canBend(this) || !player.isSneaking()){
            bPlayer.addCooldown(this);
            remove();
            return;
        }

        Block block = getLavaSourceBlock(player, getName() ,SOURCE_RANGE );
        if (block == null)return;

        Eruption eruption = getAbility(player, getClass());

        if (eruption != null){
            eruption.remove();
        }

        sourceBlock = block;
        location = block.getLocation().add(.5,.5,.5);
        tempBlocks = new LinkedList<TempBlock>();

        state = State.SOURCE;



        switch (state){
            case SOURCE:
                progressSource();

                break;
            case BIULDVOCANOS :
                progressBuild();

                break;
            case SHOOTLAVA :
                progressShoot();

                break;


        }
    }
    private void progressSource() {
        isLava(sourceBlock);

        if(sourceBlock.getLocation().distanceSquared(player.getLocation()) > SOURCE_RANGE * SOURCE_RANGE || !isLavabendable(player, sourceBlock)){
            remove();
        }


    }
    private void progressBuild() {
        Location playerLoc = player.getLocation();
        World world = playerLoc.getWorld();
        world.spawnParticle(Particle.ASH, playerLoc, 8, .5,.5,.5,0);

        if (GeneralMethods.isSolid(location.getBlock())) {
            if (TempBlock.isTempBlock(location.getBlock())){
                TempBlock tb = TempBlock.get(location.getBlock());
                if(!tempBlocks.contains(tb)) {
                    state = State.SHOOTLAVA;
                    return;
                }


            } else if (location.getBlock() != sourceBlock) {


            }

        }
        List<Block> blockList = getBlocksOFVolcano(playerLoc);

    }

    private List<Block> getBlocksOFVolcano(Location loc) {
        Location blockloc = loc.add(-12 ,0,0).getBlock().getLocation();
        Location blockloc2 = loc.add(-8 ,0,8).getBlock().getLocation();
        Location blockloc3 = loc.add(-8 ,0,-8).getBlock().getLocation();
        List<Location> a = null;
        a.add(blockloc);
        a.add(blockloc2);
        a.add(blockloc3);
        List<Location> blocks1 = null;
        for(int n = 0; n < 5; n++) {
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    blocks1.add(new Location(player.getWorld(),i,n,j));
                }
            }
        }
        List<Block> d = null;
        for(Location b : a){
            for(Location c : blocks1){
                d.add(b.add(c).getBlock());
            }
        }


        return  d;
    }

    private void progressShoot() {


    }

    public void onClick() {
        if(state == State.SOURCE){
            state = State.BIULDVOCANOS;
            direction = GeneralMethods.getDirection(location, GeneralMethods.getTargetedLocation(player, RANGE)).normalize().multiply(SPEED);

        }


    }




    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return 12000;
    }

    @Override
    public String getName() {
        return "Eruption";
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return "Roxo";
    }

    @Override
    public String getVersion() {
        return "1.18";
    }

    public void setState(State state) {
        this.state = state;
    }


}
