package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.lobby.GameLobby;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.resourcepack.server.PackHostingDaemon;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public final class MurderGameManager {

  private final MurderRun plugin;
  private final MurderGame game;
  private final Collection<Player> murderers;
  private final Collection<Player> participants;
  private final GameSettings settings;

  public MurderGameManager(final MurderRun plugin) {
    this.plugin = plugin;
    this.game = new MurderGame(plugin);
    this.murderers = new HashSet<>();
    this.participants = new HashSet<>();
    this.settings = new GameSettings();
  }

  public void addParticipantToLobby(final Player player) {
    this.participants.add(player);
    this.teleportPlayerToLobby(player);
    this.addCurrency(player);
    this.setResourcepack(player);
  }

  private void setResourcepack(final Player player) {
    try {
      final PackHostingDaemon daemon = this.plugin.getDaemon();
      final String url = daemon.getUrl();
      final URI uri = new URI(url);
      final String hash = daemon.getHash();
      final UUID id = UUID.randomUUID();
      final AudienceHandler handler = this.plugin.getAudience();
      final BukkitAudiences audiences = handler.retrieve();
      final Audience audience = audiences.player(player);
      final Component message = Locale.RESOURCEPACK_PROMPT.build();
      final ResourcePackInfo info = ResourcePackInfo.resourcePackInfo(id, uri, hash);
      final ResourcePackRequest request =
          ResourcePackRequest.resourcePackRequest()
              .packs(info)
              .required(true)
              .prompt(message)
              .asResourcePackRequest();
      audience.sendResourcePacks(request);
    } catch (final URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private void teleportPlayerToLobby(final Player player) {
    final GameLobby lobby = this.settings.getLobby();
    final Location spawn = lobby.getLobbySpawn();
    player.teleport(spawn);
  }

  private void addCurrency(final Player player) {
    final PlayerInventory inventory = player.getInventory();
    final ItemStack stack = new ItemStack(Material.NETHER_STAR, 64);
    for (int i = 0; i < 6; i++) {
      inventory.addItem(stack);
    }
  }

  public void setPlayerToMurderer(final Player murderer) {
    this.murderers.add(murderer);
  }

  public void startGame() {
    this.game.startGame(this.settings, this.murderers, this.participants);
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public MurderGame getGame() {
    return this.game;
  }

  public GameSettings getSettings() {
    return this.settings;
  }
}
