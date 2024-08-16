package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Participant;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

public final class ShockwaveTrap extends SurvivorTrap {

  private static final double SHOCKWAVE_RADIUS = 100f;

  public ShockwaveTrap() {
    super(
        "shockwave_trap",
        Material.TNT,
        Locale.SHOCKWAVE_TRAP_NAME.build(),
        Locale.SHOCKWAVE_TRAP_LORE.build(),
        Locale.SHOCKWAVE_TRAP_ACTIVATE.build(),
        32,
        new Color(255, 215, 0));
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer survivor) {
    final PlayerManager manager = game.getPlayerManager();
    final Location origin = survivor.getLocation();
    final World world = requireNonNull(origin.getWorld());
    world.createExplosion(origin, 0, false, false);
    manager.applyToAllParticipants(participant -> this.applyShockwave(participant, origin));
  }

  private void applyShockwave(final Participant participant, final Location origin) {
    final Location location = participant.getLocation();
    if (location.distanceSquared(origin) <= SHOCKWAVE_RADIUS) {
      final Vector direction = location.toVector().subtract(origin.toVector()).normalize();
      final Vector force = direction.multiply(2);
      participant.apply(player -> player.setVelocity(force));
    }
  }
}