package io.github.tetratheta.autoregionfarm;

import io.github.tetratheta.autoregionfarm.command.ARFCommand;
import io.github.tetratheta.mol.plugin.BasePlugin;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

/// Bootstraps the AutoRegionFarm plugin and owns the active runtime.
public final class AutoRegionFarm extends BasePlugin<AutoRegionFarmRuntime> {

  /// Creates the services and Bukkit resources for the current plugin configuration.
  ///
  /// @return new AutoRegionFarm runtime
  @Override
  protected AutoRegionFarmRuntime createRuntime() {
    return new AutoRegionFarmRuntime(this);
  }

  /// Registers commands after the initial runtime is available.
  @Override
  protected void onPluginEnabled() {
    getLifecycleManager()
        .registerEventHandler(
            LifecycleEvents.COMMANDS,
            cmd -> cmd.registrar().register(new ARFCommand(this).getCommand()));
  }
}
