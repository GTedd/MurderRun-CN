package io.github.pulsebeat02.murderrun.locale;

import static io.github.pulsebeat02.murderrun.locale.LocaleParent.*;

public interface Locale extends LocaleParent {
  NullComponent<Sender> CAR_PART_NAME = itemName("murder_run.item.car_part.name");
  NullComponent<Sender> CAR_PART_LORE = itemLore("murder_run.item.car_part.lore");
  UniComponent<Sender, Integer> CAR_PART_RETRIEVAL = title("murder_run.game.car_part_retrieval", null);
  UniComponent<Sender, String> PLAYER_DEATH = title("murder_run.game.death", null);
}
