package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class IceSkatin extends MurderGadget {

  public IceSkatin() {
    super(
        "ice_skatin",
        Material.OAK_BOAT,
        Locale.ICE_SKATIN_TRAP_NAME.build(),
        Locale.ICE_SKATIN_TRAP_LORE.build());
  }

  @Override
  public void onDropEvent(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {
    super.onDropEvent(game, event, true);
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final Boat boat = (Boat) location.getWorld().spawnEntity(location, EntityType.BOAT);
    game.getScheduler()
        .scheduleTask(
            () -> {
              final Location boatLocation = boat.getLocation();
              final Block blockUnderBoat = boatLocation.subtract(0, 1, 0).getBlock();
              if (blockUnderBoat.getType() != Material.ICE) {
                blockUnderBoat.setType(Material.ICE);
              }
            },
            5L);
  }
}
