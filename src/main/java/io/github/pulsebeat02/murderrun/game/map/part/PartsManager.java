package io.github.pulsebeat02.murderrun.game.map.part;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import java.util.Collection;
import java.util.HashMap;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PartsManager {

  private final Map map;
  private final java.util.Map<String, CarPart> parts;

  public PartsManager(final Map map) {
    this.map = map;
    this.parts = new HashMap<>();
  }

  public void spawnParts() {
    this.randomizeSpawnLocations();
    this.spawnParticles();
  }

  private void randomizeSpawnLocations() {
    final Game game = this.map.getGame();
    final GameSettings configuration = game.getSettings();
    final Arena arena = requireNonNull(configuration.getArena());
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final World world = requireNonNull(first.getWorld());
    final int parts = configuration.getCarPartCount();
    for (int i = 0; i < parts; i++) {
      final double[] coords = MapUtils.generateFriendlyRandomXZ(first, second);
      final Location temp = new Location(world, coords[0], 320, coords[1]);
      final Block highest = world.getHighestBlockAt(temp);
      final Location block = highest.getLocation();
      final Location location = block.add(0, 1, 0);
      final CarPart part = new CarPart(location);
      final String id = part.getUuid();
      part.spawn();
      this.parts.put(id, part);
    }
  }

  private void spawnParticles() {
    final Game game = this.map.getGame();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(this::spawnParticleTask, 0, 20);
  }

  private void spawnParticleTask() {
    final Collection<CarPart> parts = this.parts.values();
    for (final CarPart stack : parts) {
      if (!stack.isPickedUp()) {
        this.spawnParticleOnPart(stack);
      }
    }
  }

  private void spawnParticleOnPart(final CarPart stack) {
    final Location location = stack.getLocation();
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.DUST, location, 40, 0.2, 1, 0.2, new DustOptions(Color.YELLOW, 1));
  }

  public Map getMap() {
    return this.map;
  }

  public java.util.Map<String, CarPart> getParts() {
    return this.parts;
  }

  public void removeCarPart(final CarPart stack) {
    final String uuid = stack.getUuid();
    this.parts.remove(uuid);
  }

  public @Nullable CarPart getCarPartItemStack(final ItemStack stack) {
    final String uuid =
        PDCUtils.getPersistentDataAttribute(stack, Keys.CAR_PART_UUID, PersistentDataType.STRING);
    return uuid == null ? null : this.parts.get(uuid);
  }
}
