package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.Game;
import java.util.UUID;

public final class Survivor extends GamePlayer {

  private boolean canPickupCarPart;
  private boolean carPart;

  public Survivor(final Game game, final UUID uuid) {
    super(game, uuid);
    this.canPickupCarPart = true;
  }

  public void setHasCarPart(final boolean hasCarPart) {
    this.carPart = hasCarPart;
  }

  public boolean hasCarPart() {
    return this.carPart;
  }

  public void setCanPickupCarPart(final boolean canPickupCarPart) {
    this.canPickupCarPart = canPickupCarPart;
  }

  public boolean canPickupCarPart() {
    return this.canPickupCarPart;
  }
}
