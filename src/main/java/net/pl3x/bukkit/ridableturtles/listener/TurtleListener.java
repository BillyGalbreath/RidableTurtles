package net.pl3x.bukkit.ridableturtles.listener;

import net.pl3x.bukkit.ridableturtles.configuration.Lang;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.spigotmc.event.entity.EntityDismountEvent;

public class TurtleListener implements Listener {
    @EventHandler
    public void onClickTurtle(PlayerInteractAtEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // dont fire twice
        }

        Entity turtle = event.getRightClicked();
        if (turtle.getType() != EntityType.TURTLE) {
            return; // not a turtle
        }

        if (!turtle.getPassengers().isEmpty()) {
            return; // turtle already has rider
        }

        Player player = event.getPlayer();
        if (player.getVehicle() != null) {
            return; // player already riding something
        }

        if (!player.hasPermission("allow.turtle.ride")) {
            Lang.send(player, Lang.RIDE_NO_PERMISSION);
            return;
        }

        // add player as rider
        turtle.addPassenger(player);
    }

    @EventHandler
    public void onTurtleDismount(EntityDismountEvent event) {
        Entity turtle = event.getDismounted();
        if (turtle.getType() != EntityType.TURTLE) {
            return; // not a turtle
        }

        if (turtle.isDead()) {
            return; // turtle died
        }

        if (event.getEntity().getType() != EntityType.PLAYER) {
            return; // not a player
        }

        Player player = (Player) event.getEntity();
        if (player.isSneaking()) {
            return; // dismount from shift
        }

        if (player.isDead()) {
            return; // player died
        }

        // cancel dismount
        event.setCancelled(true);
    }
}
