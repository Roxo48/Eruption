package me.roxo.eruption;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.Permission;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Eruption extends LavaAbility implements AddonAbility {

    private Location location;
    private Vector direction;

    private static  double SOURCE_RANGE = 8;
    private static  double RANGE = 20;
    private static double SPEED = 1.5;

    private Block sourceBlock;
    private List<TempBlock> tempBlocks;


    private State state;
    private Listener listener;
    private Permission perm;

    private Block block;

    public Eruption(Player player) {
        super(player);
        if (!bPlayer.canBend((CoreAbility) this)) {
            remove();
            return;
        }
        if (!player.isOnline()) {
            remove();
            return;
        }
        if (CoreAbility.hasAbility(player, this.getClass())) {
            return;
        }
        if (!bPlayer.canBendIgnoreBinds(this)) {
            return;
        }
        if (bPlayer.isOnCooldown(this)) {
            remove();
            return;
        }
        state = State.SOURCE;

//        Eruption eruption = getAbility(player, getClass());
//
//        if (eruption != null){
//            //bPlayer.addCooldown(this);
//            eruption.remove();
//        }

        //if (bPlayer.isOnCooldown(this)) return;
        this.bPlayer.addCooldown((Ability)this);
        //this.location = player.getLocation();
       // this.direction = location.getDirection();

        this.start();
    }
    private void setFields() {
        this.SPEED = ConfigManager.getConfig().getInt("Eruption.SPEED");//this will amke it how many click (cloud steps)
        this.SOURCE_RANGE = ConfigManager.getConfig().getLong("Eruption.SOURCE_RANGE");
        this.RANGE = ConfigManager.getConfig().getDouble("Eruption.RANGE");

    }

    @Override
    public void progress() {

        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        }
        if(!Objects.equals(bPlayer.getBoundAbilityName(), "Eruption")){
            this.bPlayer.addCooldown(this);
            remove();
            return;
        }

        //System.out.println(block);
        block = getLavaSourceBlock(player, getName() ,SOURCE_RANGE );
        if (block == null)return;
        //TODO SET THE LAVA TICK TO 1 or 0
        if (!CoreAbility.hasAbility(player, this.getClass())) {return;}
        //if (!bPlayer.canBendIgnoreBinds(this)) {return;}
        if (bPlayer.isOnCooldown(this)) return;
        sourceBlock = block;
        location = block.getLocation().add(.5,.5,.5);
        tempBlocks = new LinkedList<TempBlock>();



        switch (state) {
            case SOURCE -> {

                progressSource();
            }
            case BIULDVOCANOS -> {

                progressBuild();
            }
            case SHOOTLAVA -> {

                progressShoot();
            }
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
       // world.spawnParticle(Particle.ASH, playerLoc, 8, .5,.5,.5,0);

        System.out.println("X6");
        List<Block> blockList = getBlocksOFVolcano(playerLoc);
        System.out.println("Size of temp " + tempBlocks.size() + "block size " + blockList.size());
        for (int i = tempBlocks.size(); i < blockList.size(); i++){

            Block blockonvoc = blockList.get(i);

            if (GeneralMethods.isSolid(blockonvoc)) {
                if (TempBlock.isTempBlock(blockonvoc)){
                    TempBlock tb = TempBlock.get(blockonvoc);
                    if(!tempBlocks.contains(tb)) {
                        state = State.SHOOTLAVA;
                        return;
                    }


                } else if (blockonvoc != sourceBlock) {


                }

            }
            System.out.println("X7");
            tempBlocks.add(new TempBlock(blockonvoc, Material.BROWN_CONCRETE));


        }



//        for(Block block : blockList){
//            BlockData a = block.getBlockData();
//            BlockState b = block.getState();
//            TempBlock tempBlock = new TempBlock(block, a, 100);
//            tempBlock.setType(Material.BROWN_CONCRETE);
//            //block.getLocation().getBlock().setType(Material.BROWN_CONCRETE);
//        }

    }

    private List<Block> getBlocksOFVolcano(Location loc) {
        Vector vector = player.getVelocity();
        Location blockloc = loc.add(-12 ,0,0).getBlock().getLocation();
        Location blockloc2 = loc.add(-8 ,0,8).getBlock().getLocation();
        Location blockloc3 = loc.add(-8 ,0,-8).getBlock().getLocation();
        List<Location> a = new ArrayList<>();
        a.add(blockloc);
        a.add(blockloc2);
        a.add(blockloc3);
        List<Location> blocks1 = new ArrayList<>();
        int size = 5;
        for(int n = 0; n <= 5; n++) {
            size--;
            for (int x = 0; x <= size; ++x) {
                for (int z = 0; z <= size; ++z) {
                  //  new Location(player.getWorld(),x,n,z)
                    blocks1.add(new Location(player.getWorld(),x,n,z));
                }
            }
        }

        List<Block> d = new ArrayList<>();
//        for(Location b : a){
//            for(Location c : blocks1){
//                Bukkit.broadcastMessage(c.toString() + " " + blocks1.size());
//                d.add((b.add(c)).getBlock());
//
//            }
//        }

        for(Location e : blocks1){
            d.add(e.getBlock());

        }

            Bukkit.broadcastMessage(d.size() + "" + blocks1.size());
        return  d;
    }

    private void progressShoot() {


    }

    public void onClick() {
        System.out.println("x12");
        Bukkit.broadcastMessage(state.toString());
        if(state == State.SOURCE){
            System.out.println("x");
            state = State.BIULDVOCANOS;
            //direction = GeneralMethods.getDirection(location, GeneralMethods.getTargetedLocation(player, RANGE)).normalize().multiply(SPEED);

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
        return location;
    }


    @Override
    public void load() {
        perm = new Permission("bending.ability.eruption");
        ProjectKorra.plugin.getServer().getPluginManager().addPermission(perm);
        listener = new Listener(this);
        ProjectKorra.plugin.getServer().getPluginManager().registerEvents(listener, ProjectKorra.plugin);
        //perm.setDefault(PermissionDefault.OP);
        final FileConfiguration config = ConfigManager.defaultConfig.get();
        config.addDefault("Eruption.SPEED",(Object) 1.5);
        config.addDefault("Eruption.SOURCE_RANGE",(Object) 8);
        config.addDefault("Eruption.RANGE",(Object) 20);
        config.addDefault("Eruption.COOLDOWN",(Object) 1200);
        ConfigManager.defaultConfig.save();


    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(listener);
        ProjectKorra.plugin.getServer().getPluginManager().removePermission(perm);


        
    }
    @Override
    public String getDescription(){
        return "\"Elements Of The Avatar Addons:\"\n" +
                ChatColor.RED +  "\"Erupt a storm of lava by forming multiple Geysers to create lava pools\"";
    }

    @Override
    public String getAuthor() {
        return "Roxo";
    }

    @Override
    public String getVersion() {
        return "1.18";
    }



}
