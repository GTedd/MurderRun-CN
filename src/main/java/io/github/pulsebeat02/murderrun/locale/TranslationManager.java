package io.github.pulsebeat02.murderrun.locale;

import io.github.pulsebeat02.murderrun.locale.minimessage.MurderTranslator;
import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;

public final class TranslationManager {

  private static final java.util.Locale DEFAULT_LOCALE = Locale.ENGLISH;
  private static final Key ADVENTURE_KEY = Key.key("murder_run", "main");

  private final TranslationRegistry registry;
  private final MurderTranslator translator;

  public TranslationManager() {
    this.registry = TranslationRegistry.create(ADVENTURE_KEY);
    this.registry.defaultLocale(DEFAULT_LOCALE);
    this.translator = new MurderTranslator(ADVENTURE_KEY, this.registry);
    this.registerTranslations();
  }

  private void registerTranslations() {
    this.registerLocale();
    this.addGlobalRegistry();
  }

  private void registerLocale() {
    final ResourceBundle bundle = this.getBundle();
    this.registry.registerAll(DEFAULT_LOCALE, bundle, false);
  }

  private void addGlobalRegistry() {
    GlobalTranslator.translator().addSource(this.translator);
  }

  private PropertyResourceBundle getBundle() {
    try (final Reader reader =
        ResourceUtils.getResourceAsReader("locale/murder_run_en.properties")) {
      return new PropertyResourceBundle(reader);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public TranslationRegistry getRegistry() {
    return this.registry;
  }

  public Component render(final TranslatableComponent component) {
    return GlobalTranslator.render(component, DEFAULT_LOCALE);
  }
}
