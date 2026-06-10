package io.github.tetratheta.autoregionfarm;

import io.github.tetratheta.autoregionfarm.config.ARFConfig;
import io.github.tetratheta.autoregionfarm.crop.CropRegistry;
import io.github.tetratheta.autoregionfarm.listener.CropBreakListener;
import io.github.tetratheta.autoregionfarm.listener.FarmlandTramplingListener;
import io.github.tetratheta.autoregionfarm.region.RegionService;
import io.github.tetratheta.autoregionfarm.service.FarmlandProtectionService;
import io.github.tetratheta.autoregionfarm.service.HarvestService;
import io.github.tetratheta.autoregionfarm.service.NotificationService;
import io.github.tetratheta.mol.message.MessageService;
import io.github.tetratheta.mol.plugin.PluginRuntime;

/// Wires configuration-backed services and Bukkit resources for one plugin runtime.
public final class AutoRegionFarmRuntime extends PluginRuntime {
  private final ARFConfig config;
  private final CropRegistry cropRegistry;
  private final FarmlandProtectionService farmlandProtectionService;
  private final HarvestService harvestService;
  private final MessageService messageService;
  private final NotificationService notificationService;
  private final RegionService regionService;

  /// Creates all services from the current disk configuration and registers runtime listeners.
  ///
  /// @param plugin plugin entry point that owns this runtime
  public AutoRegionFarmRuntime(AutoRegionFarm plugin) {
    super(plugin);
    config = new ARFConfig(plugin);
    messageService = new MessageService(plugin, config.getLanguage());
    cropRegistry = new CropRegistry(config.getConfiguredCropMaterials(), messageService);
    regionService = new RegionService(config.getWatchedRegions(), messageService);

    boolean changed = config.validateAndFix(messageService, cropRegistry, regionService);
    if (changed) config.saveConfig();

    notificationService =
        new NotificationService(
            messageService, config.getNotificationChannel(), config.getChatCooldownTicks());
    harvestService = new HarvestService(config, notificationService);
    farmlandProtectionService = new FarmlandProtectionService(config, regionService);
    registerListener(
        new CropBreakListener(
            plugin,
            config,
            cropRegistry,
            regionService,
            harvestService,
            notificationService,
            this::runTask));
    registerListener(new FarmlandTramplingListener(farmlandProtectionService));
  }

  /// Returns the active configuration facade.
  ///
  /// @return active configuration facade
  public ARFConfig getConfig() {
    return config;
  }

  /// Returns the active crop registry.
  ///
  /// @return active crop registry
  public CropRegistry getCropRegistry() {
    return cropRegistry;
  }

  /// Returns the active localized message service.
  ///
  /// @return localized message service
  public MessageService getMessageService() {
    return messageService;
  }

  /// Returns the active WorldGuard region service.
  ///
  /// @return active region service
  public RegionService getRegionService() {
    return regionService;
  }
}
