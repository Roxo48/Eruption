package me.roxo.eruption;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import static com.projectkorra.projectkorra.ability.CoreAbility.getAbility;

public class Listener implements org.bukkit.event.Listener {

    private  final Eruption eruption;

    public Listener(Eruption eruption) {
        this.eruption = eruption;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();

        if(!event.isSneaking())return;

        Eruption eruption = CoreAbility.getAbility(player, Eruption.class);

        System.out.println("click");

        if (eruption != null) {
            System.out.println("click WORKS");
            eruption.onClick();
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
            System.out.println("shift2");
            new Eruption(player);
        }










    }



}
