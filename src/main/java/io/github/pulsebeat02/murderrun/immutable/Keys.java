package io.github.pulsebeat02.murderrun.immutable;

import org.bukkit.NamespacedKey;

public final class Keys {

  public static final String NAMESPACE = "murderrun";

  public static NamespacedKey SPECIAL_SWORD = createNamespacedPluginKey("sword");
  public static NamespacedKey CAR_PART_UUID = createNamespacedPluginKey("car-part-uuid");
  public static NamespacedKey GADGET_KEY_NAME = createNamespacedPluginKey("gadget");
  public static NamespacedKey CAN_BREAK_BLOCKS = createNamespacedPluginKey("can-break-blocks");
  public static NamespacedKey KILLER_TRACKER = createNamespacedPluginKey("killer-tracker");
  public static NamespacedKey FLASH_BANG = createNamespacedPluginKey("flash-bang");
  public static NamespacedKey SMOKE_GRENADE = createNamespacedPluginKey("smoke-grenade");
  public static NamespacedKey FLASHLIGHT_USE = createNamespacedPluginKey("flashlight-use");
  public static NamespacedKey TRANSLOCATOR = createNamespacedPluginKey("translocator");
  public static NamespacedKey PLAYER_TRACKER = createNamespacedPluginKey("player-tracker");
  public static NamespacedKey HOOK = createNamespacedPluginKey("hook");
  public static NamespacedKey PORTAL_GUN = createNamespacedPluginKey("portal-gun");
  public static NamespacedKey UUID = createNamespacedPluginKey("uuid");
  public static NamespacedKey AIR_DROP = createNamespacedPluginKey("air_drop");
  public static NamespacedKey ICE_SPIRIT_OWNER = createNamespacedPluginKey("ice-spirit-owner");
  public static NamespacedKey DEATH_HOUND_OWNER = createNamespacedPluginKey("death-hound-owner");
  public static NamespacedKey DORMAGOGG_OWNER = createNamespacedPluginKey("dormagogg-owner");
  public static NamespacedKey ITEM_WAND = createNamespacedPluginKey("item-wand");
  public static NamespacedKey PLAYER_UUID = createNamespacedPluginKey("player-uuid");
  public static NamespacedKey ARENA_NAME = createNamespacedPluginKey("arena-name");
  public static NamespacedKey LOBBY_NAME = createNamespacedPluginKey("lobby-name");
  public static NamespacedKey KILLER_ROLE = createNamespacedPluginKey("killer-role");

  private static NamespacedKey createNamespacedPluginKey(final String key) {
    return new NamespacedKey(NAMESPACE, key);
  }
}
