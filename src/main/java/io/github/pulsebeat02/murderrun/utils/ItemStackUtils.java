package io.github.pulsebeat02.murderrun.utils;

import io.github.pulsebeat02.murderrun.map.part.CarPartItemStack;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class ItemStackUtils {

  private ItemStackUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static boolean isCarPart(final ItemStack stack) {
    final ItemMeta meta = stack.getItemMeta();
    final PersistentDataContainer container = meta.getPersistentDataContainer();
    final NamespacedKey key = CarPartItemStack.getCarPartKey();
    final String id = container.get(key, PersistentDataType.STRING);
    return id != null;
  }
}
