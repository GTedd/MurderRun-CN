package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.CitizensManager;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class EnderShadows extends KillerGadget {

  private static final String TEXTURE_SIGNATURE =
      "JJLyJh0n4sr4EwvWlIHu6Rz+eiCv6gIte/HZa4z1XH0CSnUBcrKXfIlzaLKo24k6OmJMysIRRtGVjhBYpyTe0ggCdFSibp6hDOfH1j/BR8ZmJkBn4ylpZZZmc4fxqsEc04AuxhkAUkGqpseirS2p44eQb60CyVwCf8kfh4sSSvmgaORx+aEENpwALbx6aUBJ2DRlzBRtftTo3kSTWnyKJznbQyMQcFHyCXHuT96gfavJ1acavZtFcMw/xBpZM4X36Z8jR9srOF2W3y0RttyJkMR7xuWaidVg7X17GoRDkChsnK0KdawkWD+u/LVZM2mzdOSqKKHXMle2qLCLdWYTrmCufT+t/G6BrvyEtmflnP81ciVbfA7utpKH6XDzEKpA4mRIHtIRIfctO2ltTbWft5/VhXWqB+dgBuOErdUtW9qkGlg5au5LK/laDgTTQprnpq8Hd287X4AL2aAghMPCcTfIrE0Wnd2n6JbkIrXx5kA4F8K2f+N78TkXhGbbtMh1ktNzNvZXi47PFijuqalBPhhaAjCOJiWQx5b6PoCg6FWXhdZxC8ndCPB2xHtiqOKUWnCLkhBBtg/Lj+WETVvUP/GLjbMzKxljMycZHHxq9fZlWvnFtOnoiTWrljVUO5oLnR5bO0+MelTb7vN3pswLU2qO71okwCndfMhvXEnhZTs=";
  private static final String TEXTURE_VALUE =
      "ewogICJ0aW1lc3RhbXAiIDogMTY3Mjc3NTg4Mzk5MywKICAicHJvZmlsZUlkIiA6ICI2MDJmMjA0M2YzYjU0OGU1ODQyYjE4ZjljMDg2Y2U0ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCb3J5c18iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM5M2Q2NzU2M2VlNTYwMTg3NjZkMjFiMzY2OTJjYmU1NjNkYzBhOTFiYWNmYzBkYjU0YjMwYzJiOWEyNDg3MyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9";

  private final Multimap<GamePlayer, GamePlayer> shadows;

  public EnderShadows() {
    super(
        "ender_shadows",
        Material.BLACK_STAINED_GLASS,
        Message.ENDER_SHADOWS_NAME.build(),
        Message.ENDER_SHADOWS_LORE.build(),
        48);
    this.shadows = HashMultimap.create();
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location spawn = arena.getSpawn();
    final Player player = event.getPlayer();
    final GamePlayer killer = manager.getGamePlayer(player);
    final CitizensManager npcManager = game.getNPCManager();
    manager.applyToAllLivingInnocents(
        survivor -> this.handleAllSurvivors(npcManager, scheduler, killer, survivor, spawn));
  }

  private void handleAllSurvivors(
      final CitizensManager manager,
      final GameScheduler scheduler,
      final GamePlayer killer,
      final GamePlayer survivor,
      final Location spawn) {

    final Component msg = Message.ENDER_SHADOWS_ACTIVATE.build();
    survivor.sendMessage(msg);

    final Entity shadow = this.getNPCEntity(manager, spawn);
    scheduler.scheduleRepeatedTask(
        () -> this.handleSurvivorTeleport(killer, survivor, shadow), 2 * 20L, 20L);

    final Location[] old = {survivor.getLocation()};
    scheduler.scheduleRepeatedTask(
        () -> {
          shadow.teleport(old[0]);
          old[0] = survivor.getLocation();
        },
        0,
        10 * 20L);
  }

  private void handleSurvivorTeleport(
      final GamePlayer killer, final GamePlayer survivor, final Entity shadow) {
    final Collection<GamePlayer> players = this.shadows.get(survivor);
    final Component msg = Message.ENDER_SHADOWS_EFFECT.build();
    final MetadataManager metadata = killer.getMetadataManager();
    survivor.apply(player -> {
      final Location location = player.getLocation();
      final Location other = shadow.getLocation();
      final double distance = location.distanceSquared(other);
      if (distance < 1) {
        players.add(survivor);
        survivor.showTitle(msg, empty());
        metadata.setEntityGlowing(player, ChatColor.RED, true);
      } else if (players.contains(survivor)) {
        players.remove(survivor);
        metadata.setEntityGlowing(player, ChatColor.RED, false);
      }
    });
  }

  private Entity getNPCEntity(final CitizensManager manager, final Location location) {
    final NPC npc = this.spawnNPC(manager, location);
    final Entity entity = npc.getEntity();
    entity.setInvulnerable(true);
    return entity;
  }

  private NPC spawnNPC(final CitizensManager manager, final Location location) {
    final NPCRegistry registry = manager.getRegistry();
    final NPC npc = registry.createNPC(EntityType.PLAYER, "");
    final SkinTrait trait = npc.getOrAddTrait(SkinTrait.class);
    trait.setSkinPersistent("Shadow", TEXTURE_SIGNATURE, TEXTURE_VALUE);
    npc.spawn(location);
    return npc;
  }
}
