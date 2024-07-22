package io.github.pulsebeat02.murderrun.player;

import io.github.pulsebeat02.murderrun.MurderGame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

import java.util.UUID;

public final class InnocentPlayer extends GamePlayer {

  private boolean carPart;

  public InnocentPlayer(final MurderGame game, final UUID uuid) {
    super(game, uuid);
  }

  @Override
  public void onPlayerAttemptPickupPartEvent(final PlayerAttemptPickupItemEvent event) {
    this.setHasCarPart(true);
  }

  @Override
  public void onMatchStart() {
    super.onMatchStart();
    final Player player = getPlayer();
    player.setWalkSpeed(0.3f);
  }

  public boolean hasCarPart() {
    return this.carPart;
  }

  public void setHasCarPart(final boolean hasCarPart) {
    this.carPart = hasCarPart;
  }
}
