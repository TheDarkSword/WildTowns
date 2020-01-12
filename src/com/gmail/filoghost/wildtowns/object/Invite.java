package com.gmail.filoghost.wildtowns.object;

public class Invite {
	
	private long expiration;
	
	public Invite(long inviteValidForMillis) {
		this.expiration = System.currentTimeMillis() + inviteValidForMillis;
	}
	
	public boolean isExpired() {
		return System.currentTimeMillis() > expiration;
	}

}
