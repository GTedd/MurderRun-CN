package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.checkerframework.checker.nullness.qual.KeyFor;

public final class IcePath extends KillerGadget {

  public IcePath() {
    super("ice_path", Material.ICE, Message.ICE_PATH_NAME.build(), Message.ICE_PATH_LORE.build(), GameProperties.ICE_PATH_COST);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.setIceTrail(game, player), 0, 4, 20 * 60L);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.ICE_PATH_SOUND);

    return false;
  }

  private void setIceTrail(final Game game, final GamePlayer player) {
    final Location location = player.getLocation();
    final Map<Location, Material> originalBlocks = new HashMap<>();
    for (int x = -1; x <= 1; x++) {
      for (int z = -1; z <= 1; z++) {
        final Location clone = location.clone();
        final Location blockLocation = clone.add(x, -1, z);
        final Block block = blockLocation.getBlock();
        final Material type = block.getType();
        if (!type.equals(Material.ICE)) {
          originalBlocks.put(blockLocation, type);
          block.setType(Material.ICE);
        }
      }
    }

    final Map<Location, Material> blocksToRestore = new HashMap<>(originalBlocks);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(
      () -> {
        final Collection<Entry<@KeyFor("blocksToRestore") Location, Material>> entries = blocksToRestore.entrySet();
        for (final Map.Entry<Location, Material> entry : entries) {
          final Location blockLocation = entry.getKey();
          final Block block = blockLocation.getBlock();
          final Material material = entry.getValue();
          block.setType(material);
          block.getState().update(true);
        }
      },
      3 * 20L
    );
  }
}
