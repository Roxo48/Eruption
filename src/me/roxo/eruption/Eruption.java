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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Eruption extends LavaAbility implements AddonAbility {

    private Location location;
    private Vector direction;

    private static double SOURCE_RANGE = 8;
    private static double RANGE = 20;
    private static double SPEED = 1.5;

    private Block sourceBlock;
    private List<TempBlock> tempBlocks;

    private Location blockLocOne;
    private Location blockLocTwo;
    private Location blockLocThree;

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
        setFields();
         blockLocOne = player.getLocation().add(-12 ,0,0).getBlock().getLocation();
         blockLocTwo = player.getLocation().add(-8 ,0,8).getBlock().getLocation();
         blockLocThree = player.getLocation().add(-8 ,0,-8).getBlock().getLocation();
        state = State.SOURCE;

//        Eruption eruption = getAbility(player, getClass());
//
//        if (eruption != null){
//            //bPlayer.addCooldown(this);
//            eruption.remove();
//        }

        //if (bPlayer.isOnCooldown(this)) return;
        this.bPlayer.addCooldown((Ability) this);
        //this.location = player.getLocation();
        // this.direction = location.getDirection();

        this.start();
    }

    private void setFields() {
        SPEED = ConfigManager.getConfig().getInt("Eruption.SPEED");//this will amke it how many click (cloud steps)
        SOURCE_RANGE = ConfigManager.getConfig().getLong("Eruption.SOURCE_RANGE");
        RANGE = ConfigManager.getConfig().getDouble("Eruption.RANGE");

    }

    @Override
    public void progress() {

        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        }
        if (!Objects.equals(bPlayer.getBoundAbilityName(), "Eruption")) {
            this.bPlayer.addCooldown(this);
            remove();
            return;
        }

        //System.out.println(block);
        block = getLavaSourceBlock(player, getName(), SOURCE_RANGE);
        if (block == null) return;
        //TODO SET THE LAVA TICK TO 1 or 0
        if (!CoreAbility.hasAbility(player, this.getClass())) {
            return;
        }
        //if (!bPlayer.canBendIgnoreBinds(this)) {return;}
        if (bPlayer.isOnCooldown(this)) return;
        sourceBlock = block;
        location = block.getLocation().add(.5, .5, .5);
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
        player.sendMessage(ChatColor.GREEN + " Source Selected");
        if (sourceBlock.getLocation().distanceSquared(player.getLocation()) > SOURCE_RANGE * SOURCE_RANGE || !isLavabendable(player, sourceBlock)) {

            remove();
        }


    }

    private void progressBuild() {
        Location playerLoc = player.getLocation();
        World world = playerLoc.getWorld();

        List<Location> blockList = getBlocksOFVolcano(playerLoc);
        System.out.println("Size of temp " + tempBlocks.size() + "block size " + blockList.size());
        for (int i = tempBlocks.size(); i < blockList.size(); i++) {
            Block blockonvoc = blockList.get(i).getBlock();
            if (GeneralMethods.isSolid(blockonvoc)) {
                if (TempBlock.isTempBlock(blockonvoc)) {
                    TempBlock tb = TempBlock.get(blockonvoc);
                    if (!tempBlocks.contains(tb)) {
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

    private List<Location> getBlocksOFVolcano(Location loc) {
        List<Location> locationsList = new ArrayList<>();
        Location location1 = loc;
        World world = player.getWorld();
        int y = 6;
        while (y != 0) {
            if (y == 6) {
                //3x3
                //TODO make the volcano
                int[] locOfBlocks1 = {1,2,3,4,6,7,8,9};
                ArrayList<Integer> num = new ArrayList<>();
                for (int a : locOfBlocks1){num.add(a);}
                int count = 0;
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        count++;
                        if(num.contains(count)){
                            locationsList.add(new Location(world, x,y,z));
                        }
                    }
                }
            }
            if (y == 5) {
                //5x5
                int[] locOfBlocks1 = {2,3,4,6,10,11,15,16,20,22,23,24};
                ArrayList<Integer> num = new ArrayList<>();
                for (int a : locOfBlocks1){num.add(a);}
                int count = 0;
                for (int x = -2; x <= 2; x++) {
                    for (int z = -2; z <= 2; z++) {
                        count++;
                        if(num.contains(count)){
                            locationsList.add(new Location(world, x,y,z));
                        }
                    }
                }

            }
            if (y == 4) {
                //7x7
                int[] locOfBlocks1 = {2,3,4,5,6
                        ,8,14,
                        15,21,
                        22,28,
                        29,35,
                        44,45,46,47,48};
                ArrayList<Integer> num = new ArrayList<>();
                for (int a : locOfBlocks1){num.add(a);}
                int count = 0;
                for (int x = -3; x <= 3; x++) {
                    for (int z = -3; z <= 3; z++) {
                        count++;
                        if(num.contains(count)){
                            locationsList.add(new Location(world, x,y,z));
                        }
                    }
                }
            }
            if (y == 3) {
                //9x9
                int[] locOfBlocks1 = {2,3,4,5,6,7,8
                        ,10,18,
                        19,27,
                        28,36,
                        37,45,
                        46,54,
                        55,63,
                        64,72,
                        74,75,76,77,78,79,80};
                ArrayList<Integer> num = new ArrayList<>();
                for (int a : locOfBlocks1){num.add(a);}
                int count = 0;
                for (int x = -4; x <= 4; x++) {
                    for (int z = -4; z <= 4; z++) {
                        count++;
                        if(num.contains(count)){
                            locationsList.add(new Location(world, x,y,z));
                        }
                    }
                }
            }
            if (y == 2) {
                //11x11
                int[] locOfBlocks1 = {2,3,4,5,6,7,8,9,10
                        ,12,22,
                        23,33,
                        34,44,
                        45,55,
                        56,66,
                        67,77,
                        78,88,
                        89,99,
                        100,110,
                        112,113,114,115,116,117,118,119,120};
                ArrayList<Integer> num = new ArrayList<>();
                for (int a : locOfBlocks1){num.add(a);}
                int count = 0;
                for (int x = -3; x <= 3; x++) {
                    for (int z = -3; z <= 3; z++) {
                        count++;
                        if(num.contains(count)){
                            locationsList.add(new Location(world, x,y,z));
                        }
                    }
                }
            }
            if (y == 1) {
                //13x13
                int[] locOfBlocks1 = {2,3,4,5,6,7,8,9,10,11,12
                        ,14,26,
                        27,39,
                        40,52,
                        53,65,
                        66,78,
                        79,91,
                        92,104,
                        105,117,
                        118,130,
                        131,143,
                        144, 156,
                        158,159,160,161,162,163,164,165,166,167,168};
                ArrayList<Integer> num = new ArrayList<>();
                for (int a : locOfBlocks1){num.add(a);}
                int count = 0;
                for (int x = -3; x <= 3; x++) {
                    for (int z = -3; z <= 3; z++) {
                        count++;
                        if(num.contains(count)){
                            locationsList.add(new Location(world, x,y,z));
                        }

                    }
                }
            }
            y--;
        }


//        Vector vector = player.getVelocity();
//        Location blockloc = loc.add(-12 ,0,0).getBlock().getLocation();
//        Location blockloc2 = loc.add(-8 ,0,8).getBlock().getLocation();
//        Location blockloc3 = loc.add(-8 ,0,-8).getBlock().getLocation();
//        List<Location> a = new ArrayList<>();
//        a.add(blockloc);
//        a.add(blockloc2);
//        a.add(blockloc3);
//        List<Location> blocks1 = new ArrayList<>();
//        int size = 5;
//        for(int n = 0; n <= 5; n++) {
//            size--;
//            for (int x = 0; x <= size; ++x) {
//                for (int z = 0; z <= size; ++z) {
//                  //  new Location(player.getWorld(),x,n,z)
//                    blocks1.add(new Location(player.getWorld(),x,n,z));
//                }
//            }
//        }
//
//        List<Block> d = new ArrayList<>();
////        for(Location b : a){
////            for(Location c : blocks1){
////                Bukkit.broadcastMessage(c.toString() + " " + blocks1.size());
////                d.add((b.add(c)).getBlock());
////
////            }
////        }
//
//        for(Location e : blocks1){
//            d.add(e.getBlock());
//
//        }
//
//            Bukkit.broadcastMessage(d.size() + "" + blocks1.size());
//        return  d;
        return locationsList;
    }

    private void progressShoot() {
        //TODO if player left clicks then do this.
        player.sendMessage(ChatColor.RED + " You feel the molten rock heating up awaiting to rise and erupt...");

        Material lava = Material.LAVA;

        Location locationOfPlayer = GeneralMethods.getTargetedLocation(player, 10);

        Location leftVoc,middleVoc,rightVoc;
        middleVoc = blockLocOne.add(0,6,0);
        leftVoc = blockLocOne.add(0,6,0);
        rightVoc = blockLocOne.add(0,6,0);

        Vector trajectory = locationOfPlayer.toVector();


        player.sendTitle(ChatColor.DARK_RED + "Lava is Erupting", "",30,30,30);

        Location playerRightLook, playerMiddleLook, playerLeftLook;

        playerRightLook = locationOfPlayer.add(12,0,0);
        playerMiddleLook = locationOfPlayer.add(0,12,0);
        playerLeftLook = locationOfPlayer.add(0,0,12);

       List<Block> rightBlocks = GeneralMethods.getBlocksAroundPoint(playerRightLook, 3);
       List<Block> middleBlocks = GeneralMethods.getBlocksAroundPoint(playerMiddleLook, 3);
       List<Block> leftBlocks = GeneralMethods.getBlocksAroundPoint(playerLeftLook, 3);

       List<Block> allBlocks = new ArrayList<>();
       allBlocks.addAll(rightBlocks);
       allBlocks.addAll(middleBlocks);
       allBlocks.addAll(leftBlocks);

        BukkitRunnable runnable = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= 3000){
                    cancel();
                }
                Objects.requireNonNull(Bukkit.getServer().getWorld("world")).createExplosion(leftVoc, 4F, false, false);
                Objects.requireNonNull(Bukkit.getServer().getWorld("world")).createExplosion(rightVoc, 4F, false, false);
                Objects.requireNonNull(Bukkit.getServer().getWorld("world")).createExplosion(middleVoc, 4F, false, false);
                //TODO get all blocks in the curve of the pools (center)
                count++;
            }
        };
        for (int i = tempBlocks.size(); i < allBlocks.size(); i++) {
            Block blockonvoc = allBlocks.get(i);
            if (GeneralMethods.isSolid(blockonvoc)) {
                if (TempBlock.isTempBlock(blockonvoc)) {
                    TempBlock tb = TempBlock.get(blockonvoc);
                    if (!tempBlocks.contains(tb)) {
                        state = State.SHOOTLAVA;
                        return;
                    }


                } else if (blockonvoc != sourceBlock) {


                }

            }
            System.out.println("X7");
            tempBlocks.add(new TempBlock(blockonvoc, blockonvoc.getBlockData(),200));


        }

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
