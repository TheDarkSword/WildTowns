package com.gmail.filoghost.wildtowns.timer;

import java.util.concurrent.TimeUnit;

import org.bukkit.plugin.Plugin;

import com.gmail.filoghost.wildtowns.object.TownValueManager;
import com.gmail.filoghost.wildtowns.util.Utils;
import wild.api.scheduler.BukkitTimer;

public class UpdateTownValuesTimer extends BukkitTimer {

	public UpdateTownValuesTimer(Plugin plugin, TimeUnit timeUnit, int duration) {
		super(plugin, 0, Utils.secondsToTicks(timeUnit.toSeconds(duration)));
	}

	@Override
	public void run() {
		TownValueManager.update();
	}

}
