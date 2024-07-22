package io.github.pulsebeat02.murderrun.map.part;

import io.github.pulsebeat02.murderrun.MurderGame;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.config.GameConfiguration;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.InnocentPlayer;
import io.github.pulsebeat02.murderrun.player.Murderer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;
import java.util.UUID;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.title.Title.title;

public final class CarPartEvents implements Listener {

  private final MurderGame game;

  private int carPartCount;

  public CarPartEvents(final MurderGame game) {
    this.game = game;
  }

  @EventHandler
  public void onPlayerPickupItem(final PlayerAttemptPickupItemEvent event) {

    final Item item = event.getItem();
    final ItemStack stack = item.getItemStack();
    if (this.checkCarPart(stack)) {
      return;
    }

    final UUID owner = item.getOwner();
    final PlayerManager manager = this.game.getPlayerManager();
    final Optional<GamePlayer> player = manager.lookupPlayer(owner);
    if (player.isEmpty()) {
      return;
    }

    final GamePlayer gamePlayer = player.get();
    if (gamePlayer instanceof final InnocentPlayer innocent) {
      innocent.setHasCarPart(true);
    } else if (gamePlayer instanceof Murderer) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerThrowItem(final PlayerDropItemEvent event) {

    final Item item = event.getItemDrop();
    final ItemStack stack = item.getItemStack();
    if (this.checkCarPart(stack)) {
      return;
    }

    final GameConfiguration configuration = this.game.getConfiguration();
    final Location truckLocation = configuration.getTruckLocation();
    final double distSquared = item.getLocation().distanceSquared(truckLocation);
    if (distSquared > 16) {
      return;
    }

    item.remove();
    this.carPartCount++;
    final int goal = configuration.getCarPartCount();
    int remaining = goal - this.carPartCount;
    final PlayerManager manager = game.getPlayerManager();
    for (Player player : manager.getParticipants()) {
      player.showTitle(title(Locale.CAR_PART_RETRIEVAL.build(remaining), empty()));
    }

    if (this.carPartCount == goal) {
      game.finishGame();
      return;
    }

    final Player thrower = event.getPlayer();
    final PlayerInventory inventory = thrower.getInventory();
    final ItemStack[] contents = inventory.getContents();
    for (ItemStack slot : contents) {
      if (!checkCarPart(slot)) {
        return;
      }
    }

    final UUID uuid = thrower.getUniqueId();
    final GamePlayer player = manager.lookupPlayer(uuid).orElseThrow();
    if (player instanceof final InnocentPlayer innocent) {
      innocent.setHasCarPart(false);
    }
  }

  private boolean checkCarPart(final ItemStack stack) {
    final ItemMeta meta = stack.getItemMeta();
    final PersistentDataContainer container = meta.getPersistentDataContainer();
    final NamespacedKey key = MurderRun.getKey();
    final String data = container.get(key, PersistentDataType.STRING);
    final String check = CarPart.getPdcId();
    return data == null || !data.equals(check);
  }
}
