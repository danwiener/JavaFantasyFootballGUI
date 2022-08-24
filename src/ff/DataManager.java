package ff;

import java.io.FileOutputStream; // Necessary imports
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.FileWriter;
import java.util.Arrays;
import java.io.File; 
import java.io.FileNotFoundException;
import java.io.IOException; 
import java.util.Scanner;
import java.util.ArrayList;

public class DataManager {
	
	public DataManager() {}
	
	// method to load league into FantasyFootballGUI
	public ArrayList<String> loadLeagueWindowData() {
		ArrayList<String> leaguesList = new ArrayList<>();
		
		File dir = new File("/Users/danielwiener/eclipse-workspace/FantasyFootball");
		File[] dir_contents = dir.listFiles();
		for (File object : dir_contents) {
			if ( (object.getName().contains(".csv")) && (object.getName().length() > 4) ) {
				String leagueName = object.getName().substring(0, (object.getName().length() - 4));
				leaguesList.add(leagueName);
			}
		}
		return leaguesList;
	} // end method loadLeagueWindowData
	
	// method to check whether league exists
	public boolean doesFileExist(String leagueName) {
		String fileName = leagueName + ".csv";
		
		File dir = new File("/Users/danielwiener/eclipse-workspace/FantasyFootball");
		File[] dir_contents = dir.listFiles();
		for (File object : dir_contents) {
			if (object.getName().equals(fileName)) {
				return true; // league already exists
			}
		}
		return false; // league does not already exist
	}
	
	// method to check whether team exists
	public boolean doesTeamExist(String teamName, League league) {
		String fileName = league.getLeagueName() + ".csv";
		try (Scanner fileIn = new Scanner(Path.of(fileName))) { // open data file
			// 1st row is league info. skip
			if (fileIn.hasNextLine())
				fileIn.nextLine();
			
			// loop through multiple records
			while (fileIn.hasNext()) { // still have records?
				// 1. read one record
				String record = fileIn.nextLine();
				String[] cols = record.split(","); // split a comma separated line
				String teamNameField = cols[0].trim();
				
				if (teamNameField.equals(teamName)) {
					return true; // team name already exists
				}
			} // end while
			fileIn.close();
		}
		
		catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		return false; // team does not already exist
	} // end method doesTeamExist
	
	public void createFile(League league) {
		String fileName = league.getLeagueName() + ".csv";

		// try to create file
		try {
			File newFile = new File(fileName);
			if (newFile.createNewFile()) {
				System.out.println("File created: " + newFile.getName());
			}
		} // end try
		catch (IOException ioe) {
			System.out.println("File already exists.");
		} // end catch
	} // end method createFile
	
	public void writeFile(League league) {
		String fileName = league.getLeagueName() + ".csv";
		// try to open newly created file to write to
		try {
			FileWriter fw = new FileWriter(fileName);
			PrintWriter out = new PrintWriter(fw);
			
			// ',' divides the word into columns
			out.print(league.getLeagueName()); //first row first column
			out.print(",");
			out.print(league.getLeagueOwner()); // first row second column
			out.print(",");
			out.print(league.getMaxTeams()); // first row third column
			out.print(",");
			out.println();
			for (int i = 0; i < league.getTeamsInLeague().size(); i++) {
				out.print(league.getTeamsInLeague().get(i).getTeamName());
				out.print(",");
				out.print(league.getTeamsInLeague().get(i).getTeamOwner());
				out.print(",");
				out.println(league.getTeamsInLeague().get(i).getTimeCreated());
				for (int j = 0; j < league.getTeamsInLeague().get(i).getTeamRoster().size(); j++) {
					out.print("Position");
					out.print(",");
					out.print(league.getTeamsInLeague().get(i).getTeamRoster().get(j).getPosition());
					out.print(",");
					out.print(league.getTeamsInLeague().get(i).getTeamRoster().get(j).getName());
					out.print(",");
					out.println(league.getTeamsInLeague().get(i).getTeamRoster().get(j).getTeam());
				}
			}
			
			//Flush the output to the file
			out.flush();
			
			//Close the Print Writer
			out.close();
			
			//Close the File Writer
			fw.close();
			//outFS.println
		} // end try
		catch (FileNotFoundException fnfe) {
			System.out.println("File not found.");
		} // end catch
		catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		} // end catch
		
	} // end method writeFile

} // end class DataManager
