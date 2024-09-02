package io.github.pulsebeat02.murderrun.commmand.gui.lobby;

import static net.kyori.adventure.text.Component.empty;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.commmand.gui.ChainedGui;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class ChooseLobbyGui implements ChainedGui {

  private final MurderRun plugin;
  private final ChestGui gui;
  private final HumanEntity watcher;
  private final Audience audience;
  private final Gui previous;

  public ChooseLobbyGui(final MurderRun plugin, final HumanEntity watcher, final Gui previous) {
    final Component component = Message.CHOOSE_LOBBY_GUI_TITLE.build();
    final String raw = AdventureUtils.serializeComponentToLegacyString(component);
    final AudienceProvider provider = plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final UUID uuid = watcher.getUniqueId();
    this.plugin = plugin;
    this.gui = new ChestGui(6, raw);
    this.watcher = watcher;
    this.audience = audiences.player(uuid);
    this.previous = previous;
  }

  @Override
  public void updateItems() {

    final PaginatedPane pages = new PaginatedPane(0, 0, 9, 3);
    pages.populateWithItemStacks(this.getLobbies());
    pages.setOnClick(event -> {
      final ItemStack item = event.getCurrentItem();
      if (item == null) {
        return;
      }

      final ItemMeta meta = item.getItemMeta();
      if (meta == null) {
        return;
      }

      final PersistentDataContainer container = meta.getPersistentDataContainer();
      final String name = container.get(Keys.ARENA_TITLE, PersistentDataType.STRING);
      final byte[] bytes = container.get(Keys.ARENA_SPAWN, PersistentDataType.BYTE_ARRAY);
      if (name == null || bytes == null) {
        return;
      }

      final Location spawn = MapUtils.byteArrayToLocation(bytes);
      final ModifyLobbyGui gui =
          new ModifyLobbyGui(this.plugin, this.watcher, name, spawn, true, this);
      gui.updateItems();
      event.setCancelled(true);
    });
    this.gui.addPane(pages);

    final OutlinePane background = new OutlinePane(0, 5, 9, 1);
    final GuiItem border =
        new GuiItem(Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build());
    border.setAction(event -> event.setCancelled(true));
    background.addItem(border);
    background.setRepeat(true);
    background.setPriority(Pane.Priority.LOWEST);
    this.gui.addPane(background);

    final StaticPane navigation = new StaticPane(0, 5, 9, 1);
    navigation.addItem(this.createBackStack(pages), 0, 0);
    navigation.addItem(this.createForwardStack(pages), 8, 0);
    navigation.addItem(this.createCloseStack(), 4, 0);
    this.gui.addPane(navigation);

    this.gui.show(this.watcher);
  }

  private List<ItemStack> getLobbies() {
    final LobbyManager manager = this.plugin.getLobbyManager();
    final Map<String, Lobby> lobbies = manager.getLobbies();
    final List<ItemStack> items = new ArrayList<>();
    for (final Entry<String, Lobby> entry : lobbies.entrySet()) {
      final String name = entry.getKey();
      final Lobby lobby = entry.getValue();
      final Location spawn = lobby.getLobbySpawn();
      final Component title = Message.CHOOSE_LOBBY_GUI_LOBBY_DISPLAY.build(name);
      final Component lore =
          AdventureUtils.createLocationComponent(Message.CHOOSE_LOBBY_GUI_LOBBY_LORE, spawn);
      final ItemStack item = Item.builder(Material.WHITE_BANNER)
          .name(title)
          .lore(lore)
          .pdc(Keys.ARENA_TITLE, PersistentDataType.STRING, name)
          .pdc(Keys.ARENA_SPAWN, PersistentDataType.BYTE_ARRAY, MapUtils.locationToByteArray(spawn))
          .build();
      items.add(item);
    }
    return items;
  }

  private GuiItem createCloseStack() {
    return new GuiItem(
        Item.builder(Material.BARRIER).name(Message.SHOP_GUI_CANCEL.build()).build(), event -> {
          final HumanEntity entity = event.getWhoClicked();
          this.previous.show(entity);
          event.setCancelled(true);
        });
  }

  private GuiItem createForwardStack(final PaginatedPane pages) {
    return new GuiItem(
        Item.builder(Material.GREEN_WOOL).name(Message.SHOP_GUI_FORWARD.build()).build(), event -> {
          final int current = pages.getPage();
          final int max = pages.getPages() - 1;
          if (current < max) {
            pages.setPage(current + 1);
            this.gui.update();
          }
          event.setCancelled(true);
        });
  }

  private GuiItem createBackStack(final PaginatedPane pages) {
    return new GuiItem(
        Item.builder(Material.RED_WOOL).name(Message.SHOP_GUI_BACK.build()).build(), event -> {
          final int current = pages.getPage();
          if (current > 0) {
            pages.setPage(current - 1);
            this.gui.update();
          }
          event.setCancelled(true);
        });
  }
}
