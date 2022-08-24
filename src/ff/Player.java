package ff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Player {
	String position;
	String name;
	String team;
	String finalPlayerStr;

	// two parameter constructor
	public Player(String position, String team) {
		this.position = position;
		this.team = team;
	}
	
	// three parameter constructor
	public Player(String position, String name, String team) {
		this.position = position;
		this.name = name;
		this.team = team;
	}
	
	public String getPosition() {
		return this.position;
	} // end getPosition
	
	public String getName() {
		return this.name;
	} // end getName
	
	public String getTeam() {
		return this.team;
	} // end getTeam
	
	@Override
	public String toString() {
		if (this.name != null) {
			finalPlayerStr = "Position: " + this.position + " | Name: " + this.name + " | Team: " + this.team + "\n";
		}
		if (this.name == null) {
			finalPlayerStr = "Position: " + this.position + " | Team: " + this.team + "\n";
		}
		return finalPlayerStr;
	} // end toString

} // end class Player
