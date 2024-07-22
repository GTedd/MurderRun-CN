package io.github.pulsebeat02.murderrun.map.part;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.SplittableRandom;

public final class CarPartItemStack {

  private static final SplittableRandom RANDOM = new SplittableRandom();
  private static final String PDC_ID = "car_part";

  private final ItemStack stack;
  private Location location;

  public CarPartItemStack(final Location location) {
    this.location = location;
    this.stack = this.createItemStack();
  }

  public void spawn() {
    final World world = this.location.getWorld();
    final Item item = world.dropItemNaturally(this.location, this.stack);
    this.customizeItemEntity(item);
  }

  private void customizeItemEntity(final Item item) {
    item.setUnlimitedLifetime(true);
    item.setWillAge(false);
  }

  private ItemStack createItemStack() {
    final ItemStack stack = new ItemStack(Material.DIAMOND);
    final ItemMeta meta = this.customize(stack.getItemMeta());
    stack.setItemMeta(meta);
    return stack;
  }

  private ItemMeta customize(final ItemMeta meta) {
    this.tagData(meta);
    this.setLore(meta);
    this.changeProperties(meta);
    return meta;
  }

  private void changeProperties(final ItemMeta meta) {
    final int id = this.randomizeTexture();
    meta.displayName(Locale.CAR_PART_NAME.build());
    meta.setCustomModelData(id);
  }

  private void setLore(final ItemMeta meta) {
    if (!meta.hasLore()) {
      final List<Component> components = List.of(Locale.CAR_PART_LORE.build());
      meta.lore(components);
    }
  }

  private void tagData(final ItemMeta meta) {
    final PersistentDataContainer container = meta.getPersistentDataContainer();
    final NamespacedKey key = MurderRun.getKey();
    container.set(key, PersistentDataType.STRING, PDC_ID);
  }

  private int randomizeTexture() {
    return RANDOM.nextInt(1, 8);
  }

  public ItemStack getStack() {
    return this.stack;
  }

  public Location getLocation() {
    return this.location;
  }

  public void setLocation(final Location location) {
    this.location = location;
  }

  public static String getPDCId() {
    return PDC_ID;
  }
}
