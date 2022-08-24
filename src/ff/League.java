package ff;

import java.util.ArrayList; // Necessary imports

public class League {
	private String leagueName;
	private String leagueOwner;
	private int maxTeams;
	private ArrayList<FantasyTeam> teamsInLeague;
	
	public League(String leagueName, String leagueOwner, int maxTeams) {
		this.leagueName = leagueName;
		this.leagueOwner = leagueOwner;
		this.maxTeams = maxTeams;
		
		this.teamsInLeague = new ArrayList<FantasyTeam>();
	} // end constructor
	
	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	} // end setLeagueName
	
	public String getLeagueName() {
		return this.leagueName;
	} // end getLeagueName
	
	public void setLeagueOwner(String leagueOwner) {
		this.leagueOwner = leagueOwner;
	} // end setLeagueOwner
	
	public String getLeagueOwner() {
		return this.leagueOwner;
	} // end getLeagueOwner
	
	public void setMaxTeams(int maxTeams) {
		this.maxTeams = maxTeams;
	} // end setMaxTeams
	
	public String getMaxTeams() {
		return String.valueOf(this.maxTeams);
	} // end getMaxTeams
	
	public void addTeam(FantasyTeam team) {
		teamsInLeague.add(team);
	} // end addTeam
	
	public ArrayList<FantasyTeam> getTeamsInLeague() {
		return teamsInLeague;
	} // end getTeamsInLeague
	
	public boolean isLeagueFull() {
		if (teamsInLeague.size() >= maxTeams) {
			return true;
		}
		return false;
	} // end isLeagueFull

	@Override
	public String toString() {
		return "League Name: " + leagueName + "\nLeague Owner: " + leagueOwner + "\nMax Teams: " + maxTeams;
	} // end toString
	
} // end class League