package me.roxo.eruption;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import static com.projectkorra.projectkorra.ability.CoreAbility.getAbility;

public class Listener implements org.bukkit.event.Listener {



    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();

        if(!event.isSneaking())return;

        Eruption eruption = CoreAbility.getAbility(player, Eruption.class);


        if (eruption != null) {
            eruption.onShift();
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event){
        if((event.getAction() != Action.LEFT_CLICK_BLOCK) && event.getAction() != Action.LEFT_CLICK_AIR){return;}
        Player player = event.getPlayer();
        final BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(player);

        if (bendingPlayer == null) return;

        bendingPlayer.getBoundAbilityName();

        if (bendingPlayer == null) {
            return;
        }
        if (bendingPlayer.getBoundAbilityName().equalsIgnoreCase("Eruption")) {
            new Eruption(player);
        }

    }



}
