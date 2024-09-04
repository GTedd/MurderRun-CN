package io.github.pulsebeat02.murderrun.game.event;

import io.github.pulsebeat02.murderrun.game.GameManager;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.Collection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public final class DupePreventListener implements Listener {

  private final GameManager manager;

  public DupePreventListener(final GameManager manager) {
    this.manager = manager;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onItemDrop(final PlayerDropItemEvent event) {

    final Player player = event.getPlayer();
    final Collection<Player> participants = this.manager.getParticipants();
    if (!participants.contains(player)) {
      return;
    }

    final Item item = event.getItemDrop();
    final ItemStack stack = item.getItemStack();
    final ItemStack other = ItemFactory.createCurrency(1);
    if (!stack.isSimilar(other)) {
      return;
    }

    event.setCancelled(true);
  }
}