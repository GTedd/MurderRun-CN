package io.github.pulsebeat02.murderrun.game.player.death;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import java.awt.*;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public final class KillerLocationTracker {

  private final Game game;
  private final ScheduledExecutorService service;

  public KillerLocationTracker(final Game game) {
    this.game = game;
    this.service = Executors.newScheduledThreadPool(1);
  }

  public void spawnParticles() {
    final PlayerManager manager = this.game.getPlayerManager();

    this.service.scheduleAtFixedRate(
        () -> manager.applyToAllMurderers(this::spawnParticlesWhenClose), 0, 1, TimeUnit.SECONDS);
  }

  private void spawnParticlesWhenClose(final GamePlayer murdererPlayer) {
    final Location murdererLocation = murdererPlayer.getLocation();
    final PlayerManager manager = this.game.getPlayerManager();
    final Collection<Survivor> survivors = manager.getInnocentPlayers();
    for (final Survivor survivor : survivors) {
      final Location location = survivor.getLocation();
      if (location.distanceSquared(murdererLocation) > 16) {
        continue;
      }
      final Location clone = location.clone().add(0, 1, 0);
      final World world = requireNonNull(clone.getWorld());
      world.spawnParticle(Particle.DUST, clone, 10, 1, 1, 1, Color.WHITE);
    }
  }

  public Game getGame() {
    return this.game;
  }

  public ScheduledExecutorService getService() {
    return this.service;
  }

  public void shutdownExecutor() {
    this.service.shutdown();
  }
}
