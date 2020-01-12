package com.gmail.filoghost.wildtowns.task;

import java.io.IOException;
import java.util.logging.Level;

import com.gmail.filoghost.wildtowns.WildTowns;
import com.gmail.filoghost.wildtowns.object.base.Saveable;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TrySaveTask implements Runnable {
	
	Saveable saveable;
	Runnable onSuccess;
	Runnable onError;

	@Override
	public void run() {
		try {
			saveable.save();
			if (onSuccess != null) {
				onSuccess.run();
			}
		} catch (IOException e) {
			WildTowns.logError(Level.SEVERE, "Couldn't save " + saveable.toString(), e);
			if (onError != null) {
				onError.run();
			}
		}
	}

}
