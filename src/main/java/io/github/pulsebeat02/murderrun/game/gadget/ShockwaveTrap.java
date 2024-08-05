package io.github.pulsebeat02.murderrun.game.gadget;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
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
        Locale.SHOCKWAVE_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer survivor) {
    final MurderPlayerManager manager = game.getPlayerManager();
    final Location origin = survivor.getLocation();
    final World world = origin.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

    world.createExplosion(origin, 0, false, false);
    manager.applyToAllParticipants(participant -> {
      final Location location = participant.getLocation();
      if (location.distanceSquared(origin) <= SHOCKWAVE_RADIUS) {
        final Vector direction = location.toVector().subtract(origin.toVector()).normalize();
        final Vector force = direction.multiply(2);
        participant.apply(player -> player.setVelocity(force));
      }
    });
  }
}
