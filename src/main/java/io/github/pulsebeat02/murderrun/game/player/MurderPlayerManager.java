package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.player.death.MurdererLocationManager;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathManager;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class MurderPlayerManager {

  private final MurderGame game;
  private final PlayerDeathManager deathManager;
  private final MurdererLocationManager murdererLocationManager;

  private final Map<UUID, GamePlayer> lookupMap;
  private Collection<GamePlayer> cachedDeadPlayers;
  private Collection<Murderer> cachedMurderers;
  private Collection<Innocent> cachedInnocents;

  public MurderPlayerManager(final MurderGame game) {
    this.game = game;
    this.deathManager = new PlayerDeathManager(game);
    this.murdererLocationManager = new MurdererLocationManager(game);
    this.lookupMap = new WeakHashMap<>();
  }

  public void start(final Collection<Player> murderers, final Collection<Player> participants) {
    this.assignPlayerRoles(murderers, participants);
    this.setupAllPlayers();
    this.resetCachedPlayers();
    this.murdererLocationManager.spawnParticles();
    this.deathManager.spawnParticles();
  }

  private void assignPlayerRoles(
      final Collection<Player> murderers, final Collection<Player> participants) {
    this.createMurderers(murderers);
    this.createInnocents(murderers, participants);
  }

  private void setupAllPlayers() {
    final PlayerStartConfigurator manager = new PlayerStartConfigurator(this);
    manager.configurePlayers();
  }

  public void resetCachedPlayers() {
    this.cachedMurderers = this.lookupMap.values().stream()
        .filter(player -> player instanceof Murderer)
        .map(murderer -> (Murderer) murderer)
        .collect(Collectors.toSet());
    this.cachedDeadPlayers = this.lookupMap.values().stream()
        .filter(player -> !player.isAlive())
        .collect(Collectors.toSet());
    this.cachedInnocents = this.lookupMap.values().stream()
        .filter(player -> player instanceof Innocent)
        .map(murderer -> (Innocent) murderer)
        .collect(Collectors.toSet());
  }

  public @Nullable GamePlayer getNearestKiller(final Location origin) {
    GamePlayer nearest = null;
    double min = Double.MAX_VALUE;
    final Collection<Murderer> killers = this.getMurderers();
    for (final GamePlayer killer : killers) {
      final Location location = killer.getLocation();
      final double distance = location.distanceSquared(origin);
      if (distance < min) {
        nearest = killer;
        min = distance;
      }
    }
    return nearest;
  }

  private void createMurderers(final Collection<Player> murderers) {
    for (final Player player : murderers) {
      final UUID uuid = player.getUniqueId();
      final Murderer murderer = new Murderer(this.game, uuid);
      this.lookupMap.put(uuid, murderer);
    }
  }

  private void createInnocents(
      final Collection<Player> murderers, final Collection<Player> participants) {
    final Set<UUID> uuids = this.createMurdererUuids(murderers);
    for (final Player player : participants) {
      final UUID uuid = player.getUniqueId();
      if (uuids.contains(uuid)) {
        continue;
      }
      final Innocent innocent = new Innocent(this.game, uuid);
      this.lookupMap.put(uuid, innocent);
    }
  }

  public void applyToAllParticipants(final Consumer<GamePlayer> consumer) {
    this.getParticipants().forEach(consumer);
  }

  private Set<UUID> createMurdererUuids(final Collection<Player> murderers) {
    return murderers.stream().map(Player::getUniqueId).collect(Collectors.toSet());
  }

  public Collection<GamePlayer> getParticipants() {
    return this.lookupMap.values();
  }

  public void shutdown() {
    this.resetAllPlayers();
    this.deathManager.shutdownExecutor();
    this.murdererLocationManager.shutdownExecutor();
  }

  private void resetAllPlayers() {
    final PlayerResetConfigurator manager = new PlayerResetConfigurator(this);
    manager.configure();
  }

  public void applyToAllInnocents(final Consumer<Innocent> consumer) {
    this.getInnocentPlayers().forEach(consumer);
  }

  public Collection<Innocent> getInnocentPlayers() {
    return this.cachedInnocents;
  }

  public void applyToAllMurderers(final Consumer<Murderer> consumer) {
    this.getMurderers().forEach(consumer);
  }

  public Collection<Murderer> getMurderers() {
    return this.cachedMurderers;
  }

  public void applyToAllDead(final Consumer<GamePlayer> consumer) {
    this.getDead().forEach(consumer);
  }

  public Collection<GamePlayer> getDead() {
    return this.cachedDeadPlayers;
  }

  public Optional<GamePlayer> lookupPlayer(final UUID uuid) {
    return Optional.ofNullable(this.lookupMap.get(uuid));
  }

  public Optional<GamePlayer> lookupPlayer(final Player player) {
    return this.lookupPlayer(player.getUniqueId());
  }

  public MurderGame getGame() {
    return this.game;
  }

  public PlayerDeathManager getDeathManager() {
    return this.deathManager;
  }

  public @Nullable GamePlayer removePlayer(final UUID uuid) {
    return this.lookupMap.remove(uuid);
  }
}
