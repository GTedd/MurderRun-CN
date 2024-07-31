package io.github.pulsebeat02.murderrun.utils;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.LocaleParent;
import io.github.pulsebeat02.murderrun.locale.Sender;
import io.github.pulsebeat02.murderrun.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.resourcepack.sound.FXSound;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Sound;

public final class AdventureUtils {

  private static final LegacyComponentSerializer SERIALIZER = BukkitComponentSerializer.legacy();

  private AdventureUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static Component createLocationComponent(
      final LocaleParent.TriComponent<Sender, Integer, Integer, Integer> function,
      final Location location) {
    final int x = location.getBlockX();
    final int y = location.getBlockY();
    final int z = location.getBlockZ();
    return function.build(x, y, z);
  }

  public static String serializeComponentToLegacy(final Component component) {
    return SERIALIZER.serialize(component);
  }

  public static void playSoundForAllParticipants(final MurderGame game, final FXSound... keys) {
    final String key = getRandomKey(keys);
    final Key id = Key.key(key);
    final MurderPlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(
        player -> player.playSound(player.getLocation(), id, Source.MASTER, 1f, 1f));
  }

  private static String getRandomKey(final FXSound... keys) {
    final int bound = keys.length;
    final int random = RandomUtils.generateInt(bound);
    final FXSound chosen = keys[random];
    return chosen.getSoundName();
  }

  public static void playSoundForAllParticipants(final MurderGame game, final Sound... keys) {
    final Sound key = getRandomKey(keys);
    final MurderPlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(
        player -> player.playSound(player.getLocation(), key, Source.MASTER, 1f, 1f));
  }

  private static Sound getRandomKey(final Sound... keys) {
    final int bound = keys.length;
    final int random = RandomUtils.generateInt(bound);
    return keys[random];
  }

  public static void playSoundForAllMurderers(final MurderGame game, final FXSound... keys) {
    final String key = getRandomKey(keys);
    final Key id = Key.key(key);
    final MurderPlayerManager manager = game.getPlayerManager();
    manager.applyToAllDead(player -> {
      final Location location = player.getLocation();
      player.playSound(location, id, Source.MASTER, 1f, 1f);
    });
  }

  public static void playSoundForAllInnocents(final MurderGame game, final FXSound... keys) {
    final String key = getRandomKey(keys);
    final Key id = Key.key(key);
    final MurderPlayerManager manager = game.getPlayerManager();
    manager.applyToAllInnocents(innocent -> {
      final Location location = innocent.getLocation();
      innocent.playSound(location, id, Source.MASTER, 1f, 1f);
    });
  }

  public static void playSoundForAllParticipantsAtLocation(
      final MurderGame game, final Location origin, final FXSound... keys) {
    final String key = getRandomKey(keys);
    final Key id = Key.key(key);
    final MurderPlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(player -> player.playSound(origin, id, Source.MASTER, 1f, 1f));
  }

  public static void showTitleForAllParticipants(
      final MurderGame game, final Component title, final Component subtitle) {
    final MurderPlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(player -> player.showTitle(title, subtitle));
  }

  public static void showTitleForAllMurderers(
      final MurderGame game, final Component title, final Component subtitle) {
    final MurderPlayerManager manager = game.getPlayerManager();
    manager.applyToAllMurderers(murderer -> murderer.showTitle(title, subtitle));
  }

  public static void showTitleForAllInnocents(
      final MurderGame game, final Component title, final Component subtitle) {
    final MurderPlayerManager manager = game.getPlayerManager();
    manager.applyToAllInnocents(innocent -> innocent.showTitle(title, subtitle));
  }

  public static void sendMessageToAllParticipants(final MurderGame game, final Component message) {
    final MurderPlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(player -> player.sendMessage(message));
  }

  public static void showBossBarForAllParticipants(
      final MurderGame game,
      final Component name,
      final float progress,
      final BossBar.Color color,
      final BossBar.Overlay overlay) {
    final MurderPlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(player -> player.showBossBar(name, progress, color, overlay));
  }
}
