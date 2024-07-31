package io.github.pulsebeat02.murderrun.reflect.v1_21;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import io.github.pulsebeat02.murderrun.reflect.NMSUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NMSUtilsImpl implements NMSUtils {

  @Override
  public byte[] toByteArray(final ItemStack item) {
    try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      final int version = Bukkit.getUnsafe().getDataVersion();
      final RegistryAccess.Frozen dimension = MinecraftServer.getServer().registryAccess();
      final net.minecraft.world.item.ItemStack craftItemStack = CraftItemStack.asNMSCopy(item);
      final CompoundTag compound = (CompoundTag) craftItemStack.save(dimension);
      compound.putInt("DataVersion", version);
      NbtIo.writeCompressed(compound, outputStream);
      return outputStream.toByteArray();
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public ItemStack fromByteArray(final byte[] bytes) {
    try (final ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
      final RegistryAccess.Frozen dimension = MinecraftServer.getServer().registryAccess();
      final NbtAccounter unlimited = NbtAccounter.unlimitedHeap();
      final CompoundTag old = NbtIo.readCompressed(stream, unlimited);
      final int dataVersion = old.getInt("DataVersion");
      final int ver = Bukkit.getUnsafe().getDataVersion();
      final DSL.TypeReference reference = References.ITEM_STACK;
      final MinecraftServer server = MinecraftServer.getServer();
      final DataFixer fixer = server.fixerUpper;
      final Dynamic<Tag> dynamic = new Dynamic<>(NbtOps.INSTANCE, old);
      fixer.update(reference, dynamic, dataVersion, ver);
      final CompoundTag newCompound = (CompoundTag) dynamic.getValue();
      return CraftItemStack.asCraftMirror(
          net.minecraft.world.item.ItemStack.parseOptional(dimension,
              newCompound));
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public void sendGlowPacket(final Player watcher, final Entity glow) {
    final int id = glow.getEntityId();
    final CraftPlayer player = (CraftPlayer) watcher;
    final ClientboundUpdateMobEffectPacket effect = new ClientboundUpdateMobEffectPacket(id,
        new MobEffectInstance(MobEffects.GLOWING, Integer.MAX_VALUE, 0, true, true), false);
    player.getHandle().connection.send(effect);
  }

  @Override
  public void sendRemoveGlowPacket(final Player watcher, final Entity glow) {
    final int id = glow.getEntityId();
    final CraftPlayer player = (CraftPlayer) watcher;
    final ClientboundRemoveMobEffectPacket effect = new ClientboundRemoveMobEffectPacket(id,
        MobEffects.GLOWING);
    player.getHandle().connection.send(effect);
  }
}
