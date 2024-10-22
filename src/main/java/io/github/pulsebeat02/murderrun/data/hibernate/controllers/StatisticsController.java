package io.github.pulsebeat02.murderrun.data.hibernate.controllers;

import io.github.pulsebeat02.murderrun.data.hibernate.HibernateIdentifiers;
import io.github.pulsebeat02.murderrun.game.statistics.StatisticsManager;
import org.hibernate.SessionFactory;

public final class StatisticsController extends AbstractController<StatisticsManager> {

  public StatisticsController(final SessionFactory factory) {
    super(factory, HibernateIdentifiers.STATISTICS_MANAGER_ID);
  }

  @Override
  public StatisticsManager createDefaultEntity() {
    return new StatisticsManager();
  }
}
