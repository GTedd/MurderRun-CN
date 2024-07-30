package io.github.pulsebeat02.murderrun.gadget;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class MurderGadget {

  private final String name;
  private final ItemStack gadget;

  public MurderGadget(
      final String name,
      final Material material,
      final Component itemName,
      final Component itemLore) {
    this(name, material, itemName, itemLore, null);
  }

  public MurderGadget(
      final String name,
      final Material material,
      final Component itemName,
      final Component itemLore,
      final @Nullable Consumer<ItemStack> consumer) {
    this.name = name;
    this.gadget = this.constructItemStack(material, itemName, itemLore, consumer);
  }

  public ItemStack constructItemStack(
      @UnderInitialization MurderGadget this,
      final Material material,
      final Component itemName,
      final Component itemLore,
      final @Nullable Consumer<ItemStack> consumer) {

    if (itemName == null || itemLore == null || material == null) {
      throw new AssertionError("Failed to create ItemStack for trap!");
    }

    final String name = AdventureUtils.serializeComponentToLegacy(itemName);
    final String rawLore = AdventureUtils.serializeComponentToLegacy(itemLore);
    final List<String> lore = List.of(rawLore);
    final ItemStack stack = new ItemStack(material);
    final ItemMeta meta = stack.getItemMeta();
    if (meta == null) {
      throw new AssertionError("Failed to construct ItemStack for trap!");
    }

    meta.setDisplayName(name);
    meta.setLore(lore);
    stack.setItemMeta(meta);

    if (consumer != null) {
      consumer.accept(stack);
    }

    return stack;
  }

  public void onRightClickEvent(final MurderGame game, final PlayerInteractEvent event) {}

  public void onDropEvent(final MurderGame game, final PlayerDropItemEvent event) {}

  public ItemStack getGadget() {
    return this.gadget;
  }

  public String getName() {
    return this.name;
  }
}