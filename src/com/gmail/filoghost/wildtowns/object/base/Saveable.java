package com.gmail.filoghost.wildtowns.object.base;

import java.io.IOException;

import org.bukkit.Bukkit;

import com.gmail.filoghost.wildtowns.WildTowns;
import com.gmail.filoghost.wildtowns.task.TrySaveTask;

public abstract class Saveable {
	
	public void trySaveAsync(Runnable onSuccess, Runnable onError) {
		Bukkit.getScheduler().runTaskAsynchronously(WildTowns.getInstance(), new TrySaveTask(this, onSuccess, onError));
	}
	
	public abstract void save() throws IOException;
	
}
