package com.gmail.filoghost.wildtowns.object.base;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.gmail.filoghost.wildtowns.WildTowns;
import com.gmail.filoghost.wildtowns.util.Validate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import wild.api.uuid.UUIDRegistry;


public final class Resident extends Saveable {
	
	private final UUID uuid;
	@Getter protected Town town;
	protected Set<Resident> friends;
	@Setter private ChatMode chatMode;
	
	private List<UUID> tempFriendsUUIDs;

	public Resident(@NonNull UUID uuid) {
		this.uuid = uuid;
		this.friends = Sets.newHashSet();
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
	public void tellIfOnline(String message) {
		Player player = Bukkit.getPlayer(uuid);
		if (player != null) {
			player.sendMessage(message);
		}
	}
	
	public ChatMode getChatMode() {
		if (chatMode != null) {
			return chatMode;
		} else {
			return ChatMode.PUBLIC;
		}
	}
	
	public Collection<Resident> getFriends() {
		return Collections.unmodifiableSet(friends);
	}
	
	public int getFriendsCount() {
		return friends.size();
	}
	
	public boolean hasFriend(@NonNull Resident resident) {
		return friends.contains(resident);
	}
	
	public boolean addFriend(@NonNull Resident resident) {
		Validate.isTrue(resident != this, "Can't add self as friend");
		return friends.add(resident);
	}
	
	public boolean removeFriend(@NonNull Resident resident) {
		return friends.remove(resident);
	}
	
	@Override
	public String toString() {
		return UUIDRegistry.getNameFallback(uuid);
	}
	
	
	/*
	 *  Caricamenti e salvataggi
	 */
	
	public Resident(@NonNull YamlConfiguration config) {
		this.uuid = UUID.fromString(config.getString(Nodes.UUID));
		this.friends = Sets.newHashSet();
		this.tempFriendsUUIDs = Lists.newArrayList();
		for (String serializedFriendUUID : config.getStringList(Nodes.FRIENDS)) {
			this.tempFriendsUUIDs.add(UUID.fromString(serializedFriendUUID));
		}
	}
	
	public void setupFriends() {
		if (tempFriendsUUIDs != null) {
			for (UUID tempFriendUUID : tempFriendsUUIDs) {
				Resident friend = WTManager.getOfflineResident(tempFriendUUID);
				Validate.notNull(friend, "Resident friend " + tempFriendUUID + " not found for " + this);
				addFriend(friend);
			}
			tempFriendsUUIDs = null;
		}
	}
	
	@Override
	public void save() throws IOException {
		YamlConfiguration config = new YamlConfiguration();
		config.set(Nodes.UUID, this.uuid.toString());
		
		List<String> serializedFriends = Lists.newArrayList();
		for (Resident friend : friends) {
			serializedFriends.add(friend.getUUID().toString());
		}
		config.set(Nodes.FRIENDS, serializedFriends);

		config.save(WildTowns.getResidentFile(this.uuid));
	}

	private static class Nodes {
		
		private static final String
			UUID = "uuid",
			FRIENDS = "friends";
		
	}
	
}
