package io.github.pulsebeat02.murderrun.data.hibernate;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.data.hibernate.controllers.ArenaController;
import io.github.pulsebeat02.murderrun.data.hibernate.controllers.LobbyController;
import io.github.pulsebeat02.murderrun.data.hibernate.controllers.StatisticsController;
import io.github.pulsebeat02.murderrun.data.yaml.PluginDataConfigurationMapper;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.game.statistics.StatisticsManager;

import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public final class HibernateManager {

  private final MurderRun plugin;
  private final ArenaController arenaController;
  private final LobbyController lobbyController;
  private final StatisticsController statisticsController;
  private final SessionFactory factory;

  public HibernateManager(final MurderRun plugin) {
    this.plugin = plugin;
    this.factory = this.constructSessionFactory(plugin);
    this.arenaController = new ArenaController(this.factory);
    this.lobbyController = new LobbyController(this.factory);
    this.statisticsController = new StatisticsController(this.factory);
  }

  private SessionFactory constructSessionFactory(@UnderInitialization HibernateManager this, final MurderRun plugin) {
    try {
      final PluginDataConfigurationMapper mapper = plugin.getConfiguration();
      return this.constructSession(mapper);
    } catch (final HibernateException e) {
      throw new RuntimeException("Failed to connect to database!", e);
    }
  }

  private SessionFactory constructSession(@UnderInitialization HibernateManager this, final PluginDataConfigurationMapper mapper) {
    return new Configuration()
            .setProperty(Environment.JAKARTA_JDBC_DRIVER, mapper.getDatabaseDriver())
            .setProperty(Environment.JAKARTA_JDBC_USER, mapper.getDatabaseUsername())
            .setProperty(Environment.JAKARTA_JDBC_PASSWORD, mapper.getDatabasePassword())
            .setProperty(Environment.HBM2DDL_AUTO, mapper.getDatabaseHbm2ddl())
            .setProperty(Environment.SHOW_SQL, mapper.isDatabaseShowSql())
            .setProperty(Environment.JAKARTA_JDBC_URL, mapper.getDatabaseUrl())
            .setProperty(Environment.AUTOCOMMIT, true)
            .setProperty(Environment.AUTO_CLOSE_SESSION, true)
            .addAnnotatedClass(ArenaManager.class)
            .addAnnotatedClass(LobbyManager.class)
            .addAnnotatedClass(StatisticsManager.class)
            .buildSessionFactory();
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public ArenaController getArenaController() {
    return this.arenaController;
  }

  public LobbyController getLobbyController() {
    return this.lobbyController;
  }

  public StatisticsController getStatisticsController() {
    return this.statisticsController;
  }

  public SessionFactory getFactory() {
    return this.factory;
  }
}
