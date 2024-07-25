package io.github.pulsebeat02.murderrun.map.part;

import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class CarPartItemStack {

  private static final NamespacedKey CAR_PART_ID = new NamespacedKey("murder_run", "car-part-uuid");

  private final String uuid;
  private final ItemStack stack;
  private Location location;
  private boolean pickedUp;

  public CarPartItemStack(final Location location) {
    this.uuid = UUID.randomUUID().toString();
    this.location = location;
    this.stack = this.createItemStack();
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

  private void tagData(final ItemMeta meta) {
    final PersistentDataContainer container = meta.getPersistentDataContainer();
    container.set(CAR_PART_ID, PersistentDataType.STRING, this.uuid);
  }

  private void setLore(final ItemMeta meta) {
    if (!meta.hasLore()) {
      final List<Component> components = List.of(Locale.CAR_PART_ITEM_LORE.build());
      final List<String> lore =
          components.stream().map(AdventureUtils::serializeComponentToLegacy).toList();
      meta.setLore(lore);
    }
  }

  private void changeProperties(final ItemMeta meta) {
    final Component component = Locale.CAR_PART_ITEM_NAME.build();
    final String raw = AdventureUtils.serializeComponentToLegacy(component);
    final int id = RandomUtils.generateInt(1, 7);
    meta.setDisplayName(raw);
    meta.setCustomModelData(id);
  }

  public static NamespacedKey getCarPartKey() {
    return CAR_PART_ID;
  }

  public void spawn() {
    final World world = this.location.getWorld();
    final Item item = world.dropItemNaturally(this.location, this.stack);
    this.customizeItemEntity(item);
  }

  private void customizeItemEntity(final Item item) {
    item.setUnlimitedLifetime(true);
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

  public String getUuid() {
    return this.uuid;
  }

  public boolean isPickedUp() {
    return this.pickedUp;
  }

  public void setPickedUp(final boolean pickedUp) {
    this.pickedUp = pickedUp;
  }
}
