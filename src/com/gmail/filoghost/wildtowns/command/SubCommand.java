package com.gmail.filoghost.wildtowns.command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.gmail.filoghost.wildtowns.object.TownRank;

import lombok.Getter;
import lombok.Setter;

public abstract class SubCommand {
	
	@Getter	private final String name;
	@Getter	private final String[] nameParts;
	@Getter	private List<String> description;

	@Getter @Setter	private String usage;
	@Getter @Setter	private int minArgs;
	
	@Getter @Setter private TownRank requiredRank; // Si può trasformare in una lista
	
	@Getter @Setter private boolean hideFromHelp;
	
	protected SubCommand(String name) {
		this.name = name;
		this.nameParts = name.split(" ");
	}
	
	public String getPermission(String base) {
		return base + "." + name.replace(" ", "_");
	}
	
	protected void setDescription(String... description) {
		this.description = Arrays.asList(description);
	}
	
	protected int getCost() {
		return 0;
	}
	
	public abstract void execute(CommandSender sender, String label, String[] args);

}
