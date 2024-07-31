package io.github.pulsebeat02.murderrun.gadget.innocent.tool;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Material;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public final class Shield extends MurderGadget {

  public Shield() {
    super(
        "shield",
        Material.SHIELD,
        Locale.SHIELD_TRAP_NAME.build(),
        Locale.SHIELD_TRAP_LORE.build(),
        stack -> {
          final ItemMeta meta = stack.getItemMeta();
          if (meta instanceof final Damageable damageable) {
            final int max = damageable.getMaxDamage();
            final int damage = max - 5;
            damageable.setDamage(damage);
          }
          stack.setItemMeta(meta);
        });
  }
}
