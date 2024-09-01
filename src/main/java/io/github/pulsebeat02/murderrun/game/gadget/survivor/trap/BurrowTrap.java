package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetSettings;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class BurrowTrap extends SurvivorTrap {

  public BurrowTrap() {
    super(
        "burrow",
        Material.DIRT,
        Message.BURROW_NAME.build(),
        Message.BURROW_LORE.build(),
        Message.BURROW_ACTIVATE.build(),
        GadgetSettings.BURROW_COST,
        new Color(49, 42, 41));
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {

    final Location location = murderer.getLocation();
    final Location clone = location.clone();
    clone.subtract(0, 50, 0);

    final GameScheduler scheduler = game.getScheduler();
    if (!(murderer instanceof final Killer killer)) {
      return;
    }

    final int duration = GadgetSettings.BURROW_DURATION;
    killer.disableJump(scheduler, duration);
    killer.disableWalkNoFOVEffects(scheduler, duration);
    killer.setForceMineBlocks(false);
    killer.teleport(clone);
    killer.setGravity(true);
    scheduler.scheduleTask(() -> this.resetState(killer, location), duration);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GadgetSettings.BURROW_SOUND);
  }

  private void resetState(final Killer killer, final Location location) {
    killer.teleport(location);
    killer.setForceMineBlocks(true);
  }
}
