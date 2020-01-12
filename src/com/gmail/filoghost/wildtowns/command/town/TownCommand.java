package com.gmail.filoghost.wildtowns.command.town;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.wildtowns.Perms;
import com.gmail.filoghost.wildtowns.command.HelpSubCommand;
import com.gmail.filoghost.wildtowns.command.SubCommandFramework;
import com.gmail.filoghost.wildtowns.command.town.sub.TownAcceptCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownAllyAddCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownAllyRemoveCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownPlotBuyCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownPlotDescCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownChatCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownClaimCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownConfirmCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownCreateCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownDepositCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownEnemyAddCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownEnemyRemoveCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownFriendAddCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownFriendListCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownFriendRemoveCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownPlotForsaleCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownHomeCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownInfoCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownInviteCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownKickCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownLeaveCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownListCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownMapCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownNoticeCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownOnlineCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownPlotNotforsaleCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownOutpostCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownPlotAbandonCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownPlotRemoveownerCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownPlotSettingsCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownTaxPlotCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownSetrankCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownSethomeCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownSetmayorCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownSetwarpCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownShowCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownTaxResidentCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownRankingCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownUnclaimCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownWarCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownWarpCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownWithdrawCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownDeinviteCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownDeleteCommand;
import com.gmail.filoghost.wildtowns.command.town.sub.TownDelwarpCommand;
import com.gmail.filoghost.wildtowns.disk.Lang;
import com.gmail.filoghost.wildtowns.object.base.Town;
import com.gmail.filoghost.wildtowns.object.base.WTManager;

public class TownCommand extends SubCommandFramework {
	
	
	public TownCommand(JavaPlugin plugin, String label, String... aliases) {
		super(plugin, Perms.COMMAND_TOWN, label, aliases);
		
		setSubCommands(
			// Comandi per tutti (anche senza città)
			new HelpSubCommand(this),
			new TownInfoCommand(),
			new TownShowCommand(),
			new TownListCommand(),
			new TownRankingCommand(),
			new TownMapCommand(),
			new TownFriendAddCommand(),
			new TownFriendRemoveCommand(),
			new TownFriendListCommand(),
			new TownConfirmCommand(),
			new TownCreateCommand(),
			// Comandi per cittadini
			new TownHomeCommand(),
			new TownWarpCommand(),
			new TownChatCommand(),
			new TownOnlineCommand(),
			new TownDepositCommand(),
			new TownPlotBuyCommand(),
			new TownPlotAbandonCommand(),
			new TownAcceptCommand(),
			new TownLeaveCommand(),
			// Comandi per diplomatici
			new TownInviteCommand(),
			new TownDeinviteCommand(),
			new TownKickCommand(),
			// Comandi per assistenti
			new TownClaimCommand(),
			new TownUnclaimCommand(),
			new TownOutpostCommand(),
			new TownPlotSettingsCommand(),
			new TownPlotDescCommand(),
			new TownPlotForsaleCommand(),
			new TownPlotNotforsaleCommand(),
			new TownPlotRemoveownerCommand(),
			new TownSetwarpCommand(),
			new TownDelwarpCommand(),
			new TownSetrankCommand(),
			new TownWarCommand(),
			// Comandi per sindaci
			new TownNoticeCommand(),
			new TownSethomeCommand(),
			new TownWithdrawCommand(),
			new TownAllyAddCommand(),
			new TownAllyRemoveCommand(),
			new TownEnemyAddCommand(),
			new TownEnemyRemoveCommand(),
			new TownTaxPlotCommand(),
			new TownTaxResidentCommand(),
			new TownSetmayorCommand(),
			new TownDeleteCommand()
		);
	}
	
	
	@Override
	public void sendCommandHelp(CommandSender sender) {
		Town town = null;
		if (sender instanceof Player) {
			town = WTManager.getOnlineResident((Player) sender).getTown();
		}
		
		if (town != null) {
			sender.sendMessage(Lang.color_main + "La tua città è " + Lang.color_highlight + town.getName() + Lang.color_main + ".");
			sender.sendMessage(Lang.color_main + "Per informazioni sulla tua città, scrivi " + Lang.color_highlight + "/" + label + " info");
		} else {
			sender.sendMessage(Lang.color_main + "Non appartieni a nessuna città.");
		}
		
		sender.sendMessage(Lang.color_main + "Per una lista dei comandi, scrivi " + Lang.color_highlight + "/" + label + " help");
	}

}
