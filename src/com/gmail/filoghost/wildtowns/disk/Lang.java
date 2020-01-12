package com.gmail.filoghost.wildtowns.disk;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

import org.bukkit.plugin.Plugin;

import net.cubespace.yamler.PreserveStatic;
import net.cubespace.yamler.YamlerConfig;
import net.cubespace.yamler.YamlerConfigurationException;
import net.md_5.bungee.api.ChatColor;
import wild.api.WildCommons;

@PreserveStatic
public class Lang extends YamlerConfig {
	
	public static transient ChatColor
	
		chatColor_main,
		chatColor_highlight,
		chatColor_neutral,
		chatColor_plotInfoWilderness,
		chatColor_plotInfoTown,
		chatColor_plotInfoSeparator,
		chatColor_plotInfoElement;

	
	public static String
	
		color_main = 				"&3",
		color_highlight = 			"&b",
		color_neutral = 			"&7",
		color_error = 				"&c",
		color_plotInfoWilderness =	"&2",
		color_plotInfoTown =		"&b",
		color_plotInfoSeparator =	"&7",
		color_plotInfoElement =		"&a",
		
		format_townChat =				"[{town}] {name}: ",
		format_rankTag =				"&7[{rank}] ",
		format_townTag =				"[{town}] ",
		format_townBroadcast = 			"[{town}] ",
		format_plotInfoPrefix =			"~ ",
		format_wildernessName = 		"Wilderness",
		format_plotInfoSeparator = 		" / ",
		format_townInfoHeader = 		"{1}=----------*[ {town} ]*----------=",
		format_townInfoSeparator =		"",
		format_helpHeader = 			"{1}=------------*[ Lista comandi (pagina {page}/{pages}) ]*------------=",
		format_topHeader = 				"{1}=------------*[ Classifica città (pagina {page}) ]*------------=",
		format_townMapHeader = 			"{1}=----------*[ Mappa ]*----------=",
		format_joinNotice =				"\n {1}Annuncio della città:\n {2}{message}\n ",
		format_newWarFrom =				"\n &4{town} ha ricevuto una dichiarazione di guerra da {from}:\n &c&n{link}\n ",
		format_newWarTo =				"\n &4{town} ha dichiarato guerra a {to}:\n &c&n{link}\n ",
		format_confirm_header = 		"&4=------------------*[&c Sei sicuro? &4]*------------------=",
		format_confirm_footer = 		"&4=--------------------------------------------------=",
		format_confirm_description = 	"&7{text}\\n ",
		format_confirm_buttonPrefix = 	"                        ",
		format_confirm_buttonStyle = 	"&e&n",
		format_confirm_buttonText = 	"Clicca per confermare",
		format_confirm_buttonTooltip = 	"Clicca qui per confermare il comando",
		
	
		internalError = 			"{E}Errore interno, per favore contatta lo staff.",
		
		plotAlreadyOwned = 			"{E}La tua città possiede già questo plot.",
		plotBelongsAnotherTown = 	"{E}Questo plot appartiene a un'altra città.",
		
		thatPlayerIsNotOnline = 	"{E}Quel giocatore non è online.",
		residentNotFound = 			"{E}Giocatore non trovato.",
		youDontHaveTown = 			"{E}Non sei in nessuna città.",
		youAlreadyHaveTown = 		"{E}Sei già in una città.",
		specifiedTownNotExist =		"{E}La città specificata non esiste.",
		cantUseCommandOnSelf = 		"{E}Non puoi utilizzare questo comando su te stesso.",
		
		townNameInvalidFormat = 	"{E}Il nome può contenere solo lettere maiuscole e minuscole.",
		
		noMoney = 					"{E}Non hai abbastanza soldi per {action} (servono {money}).",
		distanceTooShort = 			"{E}Devi mantenere una distanza di almeno {chunks} plot dalle altre città.",
		
		cantUseChorusFruit = 		"{E}Non puoi teletrasportarti all'interno della città con il frutto corale.",
		
		friendAdded = 				"{2}Hai aggiunto {friend} alla lista amici.",
		youHaveBeenAddedAsFriend =	"{2}{name} ti ha aggiunto alla sua lista amici.",
		youHaveBeenRemovedAsFriend ="{2}{name} ti ha rimosso alla sua lista amici.",
		friendRemoved = 			"{2}Hai rimosso {friend} dalla lista amici.",
		
		newTownCost = 				"{2}Hai pagato {money} per fondare la città.",
		newTownBroadcast = 			"{2}{player} ha fondato la città {town}!",
		
		youClaimedPlot =			"{2}Hai conquistato un plot. {1}({money})",
		youClaimedOutpost =			"{2}Hai conquistato un avamposto. {1}({money})",
		plotUnclaimed =				"{2}Hai abbandonato il plot.",
		moneyDeposited =			"{2}Hai depositato {money} nella banca.",
		moneyWithdrawn =			"{2}Hai ritirato {money} dalla banca.",
		playerInfo = 				"{2}{player}{1} è {2}{rank}{1} nella città {2}{town}{1}.",
		rankSet =					"{2}{player} ha assegnato il rango di {rank} a {ranked}.",
		chatMode =					"{2}Modalità chat: {chat}.",
		townLeave = 				"{2}{player} è uscito dalla città.",
		townKick =					"{2}{player} ha cacciato {kicked} dalla città.",
		joinNoticeSet =				"{2}Hai impostato l'annuncio della città.",
		joinNoticeRemoved = 		"{2}Hai rimosso l'annuncio della città.",
		youHaveInvited =			"{2}Hai invitato {invited} ad entrare nella città.",
		youHaveBeenInvited =		"{2}{player} ti ha invitato a entrare in {town}.\n{2}Conferma entro {seconds} secondi: &7/t accept {town}",
		youHaveBeenKicked =			"{2}{player} ti ha cacciato dalla città.",
		playerKickedTaxes =			"{2}{kicked} è stato cacciato dalla città per non aver potuto pagare le tasse.",
		youHaveBeenKickedTaxes =	"{2}Sei stato cacciato dalla città perché non avevi abbastanza soldi per pagare le tasse.",
		removeInvite =				"{2}Hai annullato l'invito di {invited}.",
		newTownResident = 			"{2}{player} è entrato nella città.",
		newMayor =					"{2}{old} ha spostato la carica di sindaco a {new}.",
		townDeletedCommand =		"{2}La città {town} è stata cancellata da {player}.",
		townDeletedTaxes =			"{2}La città {town} è stata cancellata per mancanza di fondi.",
		townWillBeDeletedTaxes =	"{E}La tua città verrà cancellata per mancanza di fondi alle ore {time}:00 se non vengono depositati soldi per pagare le tasse!",
		plotSettingsSaved =			"{2}Le nuove impostazioni sono state salvate.",
		plotOwnerRemoved = 			"{2}Hai rimosso questo plot a {owner}.",
		plotBought =				"{2}Hai comprato questo plot per {money}.",
		plotAbandoned =				"{2}Hai abbandonato questo plot.",
		plotForSale =				"{2}Hai messo in vendita questo plot per {money}.",
		plotNotForSale =			"{2}Hai rimosso questo plot dalla vendita.",
		youLeftTown = 				"{2}Hai abbandonato la città.",
		taxesCollected =			"{2}Sono state raccolte le tasse.",
		townHomeSet = 				"{2}Hai impostato la home della città. {1}({money})",
		teleportingTownHome = 		"{2}Stai andando alla home della città {town}.",
		townWarpSet =				"{2}Hai impostato il teletrasporto {warp}.",
		townWarpRemoved =			"{2}Hai rimosso il teletrasporto {warp}.",
		townWarpNotFound =			"{2}Non esiste nessun teletrasporto {warp}.",
		teleportingTownWarp = 		"{2}Stai andando al teletrasporto {warp}.",
		townWarpsList = 			"{2}Lista dei teletrasporti: {warps}.",
		newEnemy = 					"{2}La città {enemy} è stata dichiarata nemica.",
		removedEnemy =				"{2}La città {enemy} non è più nemica.",
		yourTownAddedAsEnemy = 		"{2}La tua città è stata dichiarata nemica da {enemy}.",
		yourTownRemovedAsEnemy = 	"{2}La tua città è stata rimossa dai nemici di {enemy}.",
		newAlly = 					"{2}La città {ally} è ora alleata.",
		removedAlly =				"{2}La città {ally} non è più alleata.",
		yourTownRemovedAsAlly =		"{2}La città {ally} ha eliminato l'alleanza.",
		newAllyRequestSent =		"{2}Hai mandato una richiesta di alleanza a {ally}. Il sindaco di {ally} può confermare con il comando &7/town ally add {from}",
		newAllyRequestReceived = 	"{2}La città {from} ha inviato una richiesta di alleanza.\n{2}Conferma: &7/town ally add {from}",
		onewayAllyRemoved = 		"{2}Hai rimosso {ally} dagli alleati, che non aveva confermato la richiesta.",
		targetIsUnderProtection	= 	"{E}{player} è nella sua città ed ha la protezione iniziale.",
		claimOverlapsWGRegion =		"{E}Il plot si sovrappone con una zona protetta.",
		claimOverlapsBorder =		"{E}Il plot non è completamente all'interno dei limiti del mondo.",
		residentTaxSet =			"{2}Hai imposto una tassa giornaliera di {money} a ogni cittadino.",
		plotTaxSet =				"{2}Hai imposto una tassa giornaliera di {money} per ogni plot posseduto.",
		descriptionSet = 			"{2}Hai cambiato la descrizione del plot in \"{description}\".",
		descriptionCleared = 		"{2}Hai rimosso la descrizione del plot.",
		thisCommandWillCost =		"Questo comando costerà {money}.",
		nonResidentsInsideClaim =	"Ci sono giocatori non appartenenti alla città dentro questo plot.";

	
	public Lang(Plugin plugin, String filename) {
		super(plugin, filename,
			"Sostituzioni:",
			"{1} = colore principale",
			"{2} = colore secondario (per evidenziare)",
			"{E} = colore di errore");
	}
	
	
	private static transient final DecimalFormat MONEY_FORMAT = new DecimalFormat("###,###");

	
	public static String formatMoney(long money) {
		return "$" + MONEY_FORMAT.format(money);
	}
	
	public static String format(String message, String placeholder1, Object replacement1) {
		return message.replace(placeholder1, replacement1.toString());
	}
	
	public static String format(String message, String placeholder1, Object replacement1, String placeholder2, Object replacement2) {
		return message.replace(placeholder1, replacement1.toString()).replace(placeholder2, replacement2.toString());
	}
	
	public static String format(String message, String placeholder1, Object replacement1, String placeholder2, Object replacement2, String placeholder3, Object replacement3) {
		return message.replace(placeholder1, replacement1.toString()).replace(placeholder2, replacement2.toString()).replace(placeholder3, replacement3.toString());
	}
	
	@Override
	public void init() throws YamlerConfigurationException {
		super.init();
		
		for (Field field : getClass().getDeclaredFields()) {
			if (field.getType() == String.class) {
				field.setAccessible(true);
				
				try {
					String value = (String) field.get(this);
					value = WildCommons.color(value.replace("\\n", "\n").replace("{1}", color_main).replace("{2}", color_highlight).replace("{E}", color_error));
					field.set(this, value);
				} catch (Exception e) {
					throw new YamlerConfigurationException(e);
				}
			}
		}
		
		chatColor_main = parseChatColorOrDefault(color_main);
		chatColor_highlight = parseChatColorOrDefault(color_highlight);
		chatColor_neutral = parseChatColorOrDefault(color_neutral);
		chatColor_plotInfoWilderness = parseChatColorOrDefault(color_plotInfoWilderness);
		chatColor_plotInfoTown = parseChatColorOrDefault(color_plotInfoTown);
		chatColor_plotInfoSeparator = parseChatColorOrDefault(color_plotInfoSeparator);
		chatColor_plotInfoElement = parseChatColorOrDefault(color_plotInfoElement);
	}
	
	private ChatColor parseChatColorOrDefault(String chatColor) {
		if (chatColor.length() >= 2) {
			ChatColor result = ChatColor.getByChar(chatColor.charAt(1));
			if (result != null) {
				return result;
			}
		}
		
		
		return ChatColor.GRAY;
	}

}
