package io.github.pulsebeat02.murderrun.player;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;

public final class Murderer extends GamePlayer {
  public Murderer(final MurderGame game, final UUID uuid) {
    super(game, uuid);
  }

  @Override
  public void onPlayerAttemptPickupPartEvent(final EntityPickupItemEvent event) {
    event.setCancelled(true);
  }

  @Override
  public void onMatchStart() {
    super.onMatchStart();
    final Player player = this.getPlayer();
    player.setWalkSpeed(0.3f);
  }
}
