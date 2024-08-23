package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Chipped extends SurvivorGadget {

  public Chipped() {
    super(
        "chipped",
        Material.GOLD_NUGGET,
        Message.CHIPPED_NAME.build(),
        Message.CHIPPED_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllLivingInnocents(
        innocent -> innocent.setEntityGlowingForPlayer(player, ChatColor.GREEN));

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.handleSurvivors(manager, player), 5 * 20L);
  }

  private void handleSurvivors(final PlayerManager manager, final Player target) {
    manager.applyToAllLivingInnocents(innocent -> innocent.removeEntityGlowingForPlayer(target));
  }
}
