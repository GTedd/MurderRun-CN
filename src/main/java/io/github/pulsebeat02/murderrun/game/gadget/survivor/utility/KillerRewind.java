package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.MovementManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class KillerRewind extends SurvivorGadget {

  private static final int KILLER_REWIND_COOLDOWN = 3000;
  private static final String KILLER_REWIND_SOUND = "entity.shulker.teleport";

  public KillerRewind() {
    super(
        "killer_rewind",
        Material.LAPIS_BLOCK,
        Message.MURDERER_REWIND_NAME.build(),
        Message.MURDERER_REWIND_LORE.build(),
        16);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final Location location = player.getLocation();
    final PlayerManager manager = game.getPlayerManager();
    final MovementManager movementManager = manager.getMovementManager();
    final GamePlayer closest = manager.getNearestKiller(location);
    if (closest == null) {
      return true;
    }

    if (!(closest instanceof final Killer killer)) {
      return remove;
    }

    final long current = System.currentTimeMillis();
    final long last = killer.getKillerRewindCooldown();
    if (last == 0) {
      killer.setKillerRewindCooldown(current);
      this.handleRewind(game, movementManager, killer, item, current);
      return true;
    }

    final long difference = current - last;
    if (difference < KILLER_REWIND_COOLDOWN) {
      super.onGadgetDrop(game, player, item, false);
      return true;
    }

    this.handleRewind(game, movementManager, killer, item, current);

    return false;
  }

  private void handleRewind(
      final Game game,
      final MovementManager movementManager,
      final Killer killer,
      final Item item,
      final long current) {

    killer.setKillerRewindCooldown(current);
    killer.setFallDistance(0.0f);

    final boolean successful = movementManager.handleRewind(killer);
    super.onGadgetDrop(game, killer, item, successful);

    final PlayerAudience audience = killer.getAudience();
    audience.playSound(KILLER_REWIND_SOUND);
  }
}
