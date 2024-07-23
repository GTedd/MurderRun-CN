package io.github.pulsebeat02.murderrun.locale;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.JoinConfiguration.separator;
import static net.kyori.adventure.text.format.NamedTextColor.*;

import java.util.List;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public interface LocaleParent {

  TranslationManager MANAGER = new TranslationManager();

  NullComponent<Sender> PREFIX =
      () -> text().color(RED).append(text('['), text("Murder Run", AQUA), text(']')).build();

  static NullComponent<Sender> error(final String key) {
    return () -> MANAGER.render(translatable(key, RED));
  }

  static <T> UniComponent<Sender, T> error(final String key, final Function<T, String> function) {
    return argument -> error0(key, List.of(text(createFinalText(argument, function), AQUA)));
  }

  static NullComponent<Sender> itemLore(final String key) {
    return () -> translatable(key, GRAY);
  }

  static NullComponent<Sender> itemName(final String key) {
    return () -> translatable(key, GOLD);
  }

  static NullComponent<Sender> title(final String key, final NamedTextColor color) {
    return () -> translatable(key, color);
  }

  static <T> UniComponent<Sender, T> title(
      final String key, final Function<T, String> function, final NamedTextColor argColor) {
    return argument -> info0(key, List.of(text(createFinalText(argument, function), argColor)));
  }

  static NullComponent<Sender> info(final String key) {
    return () -> format(translatable(key, GOLD));
  }

  static <T> UniComponent<Sender, T> info(final String key, final Function<T, String> function) {
    return argument -> format(info0(key, List.of(text(createFinalText(argument, function), AQUA))));
  }

  static Component info0(final String key, final List<Component> arguments) {
    return internal0(key, GOLD, arguments);
  }

  static Component error0(final String key, final List<Component> arguments) {
    return internal0(key, RED, arguments);
  }

  static Component internal0(
      final String key, final NamedTextColor color, final List<Component> arguments) {
    return MANAGER.render(translatable(key, color, arguments));
  }

  static <T> String createFinalText(final T argument, final Function<T, String> function) {
    return function == null ? argument.toString() : function.apply(argument);
  }

  static Component format(final Component message) {
    return MANAGER.render(join(separator(space()), PREFIX.build(), message));
  }

  @FunctionalInterface
  interface NullComponent<S extends Sender> {

    Component build();

    default void send(final S sender) {
      sender.sendMessage(format(this.build()));
    }
  }

  @FunctionalInterface
  interface UniComponent<S extends Sender, A0> {

    Component build(A0 arg0);

    default void send(final S sender, final A0 arg0) {
      sender.sendMessage(format(this.build(arg0)));
    }
  }

  @FunctionalInterface
  interface BiComponent<S extends Sender, A0, A1> {

    Component build(A0 arg0, A1 arg1);

    default void send(final S sender, final A0 arg0, final A1 arg1) {
      sender.sendMessage(format(this.build(arg0, arg1)));
    }
  }
}
