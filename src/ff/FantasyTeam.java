package ff;

import java.time.LocalDateTime;

import java.util.ArrayList;

public class FantasyTeam {
	private String teamName;
	private String teamOwner;
	private String timeCreated;
	
	private ArrayList<Player> roster;
	
	public int qbCount;
	public int rbCount;
	public int wrCount;
	public int teCount;
	public int defenseCount;
	public int kCount;
	
	public int qbLimit = 3;
	public int rbLimit = 4;
	public int wrLimit = 5;
	public int teLimit = 3;
	public int defenseLimit = 2;
	public int kLimit = 2;
	
	public FantasyTeam(String teamName, String teamOwner, String timeCreated) {
		this.teamName = teamName;
		this.teamOwner = teamOwner;
		this.timeCreated = timeCreated;
		
		this.roster = new ArrayList<Player>();
	} // end constructor
	
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	} // end setTeamName
	
	public String getTeamName() {
		return this.teamName;
	} // end getTeamName
	
	public void setTeamOwner(String teamOwner) {
		this.teamOwner = teamOwner;
	} // end setTeamOwner
	
	public String getTeamOwner() {
		return this.teamOwner;
	} // end getTeamOwner
	
	public void setTimeCreated(String timeCreated) {
		this.timeCreated = timeCreated;
	} // end setTimeCreated
	
	public String getTimeCreated() {
		return this.timeCreated;
	} // end getTimeCreated
	
	public void addPlayer(Player player) {
		if (!isPositionOrRosterLimitReached(player)) {
			
			roster.add(player);
		}
	} // end method addPlayer
	
	public void removePlayer(Player player) {
		
	} // end method removePlayer
	
	public boolean isPositionOrRosterLimitReached(Player player) {
		qbCount = 0;
		rbCount = 0;
		wrCount = 0;
		teCount = 0;
		defenseCount = 0;
		kCount = 0;
		
		// if team is at capacity
		if (roster.size() >= 16) {
			FantasyFootballGUI.createGenericAlert("Cannot Add Player", "Roster Size Limit Reached. Cannot Add Any More Players.");
			return true;
		}
		
		// count amount of each position on team
		for (int i = 0; i < roster.size(); i++) {
			switch (roster.get(i).getPosition()) {
			case "QB":
				qbCount++;
				break;
			case "RB":
				rbCount++;
				break;
			case "WR":
				wrCount++;
				break;
			case "TE":
				teCount++;
				break;
			case "DEFENSE":
				defenseCount++;
				break;
			case "K":
				kCount++;
				break;
			} // end switch
		} // end for: count each position
		
		this.qbCount = qbCount;
		this.rbCount = rbCount;
		this.wrCount = wrCount;
		this.teCount = teCount;
		this.defenseCount = defenseCount;
		this.kCount = kCount;
		
		// create alerts if attempt is made to exceed positional limits per team
		switch (player.getPosition()) {
		case "QB":
			if (qbCount >= qbLimit) {
				FantasyFootballGUI.createGenericAlert("Position Limit Reached", "Cannot Have More than 3 Quarterbacks On Team.");
				return true;
			}
		case "RB":
			if (rbCount >= rbLimit) {
				FantasyFootballGUI.createGenericAlert("Position Limit Reached", "Cannot Have More than 4 Running Backs On Team.");
				return true;
			}
		case "WR":
			if (wrCount >= wrLimit) {
				FantasyFootballGUI.createGenericAlert("Position Limit Reached", "Cannot Have More than 5 Wide Receivers On Team.");
				return true;
			}
		case "TE":
			if (teCount >= teLimit) {
				FantasyFootballGUI.createGenericAlert("Position Limit Reached", "Cannot Have More than 3 Tight Ends On Team.");
				return true;
			}
		case "DEFENSE":
			if (defenseCount >= defenseLimit) {
				FantasyFootballGUI.createGenericAlert("Position Limit Reached", "Cannot Have More than 2 Defenses On Team.");
				return true;
			}
		case "K":
			if (kCount >= kLimit) {
				FantasyFootballGUI.createGenericAlert("Position Limit Reached", "Cannot Have More than 2 Kickers On Team.");
				return true;
			}
		} // end switch
		
		return false;
	} // end isPositionOrLimitReached
	
	public ArrayList<Player> getTeamRoster() {
		return roster;
	} // end method getTeamRoster
	
	@Override
	public String toString() {
		return "Team Name: " + teamName + "\nTeam Owner: " + teamOwner + "\nTime Created: " + timeCreated + "\n";
	} // end toString
	
} // end class FantasyTeam
