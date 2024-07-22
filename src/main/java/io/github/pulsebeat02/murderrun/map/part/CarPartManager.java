package io.github.pulsebeat02.murderrun.map.part;

import io.github.pulsebeat02.murderrun.MurderGame;
import io.github.pulsebeat02.murderrun.config.GameConfiguration;
import io.github.pulsebeat02.murderrun.map.MurderMap;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.Collection;
import java.util.HashSet;
import java.util.SplittableRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class CarPartManager {

  private final MurderMap map;
  private final Collection<CarPartItemStack> parts;
  private final ScheduledExecutorService service;

  public CarPartManager(final MurderMap map) {
    this.map = map;
    this.parts = new HashSet<>();
    this.service = Executors.newScheduledThreadPool(1);
  }

  public void spawnParts() {
    this.randomizeSpawnLocations();
    this.spawnParticles();
  }

  public void shutdown() {
    this.service.shutdown();
  }

  private void randomizeSpawnLocations() {
    final MurderGame game = this.map.getGame();
    final GameConfiguration configuration = game.getConfiguration();
    final SplittableRandom random = new SplittableRandom();
    final Location first = configuration.getFirstCorner();
    final Location second = configuration.getSecondCorner();
    final World world = first.getWorld();
    final int parts = configuration.getCarPartCount();
    for (int i = 0; i < parts; i++) {
      final double x = first.getX() + (second.getX() - first.getX()) * random.nextDouble();
      final double y = first.getY() + (second.getY() - first.getY()) * random.nextDouble();
      final double z = first.getZ() + (second.getZ() - first.getZ()) * random.nextDouble();
      final Location location = new Location(world, x, y, z);
      final CarPartItemStack part = new CarPartItemStack(location);
      part.spawn();
      this.parts.add(part);
    }
  }

  private void spawnParticles() {
    this.service.scheduleAtFixedRate(
        () -> this.parts.forEach(this::spawnParticleOnPart), 0, 1, TimeUnit.SECONDS);
  }

  private void spawnParticleOnPart(final CarPartItemStack stack) {
    final Location location = stack.getLocation();
    final Location clone = location.clone().add(0, 1, 0);
    final World world = clone.getWorld();
    world.spawnParticle(Particle.EFFECT, clone, 10, 0.5, 0.5, 0.5);
  }

  public MurderMap getMap() {
    return this.map;
  }

  public Collection<CarPartItemStack> getParts() {
    return this.parts;
  }
}
