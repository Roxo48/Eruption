package me.roxo.eruption;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Eruption extends LavaAbility implements AddonAbility {
    private Location location;

    private static double SOURCE_RANGE;
    private static double RANGE;

    private static long COOLDOWN;

    private static long SPEED_LAVAFLOW;

    private static int TIME_ERUPTION;

    private Block sourceBlock;
    private List<TempBlock> tempBlocks;
    private Location blockLocOne;
    private Location blockLocTwo;
    private Location blockLocThree;
    private int a;
    private State state;
    private Listener listener;
    private Permission perm;
    private Freeze freeze;
    private List<Location> locations;
    private boolean runAsh;
    int count = 0;
    private Block block;

    public Eruption(Player player) {
        super(player);
        if (!bPlayer.canBend(this)) {
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
        this.locations = new ArrayList<>();
        this.freeze = new Freeze(this, player, TIME_ERUPTION);

        state = State.SOURCE;

        runAsh = false;
         a = 0;

        Eruption eruption = getAbility(player, getClass());

        if (eruption != null){
            bPlayer.addCooldown(this);
            eruption.remove();
        }
        this.start();
    }

    public void effect(final Location loc) {
        ParticleEffect.ASH.display(loc, 35, 0, .4, 0);
    }

    private void setFields() {
        SOURCE_RANGE = ConfigManager.getConfig().getDouble("Eruption.SOURCE_RANGE");
        RANGE = ConfigManager.getConfig().getDouble("Eruption.RANGE_LAVA");
        TIME_ERUPTION = ConfigManager.getConfig().getInt("Eruption.TIME_ERUPTION");
        COOLDOWN = ConfigManager.getConfig().getLong("Eruption.COOLDOWN");
        SPEED_LAVAFLOW = ConfigManager.getConfig().getLong("Eruption.SPEED_LAVAFLOW");
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

        block = getLavaSourceBlock(player, getName(), SOURCE_RANGE);
        if (block == null) return;

        if (!CoreAbility.hasAbility(player, this.getClass())) {return;}
        if (bPlayer.isOnCooldown(this)){ remove(); return;}

        sourceBlock = block;
        location = player.getLocation();
        tempBlocks = new LinkedList<>();

        switch (state) {
            case SOURCE -> {

                progressSource();
            }
            case BIULDVOCANOS -> {
                blindPlayers();
                progressBuild();
            }
            case SHOOTLAVA -> {
                runAsh = true;
                if(count == 0){
                    player.sendMessage(ChatColor.RED + " You feel the molten rock heating up awaiting to rise and erupt...");
                    freeze.runTaskTimer(this.getElement().getPlugin(),0,20 );
                }
                count++;
            }
        }
    }

    private void progressSource() {
        effect(sourceBlock.getLocation());
        Location location1 = sourceBlock.getLocation();
        for(int i = -1; i < 1; i++){
            for(int j = -1; j < 1; j++){
                Location location2 = location1.clone().add(i,0,j);
                if(location2.getBlock().getType() != Material.LAVA){
                    remove();
                }
            }
        }
        if (sourceBlock.getLocation().distanceSquared(player.getLocation()) > SOURCE_RANGE * SOURCE_RANGE || !isLavabendable(player, sourceBlock)) {
            remove();
        }


    }
    //
    // rpGetPlayerDirection - Convert Player's Yaw into a human readable direction out of 16 possible. (Copied)
    //
    public int rpGetPlayerDirection(Player playerSelf){
        int dir = 0;
        float y = playerSelf.getLocation().getYaw();
        if( y < 0 ){y += 360;}
        y %= 360;
        int i = (int)((y+8) / 22.5);
        if(i == 0){dir = 0;}
        //west
        else if(i == 1){dir = 0;}
        else if(i == 2){dir = 0;}
        //north
        else if(i == 3){dir = 1;}
        else if(i == 4){dir = 1;}
        else if(i == 5){dir = 1;}
        else if(i == 6){dir = 1;}
        //east
        else if(i == 7){dir = 2;}
        else if(i == 8){dir = 2;}
        else if(i == 9){dir = 2;}
        else if(i == 10){dir = 2;}
        //south
        else if(i == 11){dir = 3;}
        else if(i == 12){dir = 3;}
        else if(i == 13){dir = 3;}
        else if(i == 14){dir = 3;}

        else if(i == 15){dir = 0;}
        else {dir = 0;}
        return dir;
    }

    private void progressBuild() {
         player.getEyeLocation().getYaw();

         if(rpGetPlayerDirection(player) == 1){//west 0
             blockLocOne = location.clone().add(8,0,0);
             blockLocTwo = location.clone().add(10 ,0,10);
             blockLocThree = location.clone().add(10 ,0,-10);
         }else if(rpGetPlayerDirection(player) == 2){//north 1
             blockLocOne = location.clone().add(0,0,8);
             blockLocTwo = location.clone().add(10 ,0,10);
             blockLocThree = location.clone().add(-10 ,0,10);
         }else if (rpGetPlayerDirection(player) == 3){//east 2
             blockLocOne = location.clone().add(-8,0,0);
             blockLocTwo = location.clone().add(-10 ,0,10);
             blockLocThree = location.clone().add(-10 ,0,-10);
         }else if(rpGetPlayerDirection(player) == 0){//south 3
             blockLocOne = location.clone().add(0,0,-8);
             blockLocTwo = location.clone().add(10 ,0,-10);
             blockLocThree = location.clone().add(-10 ,0,-10);
         }
         locations.add(blockLocOne);
         locations.add(blockLocTwo);
         locations.add(blockLocThree);
         List<Block> blockListOne = new ArrayList<>(getBlocksOFVolcano(blockLocOne));
         List<Block> blockListTwo = new ArrayList<>(getBlocksOFVolcano(blockLocTwo));
         List<Block> blockListThree = new ArrayList<>(getBlocksOFVolcano(blockLocThree));
         List<Block> blockList = new ArrayList<>();
         blockList.addAll(blockListOne);
         blockList.addAll(blockListTwo);
         blockList.addAll(blockListThree);
         playEarthbendingSound(player.getLocation());
            for (int i = tempBlocks.size(); i < blockList.size(); i++) {
                Block blockonvoc = blockList.get(i);
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
            TempBlock tempBlock = new TempBlock(blockonvoc, Material.BROWN_CONCRETE);
            tempBlocks.add(tempBlock);
            tempBlock.setRevertTime(10000);
        }
    }

    private List<Block> getBlocksOFVolcano(Location loc) {
        List<Block> blockList = new ArrayList<>();
        Location pos;
        for(int i = -3; i <= 3; i++) {
            for(int j = -3; j <= 3; j++) {
                pos = loc.clone().add(i, 0, j);
                int diff = Math.abs(i) + Math.abs(j);
                switch(diff) {
                    case 3:
                        blockList.add(pos.getBlock());
                        break;
                    case 2:
                        blockList.add(pos.getBlock());
                        blockList.add((pos.add(0, 1, 0).getBlock()));
                        blockList.add((pos.add(0, -1, 0).getBlock()));
                        break;
                    case 1:
                        blockList.add(pos.getBlock());
                        blockList.add(pos.add(0, 1, 0).getBlock());
                        blockList.add(pos.add(0, 1, 0).getBlock());
                        blockList.add(pos.add(0, -2, 0).getBlock());
                        break;
                    case 0:
                        blockList.add(pos.getBlock());
                        blockList.add(pos.add(0, 1, 0).getBlock());
                        blockList.add(pos.add(0, 1, 0).getBlock());
                        blockList.add(pos.add(0, -2, 0).getBlock());
                        blockList.add(pos.add(0, -3, 0).getBlock());
                        break;
                    default:
                        break;
                }
            }
        }
        return blockList;
    }

    public void progressShoot() {
        Location locationOfPlayer = GeneralMethods.getTargetedLocation(player, RANGE);

        Location middleOfMiddle = locationOfPlayer.clone().add(0,20,0);

        Location middleOfRight = locationOfPlayer.clone().add(0,20,0);

        Location middleOfLeft = locationOfPlayer.clone().add(0,20,0);

        List<Location> locations = getLocationBezier(blockLocOne.clone().add(0,3,0), middleOfMiddle ,locationOfPlayer,100);

        List<Location> locations2 = getLocationBezier(blockLocTwo.clone().add(0,3,0), middleOfRight ,locationOfPlayer,100);

        List<Location> locations3 = getLocationBezier(blockLocThree.clone().add(0,3,0), middleOfLeft ,locationOfPlayer,100);

              BukkitRunnable br = new BukkitRunnable() {
                  @Override
                  public void run() {
                      if(a >= 100){
                          placeLavaPool(locationOfPlayer);
                          cancel();
                      }
                      Location point = locations.get(a);
                      TempBlock tempBlock = new TempBlock(point.getBlock(), Material.LAVA);
                      tempBlock.setRevertTime(5000);

                      Location point2 = locations2.get(a);
                      TempBlock tempBlock2 = new TempBlock(point2.getBlock(), Material.LAVA);
                      tempBlock2.setRevertTime(5000);

                      Location point3 = locations3.get(a);
                      TempBlock tempBlock3 = new TempBlock(point3.getBlock(), Material.LAVA);
                      tempBlock3.setRevertTime(5000);
                      a++;
                  }
              };
              br.runTaskTimer(ProjectKorra.plugin, SPEED_LAVAFLOW,0);
        burnPlayers(locationOfPlayer);
        }

    public List<Location> getLocationBezier(Location p0,Location p1,Location p2,float t){
        List<Location> points = new ArrayList<>();
        for(int i = 0; i <= t; i ++){
            float a = i / t;
            points.add(bezierPoint(a,p0,p1,p2));
        }
        return points;
    }

        public void placeLavaPool(Location location){
        Location pos;
        List<Block> blockList = new ArrayList<>();
        for(int i = -3; i <= 3; i++){
            for(int j = -3; j <= 3; j++){
                pos = location.clone().add(i, 0, j);
                int diff = Math.abs(i) + Math.abs(j);
                switch(diff) {
                    case 3:
                        blockList.add(pos.getBlock());
                        break;
                    case 2:
                        blockList.add(pos.getBlock());
                        break;
                    case 1:
                        blockList.add(pos.getBlock());
                        break;
                    case 0:
                        blockList.add(pos.getBlock());
                        break;
                    default:
                        break;
                }
            }
        }
        for(Block block1 : blockList){
            TempBlock clearBlock1 = new TempBlock(block1, Material.LAVA);
            clearBlock1.setRevertTime(6500);
        }
            this.bPlayer.addCooldown(this);
            remove();
        }

    public void burnPlayers(Location location){
        List<Entity> players = GeneralMethods.getEntitiesAroundPoint(location, 20);
        for(Entity player1 : players){
            if(player1 instanceof Player player2){
                if(player2.getName().equals(player.getName())){
                    continue;
                }
            }
            player1.setFireTicks(500);
        }
    }

     public void blindPlayers(){
         Location location1 = player.getLocation().clone().add(0,18,0);
         BukkitRunnable bukkitRunnable = new BukkitRunnable() {
             @Override
             public void run() {
                 if(runAsh){cancel();}

                 ParticleEffect.CAMPFIRE_COSY_SMOKE.display(blockLocOne.clone().add(0,3,0), 10, 1.5,5,1.5);
                 ParticleEffect.CAMPFIRE_COSY_SMOKE.display(blockLocTwo.clone().add(0,3,0), 10, 1.5,5,1.5);
                 ParticleEffect.CAMPFIRE_COSY_SMOKE.display(blockLocThree.clone().add(0,3,0), 10, 1.5,5,1.5);
             }
         };
         bukkitRunnable.runTaskTimer(ProjectKorra.plugin,0, 20);
     }

    public Location bezierPoint(float t, Location p0, Location p1, Location p2){
       // pFinal[0] = Math.pow(1 - t, 2) * p0[0] + (1-t) * 2 * t * p1[0] + t * t * p2[0];
       // pFinal[1] = Math.pow(1 - t, 2) * p0[1] + (1-t) * 2 * t * p1[1] + t * t * p2[1];
        return p0.clone().multiply((1-t)*(1-t)).add(p1.clone().multiply((1-t) * 2 * t)).add(p2.clone().multiply(t*t));
    }

    public void onShift() {
        if(state == State.SOURCE){
            state = State.BIULDVOCANOS;
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
        return COOLDOWN;
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
        listener = new Listener();
        ProjectKorra.plugin.getServer().getPluginManager().registerEvents(listener, ProjectKorra.plugin);
        //perm.setDefault(PermissionDefault.OP);
        final FileConfiguration config = ConfigManager.defaultConfig.get();
        config.addDefault("Eruption.SOURCE_RANGE",(Object) 8);
        config.addDefault("Eruption.RANGE_LAVA",(Object) 15);
        config.addDefault("Eruption.COOLDOWN",(Object) 12000);
        config.addDefault("Eruption.TIME_ERUPTION", (Object) 5);
        config.addDefault("Eruption.SPEED_LAVAFLOW", (Object) 4);
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
                 "\"Erupt a storm of lava by forming multiple Geysers to create lava pools\"";
    }

    @Override
    public String getAuthor() {
        return "Roxo";
    }

    @Override
    public String getVersion() {
        return "1.17";
    }

    @Override
    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
}