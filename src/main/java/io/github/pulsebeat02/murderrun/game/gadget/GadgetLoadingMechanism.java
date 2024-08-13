package io.github.pulsebeat02.murderrun.game.gadget;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadgets;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadgets;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import io.github.pulsebeat02.murderrun.utils.Keys;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class GadgetLoadingMechanism {

  private static final Map<String, Constructor<?>> GADGET_LOOK_UP_MAP = new HashMap<>();

  public static void init() {
    final Plugin plugin = JavaPlugin.getProvidingPlugin(MurderRun.class);
    final SurvivorGadgets[] survivorGadgets = SurvivorGadgets.values();
    final KillerGadgets[] killerGadgets = KillerGadgets.values();
    for (final SurvivorGadgets gadget : survivorGadgets) {
      final Class<?> clazz = gadget.getClazz();
      handleGadgetClass(plugin, clazz);
    }
    for (final KillerGadgets gadget : killerGadgets) {
      final Class<?> clazz = gadget.getClazz();
      handleGadgetClass(plugin, clazz);
    }
  }

  private static void handleGadgetClass(final Plugin plugin, final Class<?> clazz) {
    final Constructor<?> constructor = getConstructor(clazz);
    final Gadget gadget = invokeGadgetConstructor(plugin, constructor);
    final String name = gadget.getName();
    GADGET_LOOK_UP_MAP.put(name, constructor);
  }

  private static Constructor<?> getConstructor(final Class<?> clazz) {
    final Constructor<?>[] constructors = clazz.getConstructors();
    if (constructors.length == 0) {
      final String message = "Couldn't find constructor of gadget class %s".formatted(clazz);
      throw new AssertionError(message);
    }
    return constructors[0];
  }

  private static Gadget invokeGadgetConstructor(
      final Plugin plugin, final Constructor<?> constructor) {
    try {
      final Class<?>[] arguments = constructor.getParameterTypes();
      final boolean hasPlugin = arguments.length == 1;
      return (Gadget) (hasPlugin ? constructor.newInstance(plugin) : constructor.newInstance());
    } catch (final InvocationTargetException | InstantiationException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  private final GadgetManager manager;
  private final Map<String, Gadget> gameGadgets;

  public GadgetLoadingMechanism(final GadgetManager manager) {
    final MurderRun run = manager.getPlugin();
    this.manager = manager;
    this.gameGadgets = this.getUsedGadgets(run);
  }

  private Map<String, Gadget> getUsedGadgets(
      @UnderInitialization GadgetLoadingMechanism this, final MurderRun plugin) {
    final Collection<Constructor<?>> gadgetClasses = GADGET_LOOK_UP_MAP.values();
    final Map<String, Gadget> gadgets = new HashMap<>();
    for (final Constructor<?> constructor : gadgetClasses) {
      final Gadget gadget = invokeGadgetConstructor(plugin, constructor);
      final String name = gadget.getName();
      gadgets.put(name, gadget);
    }
    return gadgets;
  }

  public void shutdown() {
    final Collection<Gadget> values = this.gameGadgets.values();
    for (final Gadget gadget : values) {
      if (!(gadget instanceof final Listener listener)) {
        continue;
      }
      HandlerList.unregisterAll(listener);
    }
  }

  public @Nullable Gadget getGadgetFromStack(final ItemStack stack) {
    final String data = requireNonNull(ItemUtils.getPersistentDataAttribute(
        stack, Keys.GADGET_KEY_NAME, PersistentDataType.STRING));
    return this.gameGadgets.get(data);
  }

  public GadgetManager getManager() {
    return this.manager;
  }

  public Map<String, Gadget> getGameGadgets() {
    return this.gameGadgets;
  }
}
