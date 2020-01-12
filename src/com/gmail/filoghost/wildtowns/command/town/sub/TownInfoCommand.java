package com.gmail.filoghost.wildtowns.command.town.sub;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.Perms;
import com.gmail.filoghost.wildtowns.command.SubCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.TownRank;
import com.gmail.filoghost.wildtowns.object.base.Plot;
import com.gmail.filoghost.wildtowns.object.base.Resident;
import com.gmail.filoghost.wildtowns.object.base.TaxCost;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;
import com.gmail.filoghost.wildtowns.util.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import wild.api.chat.ChatBuilder;
import wild.api.command.CommandFramework.CommandValidate;

public class TownInfoCommand extends SubCommand {
	
	private static final DateFormat creationDateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.ITALIAN);
	static {
		creationDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
	}

	public TownInfoCommand() {
		super("info");
		setMinArgs(0);
		setUsage("[città]");
		setDescription("Ottieni informazioni sulla tua o su un'altra città.");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Town town;
		boolean ownTown;
		boolean bypassNotice = false;
		
		if (args.length > 0) {
			town = WTManager.getTown(args[0]);
			CommandValidate.notNull(town, "La città specificata (" + args[0] + ") non esiste.");
			
			if (sender.hasPermission(Perms.MORE_INFO_DETAILS)) {
				ownTown = true;
				bypassNotice = true;
			} else if (sender instanceof Player) {
				Resident resident = WTManager.getOnlineResident((Player) sender);
				ownTown = town.hasResident(resident);
			} else {
				ownTown = false;
			}
		} else {
			town = WTManager.getOnlineResident(CommandValidate.getPlayerSender(sender)).getTown();
			CommandValidate.notNull(town, Lang.youDontHaveTown);
			ownTown = true;
		}
		
		// Header
		sender.sendMessage(Lang.format(Lang.format_townInfoHeader, "{town}", town.getNameAndTitle()));
		
		// Status
		if (town.isUnderProtection() || town.getWar() != null) {
			ChatBuilder statusBuilder = new ChatBuilder("Status:").color(Lang.chatColor_main);
			if (town.isUnderProtection()) {
				statusBuilder.append(" ");
				Utils.addUnderProtectionMessage(statusBuilder, town, ChatColor.GREEN);
			}
			if (town.getWar() != null) {
				statusBuilder.append(" ");
				Utils.addWarMessage(statusBuilder, town.getWar(), ChatColor.RED);
			}
			statusBuilder.send(sender);
		}
		
		// Data di creazione
		sender.sendMessage(Lang.color_main + "Data di creazione: " + Lang.color_highlight + creationDateFormat.format(town.getCreationTimestamp()));
		
		// Alleati
		ChatBuilder alliesBuilder = new ChatBuilder("Città alleate: ").color(Lang.chatColor_main);
		Collection<Town> allies;
		if (ownTown) {
			// Mostra anche le alleanze non confermate
			allies = town.getOnewayAllies();
			Utils.formattedJoin(alliesBuilder, allies, Lang.chatColor_main, ", ", (builder, allyTown) -> {
				builder.append(allyTown.getName());
				if (town.isReciprocalAlly(allyTown)) {
					builder.color(Lang.chatColor_highlight);
				} else {
					builder.color(ChatColor.DARK_GRAY).underlined(true).tooltip(Lang.chatColor_neutral, "Questa città non ha ancora confermato l'alleanza.");
				}
			});
		} else {
			allies = town.getReciprocalAllies();
			Utils.formattedJoin(alliesBuilder, allies, Lang.chatColor_main, ", ", (builder, allyTown) -> {
				builder.append(allyTown.getName()).color(Lang.chatColor_highlight);
			});
		}
		if (allies.isEmpty()) {
			alliesBuilder.append("-").color(ChatColor.DARK_GRAY);
		}
		alliesBuilder.send(sender);
		
		
		// Nemici
		Collection<Town> enemies = town.getEnemies();
		sender.sendMessage(Lang.color_main + "Città nemiche: " + (!enemies.isEmpty() ? Utils.joinColors(enemies, ", ") : ChatColor.DARK_GRAY + "-"));
		
		
		// Numero di plot
		sender.sendMessage(Lang.color_main + "Plot: " + Lang.color_highlight + town.getPlotsCount() + Lang.color_main + "/" + Lang.color_highlight + town.getSizeData().getMaxPlots());
		
		
		// Banca e tasse (solo ai cittadini)
		if (ownTown) {
			sender.sendMessage(Lang.color_main + "Banca: " + Lang.color_highlight + Lang.formatMoney(town.getMoneyBank()));

			long currentEarnings = town.getResidentTax() * (town.getResidentsCount() - 1); // -1 per escludere il sindaco
			for (Plot plot : town.getPlots()) {
				if (plot.getOwner() != null && !town.isMayor(plot.getOwner())) {
					currentEarnings += town.getPlotTax();
				}
			}
			
			String residentTaxInfo = Lang.formatMoney(town.getResidentTax());
			if (town.getPlotTax() > 0) {
				residentTaxInfo += " fissi + " + Lang.formatMoney(town.getPlotTax()) + " per plot";
			}
			
			ComponentBuilder taxesPartialsBuilder = new ComponentBuilder("Dettagli: ").color(Lang.chatColor_highlight);
			for (TaxCost taxCost : town.getTaxesCostsPartials()) {
				if (taxCost.getValue() == 0) {
					continue;
				}
				taxesPartialsBuilder.append("\n");
				taxesPartialsBuilder.append("Tasse dovute " + taxCost.getCause().getDueToWhat() + ": ").color(ChatColor.GRAY);
				taxesPartialsBuilder.append(Lang.formatMoney(taxCost.getValue())).color(ChatColor.WHITE);
			}
			
			ChatBuilder taxesBuilder = new ChatBuilder("Tasse sulla città: ").color(Lang.chatColor_main)
					.append(Lang.formatMoney(town.getTaxesCostsTotal())).color(Lang.chatColor_highlight)
					.append(" (").color(Lang.chatColor_neutral)
					.append("Info").underlined(true)
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, taxesPartialsBuilder.create()))
					.append(")", FormatRetention.NONE).color(Lang.chatColor_neutral)
					.append(" | ", FormatRetention.NONE).color(ChatColor.DARK_GRAY)
					.append("Guadagni della città: ").color(Lang.chatColor_main)
					.append(Lang.formatMoney(currentEarnings)).color(Lang.chatColor_highlight);
			
			taxesBuilder.send(sender);
			sender.sendMessage(Lang.color_main + "Tassa su ogni cittadino: " + Lang.color_highlight + residentTaxInfo);
		}
		
		sender.sendMessage(Lang.format_townInfoSeparator);
		
		
		// Sindaco e ranghi
		sender.sendMessage(Lang.color_main + "Sindaco: " + Lang.color_highlight + town.getMayor() + " (" + town.getSizeData().getMayorTitle() + ")");
		for (TownRank rank : TownRank.getPrestigeOrderNoMayor()) {
			Collection<Resident> byRank = town.getResidentsByRank(rank);
			sender.sendMessage(Lang.color_main + StringUtils.capitalize(rank.getPlural()) + " (" + byRank.size() + "): " + (byRank.isEmpty() ? ChatColor.DARK_GRAY + "-" : Utils.joinColors(byRank, ", ")));
		}
		
		// Cittadini semplici
		Collection<Resident> normalResidents = town.getResidentsByRank(null);
		sender.sendMessage(Lang.color_main + "Cittadini (" + normalResidents.size() + "): " + (normalResidents.isEmpty() ? ChatColor.DARK_GRAY + "-" : Utils.joinColors(normalResidents, ", ")));
		
		// Totale
		sender.sendMessage(Lang.color_main + "Totale giocatori: " + Lang.color_highlight + town.getResidentsCount());
		
		if (bypassNotice) {
			sender.sendMessage(ChatColor.YELLOW  + "(Mostrate informazioni aggiuntive grazie al permesso)");
		}
		
		// Spaziatura
		sender.sendMessage("");
	}

}
