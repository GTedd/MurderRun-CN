package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class MagnetMode extends SurvivorGadget {

  private static final int MAGNET_MODE_MULTIPLIER = 2;

  public MagnetMode() {
    super(
        "magnet_mode",
        Material.IRON_INGOT,
        Message.MAGNET_MODE_NAME.build(),
        Message.MAGNET_MODE_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GadgetManager gadgetManager = game.getGadgetManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final double current = gadgetManager.getActivationRange();
    gadgetManager.setActivationRange(current * MAGNET_MODE_MULTIPLIER);

    final PlayerAudience audience = gamePlayer.getAudience();
    final Component message = Message.MAGNET_MODE_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound("block.iron_trapdoor.close");
  }
}
