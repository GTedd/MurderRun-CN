package io.github.pulsebeat02.murderrun.player.death;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.map.MurderMap;
import io.github.pulsebeat02.murderrun.map.part.CarPartItemStack;
import io.github.pulsebeat02.murderrun.map.part.CarPartManager;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.ItemStackUtils;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.title.Title.title;

public final class PlayerDeathManager {

  private final MurderGame game;
  private final ScheduledExecutorService service;

  public PlayerDeathManager(final MurderGame game) {
    this.game = game;
    this.service = Executors.newScheduledThreadPool(8);
  }

  public void shutdownExecutor() {
    this.service.shutdown();
  }

  private void setGameMode(final Player player) {
    player.setGameMode(GameMode.SPECTATOR);
  }

  private ArmorStand summonArmorStand(final Player player) {
    final Location location = player.getLocation();
    final World world = location.getWorld();
    final Entity entity = world.spawnEntity(location, EntityType.ARMOR_STAND);
    return (ArmorStand) entity;
  }

  private ItemStack getHeadItemStack(final Player player) {
    final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    final SkullMeta meta = (SkullMeta) head.getItemMeta();
    meta.setOwningPlayer(player);
    head.setItemMeta(meta);
    return head;
  }

  private void setArmorStandRotations(final ArmorStand stand) {
    stand.setHeadPose(MapUtils.toEulerAngle(300, 0, 0));
    stand.setBodyPose(MapUtils.toEulerAngle(280, 0, 0));
    stand.setLeftArmPose(MapUtils.toEulerAngle(250, 0, 0));
    stand.setRightArmPose(MapUtils.toEulerAngle(250, 360, 30));
  }

  private void setArmorStandGear(final Player player, final ArmorStand stand) {
    final EntityEquipment equipment = stand.getEquipment();
    final ItemStack head = this.getHeadItemStack(player);
    final ItemStack chest = createArmorPiece(Material.LEATHER_CHESTPLATE);
    final ItemStack legs = createArmorPiece(Material.LEATHER_LEGGINGS);
    final ItemStack boots = createArmorPiece(Material.LEATHER_BOOTS);
    equipment.setHelmet(head);
    equipment.setChestplate(chest);
    equipment.setLeggings(legs);
    equipment.setBoots(boots);
  }

  private void announcePlayerDeath(final Player dead) {
    final String name = dead.getName();
    final Component title = Locale.PLAYER_DEATH.build(name);
    final Component subtitle = empty();
    AdventureUtils.showTitleForAllParticipants(this.game, title, subtitle);
  }

  public void initiateDeathSequence(final GamePlayer gamePlayer) {

    final Player player = gamePlayer.getPlayer();
    this.setGameMode(player);

    final ArmorStand stand = this.summonArmorStand(player);
    this.customizeArmorStand(stand);
    this.setArmorStandRotations(stand);
    this.setArmorStandGear(player, stand);
    this.announcePlayerDeath(player);
    this.summonCarParts(player);
  }

  private void customizeArmorStand(final ArmorStand stand) {
    stand.setInvulnerable(true);
    stand.setGravity(false);
  }

  private void summonCarParts(final Player player) {
    final PlayerInventory inventory = player.getInventory();
    final ItemStack[] slots = inventory.getContents();
    for (final ItemStack slot : slots) {
      if (!ItemStackUtils.isCarPart(slot)) {
        continue;
      }
      final MurderMap map = this.game.getMurderMap();
      final CarPartManager manager = map.getCarPartManager();
      final CarPartItemStack stack = manager.getCarPartItemStack(slot);
      final Location death = player.getLastDeathLocation();
      stack.setPickedUp(false);
      stack.setLocation(death);
      stack.spawn();
    }
  }

  public void spawnParticles() {
    final PlayerManager manager = this.game.getPlayerManager();
    this.service.scheduleAtFixedRate(
        () -> manager.getDead().forEach(this::spawnParticleOnCorpse), 0, 1, TimeUnit.SECONDS);
  }

  private void spawnParticleOnCorpse(final GamePlayer gamePlayer) {
    final Player player = gamePlayer.getPlayer();
    final Location location = player.getLastDeathLocation();
    final Location clone = location.clone().add(0, 1, 0);
    final World world = clone.getWorld();
    final BlockData data = Material.RED_CONCRETE.createBlockData();
    world.spawnParticle(Particle.BLOCK_DUST, clone, 10, 0.5, 0.5, 0.5, data);
  }

  public static ItemStack createArmorPiece(final Material leatherPiece) {
    final ItemStack item = new ItemStack(leatherPiece);
    final LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
    meta.setColor(Color.RED);
    item.setItemMeta(meta);
    return item;
  }
}
