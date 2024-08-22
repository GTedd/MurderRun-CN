package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Participant {

  String GLOW_TEAM_NAME = "glow-color-%s";
  String HIDE_NAME_TAG_TEAM_NAME = "hide-name-tag-%s";

  Map<org.bukkit.entity.Player, Team> GLOW_TEAMS = new WeakHashMap<>();
  Map<org.bukkit.entity.Player, Team> HIDE_NAME_TAG_TEAMS = new WeakHashMap<>();
  Map<UUID, WorldBorder> WORLD_BORDERS = new WeakHashMap<>();

  org.bukkit.entity.Player getInternalPlayer();

  default void disableJump(final GameScheduler scheduler, final long ticks) {
    final AttributeInstance instance = this.getAttribute(Attribute.GENERIC_JUMP_STRENGTH);
    final double before = instance.getValue();
    instance.setBaseValue(0.0);
    scheduler.scheduleTask(() -> instance.setBaseValue(before), ticks);
  }

  default void apply(final Consumer<org.bukkit.entity.Player> consumer) {
    final org.bukkit.entity.Player player = this.getInternalPlayer();
    consumer.accept(player);
  }

  default void spawnParticle(
      final Particle particle,
      final Location location,
      final int count,
      final double offSetX,
      final double offSetY,
      final double offSetZ) {
    this.apply(
        player -> player.spawnParticle(particle, location, count, offSetX, offSetY, offSetZ));
  }

  default void addPotionEffects(final PotionEffect... effects) {
    this.apply(player -> {
      for (final PotionEffect effect : effects) {
        player.addPotionEffect(effect);
      }
    });
  }

  default @NonNull Location getLocation() {
    final org.bukkit.entity.Player player = this.getInternalPlayer();
    return player.getLocation();
  }

  default @Nullable Location getDeathLocation() {
    final org.bukkit.entity.Player player = this.getInternalPlayer();
    return player.getLastDeathLocation();
  }

  default PlayerInventory getInventory() {
    final org.bukkit.entity.Player player = this.getInternalPlayer();
    return player.getInventory();
  }

  default AttributeInstance getAttribute(final Attribute attribute) {
    final org.bukkit.entity.Player player = this.getInternalPlayer();
    return requireNonNull(player.getAttribute(attribute));
  }

  void sendMessage(final Component component);

  void showTitle(final Component title, final Component subtitle);

  void showBossBar(
      final Component name,
      final float progress,
      final BossBar.Color color,
      final BossBar.Overlay overlay);

  void playSound(final Key key, final Source category, final float volume, final float pitch);

  void stopSound(final Key key);

  default void removeAllPotionEffects() {
    this.apply(player -> {
      final Collection<PotionEffect> effects = player.getActivePotionEffects();
      effects.forEach(effect -> {
        final PotionEffectType type = effect.getType();
        player.removePotionEffect(type);
      });
    });
  }

  void removeAllBossBars();

  default boolean canSeeEntity(final Entity entity, final double maxRangeSquared) {

    final Location entityLocation = entity.getLocation();
    final Location playerLocation = this.getLocation();
    final double distanceSquared = entityLocation.distanceSquared(playerLocation);
    if (distanceSquared > maxRangeSquared) {
      return false;
    }

    final World world = entity.getWorld();
    final Vector playerVector = playerLocation.toVector();
    final Vector entityVector = entityLocation.toVector();
    final Vector direction = playerVector.subtract(entityVector);
    final Vector normalizedDirection = direction.normalize();
    final RayTraceResult result =
        world.rayTraceBlocks(entityLocation, normalizedDirection, maxRangeSquared);
    if (result == null) {
      return true;
    }

    final Block block = result.getHitBlock();
    if (block == null) {
      return true;
    }

    final Material type = block.getType();
    return !type.isSolid();
  }

  default void setGlowColor(
      final ChatColor color, final Collection<? extends GamePlayer> receivers) {
    this.apply(player -> {
      final ScoreboardManager manager = requireNonNull(Bukkit.getScoreboardManager());
      final Scoreboard scoreboard = manager.getMainScoreboard();
      final UUID glowID = UUID.randomUUID();
      final String name = GLOW_TEAM_NAME.formatted(glowID);
      final Team team = scoreboard.registerNewTeam(name);
      team.setColor(color);
      team.addEntry(player.getDisplayName());
      GLOW_TEAMS.put(player, team);
      receivers.forEach(receiver -> {
        final org.bukkit.entity.Player receiverPlayer = receiver.getInternalPlayer();
        PacketToolsProvider.INSTANCE.sendGlowPacket(player, receiverPlayer);
      });
    });
  }

  default void removeGlow(final Collection<? extends GamePlayer> receivers) {
    this.apply(player -> {
      final PotionEffectType type = PotionEffectType.GLOWING;
      player.removePotionEffect(type);
      final Team team = GLOW_TEAMS.get(player);
      if (team == null) {
        return;
      }
      team.unregister();
      GLOW_TEAMS.remove(player);
      receivers.forEach(receiver -> {
        final org.bukkit.entity.Player receiverPlayer = receiver.getInternalPlayer();
        PacketToolsProvider.INSTANCE.sendRemoveGlowPacket(player, receiverPlayer);
      });
    });
  }

  default void addFakeWorldBorderEffect() {
    this.apply(player -> {
      final World world = player.getWorld();
      final UUID id = world.getUID();
      WORLD_BORDERS.computeIfAbsent(id, ignore -> {
        final WorldBorder worldBorder = world.getWorldBorder();
        final WorldBorder fakeBorder = Bukkit.createWorldBorder();
        fakeBorder.setCenter(worldBorder.getCenter());
        fakeBorder.setDamageAmount(worldBorder.getDamageAmount());
        fakeBorder.setDamageBuffer(worldBorder.getDamageBuffer());
        fakeBorder.setSize(worldBorder.getSize());
        fakeBorder.setWarningDistance(Integer.MAX_VALUE);
        fakeBorder.setWarningTime(worldBorder.getWarningTime());
        player.setWorldBorder(fakeBorder);
        return fakeBorder;
      });
    });
  }

  default void removeFakeWorldBorderEffect() {
    this.apply(player -> player.setWorldBorder(null));
  }

  default void hideNameTag() {
    this.apply(player -> {
      final ScoreboardManager manager = requireNonNull(Bukkit.getScoreboardManager());
      final Scoreboard scoreboard = manager.getMainScoreboard();
      final UUID hideID = UUID.randomUUID();
      final String name = HIDE_NAME_TAG_TEAM_NAME.formatted(hideID);
      final Team team = scoreboard.registerNewTeam(name);
      team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
      team.addEntry(player.getDisplayName());
      HIDE_NAME_TAG_TEAMS.put(player, team);
      final PotionEffectType type = PotionEffectType.GLOWING;
      final int duration = Integer.MAX_VALUE;
      player.addPotionEffect(new PotionEffect(type, duration, 0));
    });
  }

  default void showNameTag() {
    this.apply(player -> {
      final Team team = HIDE_NAME_TAG_TEAMS.get(player);
      if (team == null) {
        return;
      }
      team.unregister();
      HIDE_NAME_TAG_TEAMS.remove(player);
    });
  }

  default void teleport(final Location location) {
    this.apply(player -> player.teleport(location));
  }

  void addDeathTask(final PlayerDeathTask task);

  void removeDeathTask(final PlayerDeathTask task);

  Collection<PlayerDeathTask> getDeathTasks();

  UUID getUuid();

  boolean isAlive();

  void setAlive(final boolean alive);

  Game getGame();

  void setEntityGlowingForPlayer(final Entity entity);

  void removeEntityGlowingForPlayer(final Entity entity);

  default void setEntityGlowingForPlayer(final GamePlayer player) {
    this.apply(player::setEntityGlowingForPlayer);
  }

  default void removeEntityGlowingForPlayer(final GamePlayer player) {
    this.apply(player::removeEntityGlowingForPlayer);
  }

  @Nullable
  ArmorStand getCorpse();

  void setCorpse(@Nullable ArmorStand corpse);
}
