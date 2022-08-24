package ff;

import javafx.application.Application; // Necessary imports
import javafx.application.Platform;
import javafx.geometry.Insets;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.ButtonType;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import javafx.stage.Stage;

import java.util.Optional;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import java.util.ArrayList;

public class FantasyFootballGUI extends Application {
	// GUI constants
	private static final String BORDER_STYLE = "-fx-padding: 5;" + "-fx-border-style: solid inside;"
			+ "-fx-border-width: 1;" + "-fx-border-insents: 5;" + "fx-border-radius: 5;" + "-fx-border-color: grey;";
	
	// GUI data
	League league; // initialize league to tie this GUI to;
    DataManager dataManager = new DataManager(); // to create file for league
	
	private String leagueName;
	private String leagueOwner;
	private String maxTeams;
	private String leagueSize = "0";
	private String teamName;
	
	private StringProperty leagueSizeSP = new SimpleStringProperty(leagueSize); // to bind display labels to inputted text
	private StringProperty leagueNameSPStageTitle = new SimpleStringProperty(leagueSize);
	private StringProperty viewRosterForTeamSP = new SimpleStringProperty(teamName);
	
	private LocalDateTime date; // date & time team was created
	private DateTimeFormatter formatter; // format localdatetime
	
	private ArrayList<String> freeAgentsList;
	private ArrayList<Player> freeAgentsPlayerList;
	private ArrayList<String> rosterList;
	private ArrayList<Player> rosterPlayerList;
	
	private int viewFreeAgentsCounter;
	
	private FreeAgents freeAgents = new FreeAgents();
	
	// GUI components
	private Button createTeamButton;
	private Button setLeagueNameButton;
	private Button viewRosterButton;
	private Button viewFreeAgentsButton;
	private Button addPlayerButton;
	private Button dropPlayerButton;
	
	private ListView<String> fantasyTeamsListView;
	private ListView<String> rosterListView;
	private ListView<String> leaguesListView;
	
	private Stage applicationStage;
	private Stage createOrLoadLeagueStage;
	private Stage createTeamStage;
	private Stage createLeagueStage;
	private Stage loadWindowStage;
	private Stage leagueRulesStage;
	
	// Starts application
	@Override
	public void start(Stage applicationStage) {
		programStart();
	} // end start method
	
	private void programStart() {
		// main layout
		BorderPane borderPane = new BorderPane();
		borderPane.setPadding(new Insets(10, 10, 10, 10));
		
		// top region
		if (leagueName == null) {
			leagueName = "";
		}
		String topLabelText = "Play Fantasy Football!";
		Label topLabel = createLabel(topLabelText, 20);

		Button restartButton = new Button("Restart");
		restartButton.setMinWidth(122.5);
		restartButton.setOnAction(e -> {
			applicationStage.close();
			if (createOrLoadLeagueStage.isShowing()) { createOrLoadLeagueStage.close(); }
			if (createTeamStage != null) {if (createTeamStage.isShowing()) { createTeamStage.close(); }}
			if (createLeagueStage != null) {if (createLeagueStage.isShowing()) { createLeagueStage.close(); }}
			if (loadWindowStage != null) {if (loadWindowStage.isShowing()) { loadWindowStage.close(); }}
			this.leagueName  = "";
			programStart();
		});
		
		HBox topLabelAndRestartButton = new HBox(644.5, topLabel, restartButton);
		borderPane.setTop(topLabelAndRestartButton);
		
		// center region
		HBox listViewRow = new HBox(5, makeViewTeamsPane(), makeRosterPane());
		borderPane.setCenter(listViewRow);
		
		// bottom region
		Button exitButton = new Button("Exit");
		exitButton.setMinWidth(233.5);
		exitButton.setOnAction(e -> Platform.exit());
		
	    setLeagueNameButton = new Button("Set League Name");
	    setLeagueNameButton.setMinWidth(233.5);
	    setLeagueNameButton.setOnAction(e -> setLeagueName());
	    
		addPlayerButton = new Button ("Add Player");
		addPlayerButton.setMinWidth(233.5);
		addPlayerButton.setOnAction(e -> addPlayer()); // set disabled if viewing roster
		
		dropPlayerButton = new Button("Drop Player");
		dropPlayerButton.setMinWidth(233.5);
		dropPlayerButton.setOnAction(e -> dropPlayer()); // set disabled if viewing free agents
	    
	    HBox exitAndSetLeagueNameAddDropButtons = new HBox(5, exitButton, setLeagueNameButton, addPlayerButton, dropPlayerButton);
	    borderPane.setBottom(exitAndSetLeagueNameAddDropButtons);
		
	    String stageTitle = "Fantasy Football League " + leagueName;
	    applicationStage = new Stage();
		applicationStage.setScene(new Scene(borderPane)); // Set window's scene
		applicationStage.setTitle(stageTitle); // Set window's title
		applicationStage.titleProperty().bind(leagueNameSPStageTitle); // bind title to StringProperty leagueNameSPStageTitle
	    leagueNameSPStageTitle.setValue(stageTitle);
	    applicationStage.setResizable(false);
		applicationStage.show(); // Display window

		if ((leagueName.isBlank()) || (leagueName == null)) {
			createNoLeagueAlert("Fantasy Football", "No league has been loaded.", "Please load or create a league.");
		}
	} // end method programStart
	
	// create labels with given parameter text and fontsize
	private Label createLabel(String labelText, int fontSize) {
		Label label = new Label(labelText);
		label.setFont(new Font(fontSize));
		return label;
	} // end createLabel
	
	// create textfields with given parameter promptext and minwidth
	private TextField createTextField(String promptText, int minWidth) {
		TextField textField = new TextField();
		textField.setMinWidth(minWidth);
		textField.setEditable(true);
		textField.setPromptText(promptText);
		textField.setFocusTraversable(false);
		return textField;
	}
	
	// Set up a GUI of the fantasy teams pane
	private Pane makeViewTeamsPane() {
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(10));
		pane.setStyle(BORDER_STYLE);
		
		// a label in the top region
		String viewTeamsLabelString = "Teams in League: " + leagueSize;
		Button viewTeamsButton = new Button(viewTeamsLabelString);
		viewTeamsButton.setMinWidth(460);
		viewTeamsButton.textProperty().bind(leagueSizeSP); // bind label text to leagueNameSP (stringproperty)
		leagueSizeSP.setValue(viewTeamsLabelString);
		pane.setTop(viewTeamsButton);
		
		// teams listView in center region. 
		fantasyTeamsListView = new ListView<String>();
		final int ROW_HEIGHT = 24;
		fantasyTeamsListView.setPrefHeight(4 * ROW_HEIGHT + 2);
		pane.setCenter(fantasyTeamsListView);
		fantasyTeamsListView.setMinWidth(460); // min width 460
		
		// create VBox with createTeam and viewLeagueRules buttons in bottom region
		createTeamButton = new Button("Join League");
		createTeamButton.setMinWidth(460);
	    createTeamButton.setOnAction(e -> createTeamWindow());
		//set disable if over 12 teams in league
	
		Button viewLeagueRulesButton = new Button("View League Rules");
		viewLeagueRulesButton.setMinWidth(460);
		viewLeagueRulesButton.setOnAction(e -> viewLeagueRules());
		
		VBox createTeamViewRules = new VBox(5, createTeamButton, viewLeagueRulesButton);
		
		pane.setBottom(createTeamViewRules);
		
		return pane;
	} // end method makeViewTeamsPane
	
	private void createTeamWindow() {
		// create new window to create new team
		BorderPane borderPane = new BorderPane();
		borderPane.setPadding(new Insets(5, 5, 5, 5)); // top, right, bottom, left
		
		// top region
		Label createTeamLabel = createLabel("Mossed: Create new team.", 25);
		borderPane.setTop(createTeamLabel);
		
		// center region
				// labels
		Label enterTeamNameLabel = createLabel("Enter Team Name: ", 20);
		Label enterTeamOwnerLabel = createLabel("Enter Your Name: ", 20);
		Label dateTeamCreatedLabel = createLabel("Team Created On: ", 20);
		VBox labels = new VBox(5, enterTeamNameLabel, enterTeamOwnerLabel, dateTeamCreatedLabel); //vbox for labels

				// textFields
		TextField teamNameField = createTextField("Enter team name here.", 225);
		TextField teamOwnerField = createTextField("Enter team owner's name here.", 225);
		
		TextField dateTeamCreatedField = new TextField();
		dateTeamCreatedField.setMinWidth(225);
		dateTeamCreatedField.setEditable(false);
		date = LocalDateTime.now();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = date.format(formatter);
		dateTeamCreatedField.setText(formatDateTime);
		dateTeamCreatedField.setFocusTraversable(false);
		
		VBox textFields = new VBox(5, teamNameField, teamOwnerField, dateTeamCreatedField); //vbox for textfields
		
		HBox labelsAndText = new HBox(5, labels, textFields); //hbox for vboxes
		borderPane.setCenter(labelsAndText);
		
		// bottom region
		Button createTeamButton = new Button("Create Team");
		createTeamButton.setDefaultButton(true);
		borderPane.setBottom(createTeamButton);
		
		createTeamStage = new Stage();
		createTeamStage.setScene(new Scene(borderPane, 638, 150));
		createTeamStage.setTitle("Create New Team");
		createTeamStage.setResizable(false);
		createTeamStage.toFront();
		createTeamStage.show();

// ------------ code for tab key pressed to traverse textfields -------------
		//ADD CODE TO TRAVERSE FROM FIELD TO FIELD ON TAB KEY PRESSED -- can this be put into method?
		// if enter key pressed while teamNameField toggled, traverse to next Field
		teamNameField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.TAB)) {
					teamOwnerField.requestFocus();
				}
			}
		}); // end method teamNameField tab key pressed
		
		// if enter key pressed while teamOwnerField toggled, traverse to next Field
		teamOwnerField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.TAB)) {
					createTeamButton.requestFocus();
				}
			}
		}); // end method teamOwnerField tab key pressed
		
		// if enter key pressed while createTeamButton toggled, traverse to next Field
		createTeamButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.TAB)) {
					teamNameField.requestFocus();
				}
			}
		}); // end method createTeamButton tab key pressed
// ------------ end code for tab key pressed to traverse textfields -------------	
		
		// check for exceptions and createTeam when createTeamButton pressed
		createTeamButton.addEventHandler(ActionEvent.ACTION, e -> {
			if ( (teamNameField.getText().isBlank()) && (teamOwnerField.getText().isBlank()) ) {
					createGenericAlert("Multiple Empty Fields", "Please Enter Valid Input Into All Fields.");
					return;
			}
			else if (teamNameField.getText().isBlank()) {
				createGenericAlert("No Team Name Entered", "Please Enter a Valid Team Name.");
				teamNameField.requestFocus();
				return;
			}
			else if (teamOwnerField.getText().isBlank()) {
				createGenericAlert("No Team Owner Specified", "Please Enter Team Owner's Name.");
				teamOwnerField.requestFocus();
				return;
			}
			
			String teamNameFieldStr = teamNameField.getText();
			String teamOwnerFieldStr = teamOwnerField.getText();
			String dateTeamCreatedFieldStr = dateTeamCreatedField.getText();
			
			// if league capacity reached, do not create team
			if (league.isLeagueFull()) {
				createGenericAlert("League is Full!", "Max Number of Teams Reached. Cannot Create Any More.");
				createTeamStage.close();
				return;
			}
			
			// check if team already exists in file - if yes, team already exists - must rename
		    boolean result = dataManager.doesTeamExist(teamNameFieldStr, this.league);
		    if (result == true) {
				createGenericAlert("Team Already Exists", "Please Enter a Different Team Name.");
				return;
		    }
			
			createTeam(teamNameFieldStr, teamOwnerFieldStr, dateTeamCreatedFieldStr);
			createTeamStage.close();
			
			// if league is full after team created, add message (League At Capacity)
		    if (league.isLeagueFull()) {
		    	String viewTeamsLabelString = "Teams in League: " + leagueSize + " (League At Capacity)";
		    	leagueSizeSP.setValue(viewTeamsLabelString);
		    }
			
			String alertFormat = String.format("Your team, " + teamNameFieldStr + ", has been created. "
					+ "Good luck.");
			Alert alert = new Alert(AlertType.INFORMATION, alertFormat);
			alert.setHeaderText("Congratulations!");
			alert.showAndWait();
		}); // end eventhandler
	} // end method createTeamWindow
	
	private void createTeam(String teamName, String teamOwner, String timeCreated) {
		FantasyTeam team = new FantasyTeam(teamName, teamOwner, timeCreated);
		league.addTeam(team);

		dataManager.writeFile(league); // write team to file

		leagueSize = String.valueOf(league.getTeamsInLeague().size());
		String viewTeamsLabelString = "Teams in League: " + leagueSize;
		leagueSizeSP.setValue(viewTeamsLabelString);
	    
	    initFantasyTeamsListView(); // initialize list view
	} // end method createTeam
	
	// Initialize view teams list view with data in league.getTeamsInLeague();
	private void initFantasyTeamsListView() {
		ArrayList<String> fantasyTeamsList = new ArrayList<>();
		for (FantasyTeam team : league.getTeamsInLeague()) {
			fantasyTeamsList.add("Team: " + team.getTeamName() + " | Owner: " + team.getTeamOwner() + 
					" | Created: " + team.getTimeCreated());
		}
		ObservableList<String> teams = FXCollections.observableArrayList(fantasyTeamsList);
		fantasyTeamsListView.setItems(teams);
	} // end method initFantasyTeamsListView
	
	private void setLeagueName() {
		// ADD CODE HERE
	}
	
	// Set up a GUI of the roster view pane
	private Pane makeRosterPane() {
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(10));
		pane.setStyle(BORDER_STYLE);
		
		// top region has a label
		String viewRosterButtonText;
		if (teamName == null) {
			teamName = "No Team Selected";
		}
		viewRosterButtonText = "Roster For Team: " + teamName; // add code here to connect fantasy team name (write "no team selected" if empty)
		Button viewRosterForTeam = new Button(viewRosterButtonText);
		viewRosterForTeam.setMinWidth(460);
		viewRosterForTeam.textProperty().bind(viewRosterForTeamSP);
		viewRosterForTeamSP.setValue(viewRosterButtonText);

		pane.setTop(viewRosterForTeam);
		
		// center region has roster listview. initially empty
		rosterListView = new ListView<String>();
		final int ROW_HEIGHT = 24;
		fantasyTeamsListView.setPrefHeight(4 * ROW_HEIGHT + 2);
		pane.setCenter(rosterListView);
		rosterListView.setMinWidth(460); // min width 460
		
		// bottom region has viewRoster, viewFreeAgents, addPlayer, dropPlayer buttons
		viewRosterButton = new Button("View Roster");
		viewRosterButton.setMinWidth(460);
		viewRosterButton.setOnAction(e -> viewRoster()); // set disabled if no team selected or create alert if no team selected
		
		viewFreeAgentsButton = new Button("View Free Agents");
		viewFreeAgentsButton.setMinWidth(460);
		viewFreeAgentsButton.setOnAction(e -> viewFreeAgents()); // create alert if already viewing free agents
		
		VBox buttonsUnderRosterViewPane = new VBox(5, viewRosterButton, viewFreeAgentsButton);
		pane.setBottom(buttonsUnderRosterViewPane);
		
		return pane;
	} // end method makeRosterPane
	
	// view team roster in rosterListView
	private void viewRoster() {
		viewFreeAgentsCounter = 0; // reset viewFreeAgentsCounter so user can navigate back and forth
								   // between view roster and view free agents
		
		int selectedTeamIndex = fantasyTeamsListView.getSelectionModel().getSelectedIndex();
		
		 // create rosterlist to load into listview
		rosterList = new ArrayList<String>();
		rosterPlayerList = new ArrayList<Player>(); // copy rosterlist into new arraylist, but sorted
		
		// add in sorted by position order
		for (Player player : league.getTeamsInLeague().get(selectedTeamIndex).getTeamRoster()) {
			if (player.getPosition().equals("QB")) {
				rosterList.add(player.toString());
				rosterPlayerList.add(player);
			}
		}
		for (Player player : league.getTeamsInLeague().get(selectedTeamIndex).getTeamRoster()) {
			if (player.getPosition().equals("RB")) {
				rosterList.add(player.toString());
				rosterPlayerList.add(player);
			}
		}
		for (Player player : league.getTeamsInLeague().get(selectedTeamIndex).getTeamRoster()) {
			if (player.getPosition().equals("WR")) {
				rosterList.add(player.toString());
				rosterPlayerList.add(player);
			}
		}
		for (Player player : league.getTeamsInLeague().get(selectedTeamIndex).getTeamRoster()) {
			if (player.getPosition().equals("TE")) {
				rosterList.add(player.toString());
				rosterPlayerList.add(player);
			}
		}
		for (Player player : league.getTeamsInLeague().get(selectedTeamIndex).getTeamRoster()) {
			if (player.getPosition().equals("DEFENSE")) {
				rosterList.add(player.toString());
				rosterPlayerList.add(player);
			}
		}
		for (Player player : league.getTeamsInLeague().get(selectedTeamIndex).getTeamRoster()) {
			if (player.getPosition().equals("K")) {
				rosterList.add(player.toString());
				rosterPlayerList.add(player);
			}
		}
		
		ObservableList<String> rosterObservableList = FXCollections.observableArrayList(rosterList);
		rosterListView.setItems(rosterObservableList);
		
		teamName = league.getTeamsInLeague().get(selectedTeamIndex).getTeamName();
		String viewRosterButtonText = "Roster For Team: " + teamName;
		viewRosterForTeamSP.setValue(viewRosterButtonText);
	} // end method viewRoster
	
	// view free agents in rosterListView
	private void viewFreeAgents() {
		// if already generated free agents list, do not generate again
		if (viewFreeAgentsCounter == 1) {
			createGenericAlert("Already Viewing Free Agents", "Please Continue.");
			return;
		}
		freeAgents.generateFreeAgents();
		
		// create freeAgentsList to load into listview
		freeAgentsList = new ArrayList<String>();
		freeAgentsPlayerList = new ArrayList<Player>(); // copy freeagents arraylist to this to sort players by position
		// add into freeAgentsList sorted by position order
		for (Player player : freeAgents.getFreeAgents()) {
			if (player.getPosition().equals("QB")) {
				freeAgentsList.add(player.toString());
				freeAgentsPlayerList.add(player);
			}
		}
		for (Player player : freeAgents.getFreeAgents()) {
			if (player.getPosition().equals("RB")) {
				freeAgentsList.add(player.toString());
				freeAgentsPlayerList.add(player);
			}
		}
		for (Player player : freeAgents.getFreeAgents()) {
			if (player.getPosition().equals("WR")) {
				freeAgentsList.add(player.toString());
				freeAgentsPlayerList.add(player);
			}
		}
		for (Player player : freeAgents.getFreeAgents()) {
			if (player.getPosition().equals("TE")) {
				freeAgentsList.add(player.toString());
				freeAgentsPlayerList.add(player);
			}
		}
		for (Player player : freeAgents.getFreeAgents()) {
			if (player.getPosition().equals("DEFENSE")) {
				freeAgentsList.add(player.toString());
				freeAgentsPlayerList.add(player);
			}
		}
		for (Player player : freeAgents.getFreeAgents()) {
			if (player.getPosition().equals("K")) {
				freeAgentsList.add(player.toString());
				freeAgentsPlayerList.add(player);
			}
		}

		ObservableList<String> freeAgentObservableList = FXCollections.observableArrayList(freeAgentsList);
		rosterListView.setItems(freeAgentObservableList);
		
		viewFreeAgentsCounter = 1; // set to 1 if viewFreeAgentsButton has been hit after running function,
								   // so alert will be generated if button is hit again
	} // end method viewFreeAgents
	
	// add player to team
	private void addPlayer() {
		if (viewFreeAgentsCounter == 0) {
			createGenericAlert("Viewing Team Roster", "Cannot Add Player To Team Player Is Already On.");
			return;
		}
		String selectedTeam = fantasyTeamsListView.getSelectionModel().getSelectedItem();
		int selectedTeamIndex = fantasyTeamsListView.getSelectionModel().getSelectedIndex();
		
		String selectedFreeAgent = rosterListView.getSelectionModel().getSelectedItem();
		int selectedFreeAgentIndex = rosterListView.getSelectionModel().getSelectedIndex();
		
	    if (selectedTeam == null) {
	    	createGenericAlert("No Team Selected", "Please Select Team To Add Player To.");
	    	return;
	    }
	    if (selectedFreeAgent == null) {
	    	createGenericAlert("No Player Selected", "Please Select Player To Add To Team.");
	    	return;
	    }
	    
	    // add selected free agent to selected team
	    for (int i = 0; i < freeAgents.getFreeAgents().size(); i++) {
	    	// does freeAgents list object in freeagents class match selected object on screen (if object is NOT defense)
	    	if ( (!freeAgentsPlayerList.get(selectedFreeAgentIndex).getPosition().equals("DEFENSE")) &&
	    		(freeAgents.getFreeAgents().get(i).getName().equals(freeAgentsPlayerList.get(selectedFreeAgentIndex).getName())) && 
	    		(freeAgents.getFreeAgents().get(i).getPosition().equals(freeAgentsPlayerList.get(selectedFreeAgentIndex).getPosition())) &&
	    		(freeAgents.getFreeAgents().get(i).getTeam().equals(freeAgentsPlayerList.get(selectedFreeAgentIndex).getTeam())) ) {
	    		
	    		// if position or roster limit has not been reached for a team: add player to team
	    		if (!league.getTeamsInLeague().get(selectedTeamIndex).isPositionOrRosterLimitReached(freeAgents.getFreeAgents().get(i))) {
		    	    league.getTeamsInLeague().get(selectedTeamIndex).addPlayer(freeAgents.getFreeAgents().get(i)); // add player
					dataManager.writeFile(league); // write to file
					
					// create playerAddedAlert
					int positionCount = getPositionCount(freeAgents.getFreeAgents().get(i), league.getTeamsInLeague().get(selectedTeamIndex));
					
					String msg = freeAgents.getFreeAgents().get(i).getName() + " Has Been Added To Team: " + league.getTeamsInLeague().get(selectedTeamIndex).getTeamName() 
							+ "\n" + freeAgents.getFreeAgents().get(i).getPosition() + "s Now On Team: " + String.valueOf(positionCount + 1);
					Alert playerAddedAlert = new Alert(AlertType.INFORMATION, msg);
					playerAddedAlert.setTitle("Player Added");
					playerAddedAlert.setHeaderText("Player Added");
					playerAddedAlert.showAndWait();
		    	    
		    	    freeAgentsList.remove(freeAgentsList.get(selectedFreeAgentIndex)); // remove player from free agents lists
		    	    freeAgentsPlayerList.remove(freeAgentsPlayerList.get(selectedFreeAgentIndex));
		    	    freeAgents.removeFreeAgents(i);
			    	
					ObservableList<String> freeAgentObservableList = FXCollections.observableArrayList(freeAgentsList);
					rosterListView.setItems(freeAgentObservableList);
					
					return;
	    		} // end if
	    		else {
	    			return;
	    		}
	    	} // end if
	    	
	    	// does freeAgents list object in freeagents class match selected object on screen (IF OBJECT IS DEFENSE)
	    	if ( (freeAgentsPlayerList.get(selectedFreeAgentIndex).getPosition().equals("DEFENSE")) &&
    		(freeAgents.getFreeAgents().get(i).getPosition().equals(freeAgentsPlayerList.get(selectedFreeAgentIndex).getPosition())) &&
    		(freeAgents.getFreeAgents().get(i).getTeam().equals(freeAgentsPlayerList.get(selectedFreeAgentIndex).getTeam())) ) {
	    		
	    		// if position or roster limit has not been reached for a team: add player to team
	    		if (!league.getTeamsInLeague().get(selectedTeamIndex).isPositionOrRosterLimitReached(freeAgents.getFreeAgents().get(i))) {
		    	    league.getTeamsInLeague().get(selectedTeamIndex).addPlayer(freeAgents.getFreeAgents().get(i)); // add player
					dataManager.writeFile(league); // write to file
					
					// create playerAddedAlert
					int positionCount = getPositionCount(freeAgents.getFreeAgents().get(i), league.getTeamsInLeague().get(selectedTeamIndex));
					
					String msg =  "Defense Has Been Added To Team: " + league.getTeamsInLeague().get(selectedTeamIndex).getTeamName() 
							+ "\n" + freeAgents.getFreeAgents().get(i).getPosition() + "s On Team: " + String.valueOf(positionCount + 1);
					Alert playerAddedAlert = new Alert(AlertType.INFORMATION, msg);
					playerAddedAlert.setTitle("Player Added");
					playerAddedAlert.setHeaderText("Player Added");
					playerAddedAlert.showAndWait();
		    	    
		    	    freeAgentsList.remove(freeAgentsList.get(selectedFreeAgentIndex)); // remove player from free agents lists
		    	    freeAgentsPlayerList.remove(freeAgentsPlayerList.get(selectedFreeAgentIndex));
		    	    freeAgents.removeFreeAgents(i);
			    	
					ObservableList<String> freeAgentObservableList = FXCollections.observableArrayList(freeAgentsList);
					rosterListView.setItems(freeAgentObservableList);
					
					return;
	    		} // end if
	    		else {
	    			return;
	    		}
	    	} // end if
   		
	    } // end for: iterate through freeagents list
	} // end method addPlayer()
	
	public int getPositionCount(Player player, FantasyTeam team) {
		int positionCount = 0;
		switch (player.getPosition()) {
		case "QB":
			positionCount = team.qbCount;
			break;
		case "RB":
			positionCount = team.rbCount;
			break;
		case "WR":
			positionCount = team.wrCount;
			break;
		case "TE":
			positionCount = team.teCount;
			break;
		case "DEFENSE":
			positionCount = team.defenseCount;
			break;
		case "K":
			positionCount = team.kCount;
			break;
		}
		return positionCount;
	} // end method getPositionCount
	
	
	// drop player from team
	private void dropPlayer() {
		if (viewFreeAgentsCounter == 1) {
			createGenericAlert("Viewing Free Agents", "Cannot Drop Player Who Is Not On A Team.");
			return;
		} // end if
		
		int selectedTeamIndex = fantasyTeamsListView.getSelectionModel().getSelectedIndex();
		int selectedPlayerIndex = rosterListView.getSelectionModel().getSelectedIndex();
		
		// remove player
		for (int i = 0; i < league.getTeamsInLeague().get(selectedTeamIndex).getTeamRoster().size(); i++) {
			if ( (league.getTeamsInLeague().get(selectedTeamIndex).getTeamRoster().get(i).getPosition().equals(rosterPlayerList.get(selectedPlayerIndex).getPosition())) &&
				(league.getTeamsInLeague().get(selectedTeamIndex).getTeamRoster().get(i).getName().equals(rosterPlayerList.get(selectedPlayerIndex).getName())) &&
				(league.getTeamsInLeague().get(selectedTeamIndex).getTeamRoster().get(i).getTeam().equals(rosterPlayerList.get(selectedPlayerIndex).getTeam())) ) {
				
				// create playerDroppedAlert
				int positionCount = getPositionCount(league.getTeamsInLeague().get(selectedTeamIndex).getTeamRoster().get(i), league.getTeamsInLeague().get(selectedTeamIndex));
				
				String msg = league.getTeamsInLeague().get(selectedTeamIndex).getTeamRoster().get(i).getName() + " Has Been Dropped From Team: " + league.getTeamsInLeague().get(selectedTeamIndex).getTeamName() 
						+ "\n" + league.getTeamsInLeague().get(selectedTeamIndex).getTeamRoster().get(i).getPosition() + "s Now On Team: " + String.valueOf(positionCount-1);
				Alert playerAddedAlert = new Alert(AlertType.INFORMATION, msg);
				playerAddedAlert.setTitle("Player Dropped");
				playerAddedAlert.setHeaderText("Player Dropped");
				playerAddedAlert.showAndWait();
				
				league.getTeamsInLeague().get(selectedTeamIndex).getTeamRoster().remove(i); // remove player
				rosterList.remove(selectedPlayerIndex);
				rosterPlayerList.remove(selectedPlayerIndex);
				
				dataManager.writeFile(league); // write to file
				
				ObservableList<String> rosterObservableList = FXCollections.observableArrayList(rosterList);
				rosterListView.setItems(rosterObservableList);
			}
		}
	} // end method dropPlayer
	
	private void createNoLeagueAlert(String title, String contextMsg, String msg) {
		Alert alert = new Alert(AlertType.INFORMATION, msg);
		alert.setTitle(title);
		alert.setHeaderText(contextMsg);
		
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			// create new window with create and load league buttons
			BorderPane borderPane = new BorderPane();
			borderPane.setPadding(new Insets(5, 50, 5, 50)); // top, right, bottom, left
			
			// create buttons for window
			Button createLeagueButton = new Button("Create New League");
			
			Button loadLeagueButton = new Button("Load League");
			
			VBox leagueButtons = new VBox(5, createLeagueButton, loadLeagueButton);
			borderPane.setCenter(leagueButtons);

			createOrLoadLeagueStage = new Stage();
			createOrLoadLeagueStage.setScene(new Scene(borderPane, 225, 70));
			createOrLoadLeagueStage.setTitle("Create or Load League");
			createOrLoadLeagueStage.setResizable(false);
			createOrLoadLeagueStage.setAlwaysOnTop(true);
			createOrLoadLeagueStage.show();

			// CAN SOME OF THIS CODE BE PUT IN METHOD
			// if enter key pressed while createLeagueButton toggled, initiate createLeagueWindow
			// and close current window
			createLeagueButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode().equals(KeyCode.ENTER)) {
						createLeagueWindow();
						createOrLoadLeagueStage.close();
					}
				}
			}); // end method
			
			// if enter key pressed while loadLeagueButton toggled, initiate createLoadWindow 
			// and close current window
			loadLeagueButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode().equals(KeyCode.ENTER)) {
						createLoadWindow();
						createOrLoadLeagueStage.close();
					}
				}
			}); // end method
			
			// createLeagueWindow opens and current window closes when this button pressed
			createLeagueButton.addEventHandler(ActionEvent.ACTION, e -> {
				createLeagueWindow();
				createOrLoadLeagueStage.close();
			});
			
			// loadLeagueWindow opens and current window closes when this button pressed
			loadLeagueButton.addEventHandler(ActionEvent.ACTION, e -> {
				createLoadWindow();
				createOrLoadLeagueStage.close();
			}); // end eventhandler
		} // end if: ButtonType is OK
	} // end method createNoLeagueAlert
	
	public static Alert createGenericAlert(String contextMsg, String msg) {
		Alert alert = new Alert(AlertType.ERROR, msg);
		alert.setHeaderText(contextMsg);
		alert.showAndWait();
		
		return alert;
	} // end method createGenericAlert
	
	private void createLeagueWindow() {
		// create new window to create new league
		BorderPane borderPane = new BorderPane();
		borderPane.setPadding(new Insets(5, 5, 5, 5)); // top, right, bottom, left
		
		// top region
		Label createLeagueLabel = createLabel("Welcome! Create your fantasy league here.", 25);
		borderPane.setTop(createLeagueLabel);
		
		// center region
				// labels
		Label enterLeagueNameLabel = createLabel("Enter League Name: ", 20);
		Label enterLeagueOwnerLabel = createLabel("Enter Your Name: ", 20);
		Label maxNumberTeamsLabel = createLabel("Enter the maximum allowable number of teams: ", 20);
		VBox labels = new VBox(5, enterLeagueNameLabel, enterLeagueOwnerLabel, maxNumberTeamsLabel); //vbox for labels

				// textFields
		TextField leagueNameField = createTextField("Enter league name here.", 225);
		TextField leagueOwnerField = createTextField("Enter league owner's name here.", 225);
		TextField maxTeamsField = createTextField("Enter maximum number of teams here.", 225);

		
		VBox textFields = new VBox(5, leagueNameField, leagueOwnerField, maxTeamsField); //vbox for textfields
		
		HBox labelsAndText = new HBox(5, labels, textFields); //hbox for vboxes
		borderPane.setCenter(labelsAndText);
		
		// bottom region
		Button createLeagueButton = new Button("Create League");
		createLeagueButton.setDefaultButton(true);
		borderPane.setBottom(createLeagueButton);
		
		createLeagueStage = new Stage();
		createLeagueStage.setScene(new Scene(borderPane, 638, 150));
		createLeagueStage.setTitle("Create New League");
		createLeagueStage.setResizable(false);
		createLeagueStage.toFront();
		createLeagueStage.show();
		
// ------------ code for tab key pressed to traverse textfields -------------
		//ADD CODE TO TRAVERSE FROM FIELD TO FIELD ON TAB KEY PRESSED - CAN THIS CODE BE PUT IN METHOD?
		// if enter key pressed while leagueNameField toggled, traverse to next Field
			leagueNameField.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode().equals(KeyCode.TAB)) {
						leagueOwnerField.requestFocus();
					}
				}
			}); // end method leagueNameField tab key pressed
			
		// if enter key pressed while leagueOwnerField toggled, traverse to next Field
			leagueOwnerField.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode().equals(KeyCode.TAB)) {
						maxTeamsField.requestFocus();
					}
				}
			}); // end method leagueOwnerField tab key pressed
				
		// if enter key pressed while maxTeamsField toggled, traverse to next Field
		maxTeamsField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.TAB)) {
					createLeagueButton.requestFocus();
				}
			}
		}); // end method maxTeamsField tab key pressed
		
		// if enter key pressed while createLeagueButton toggled, traverse to next Field
		createLeagueButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.TAB)) {
					leagueNameField.requestFocus();
				}
			}
		}); // end method createLeagueButton tab key pressed
// ------------ end code for tab key pressed to traverse textfields -------------
		
		// league created when createLeagueButton is button pressed
		createLeagueButton.addEventHandler(ActionEvent.ACTION, e -> {
			if ( (leagueNameField.getText().isBlank()) && (leagueOwnerField.getText().isBlank()) &&
				(maxTeamsField.getText().isBlank()) ) {
				createGenericAlert("Multiple Empty Fields", "Please Enter Valid Input Into All Fields.");
				return;
			}
			else if ( (leagueNameField.getText().isBlank()) && (leagueOwnerField.getText().isBlank()) ) {
				createGenericAlert("Multiple Empty Fields", "Please Enter Valid Input Into All Fields.");
				return;
			}
			else if ( (leagueNameField.getText().isBlank()) && (maxTeamsField.getText().isBlank()) ) {
				createGenericAlert("Multiple Empty Fields", "Please Enter Valid Input Into All Fields.");
				return;
			}
			else if ( (leagueOwnerField.getText().isBlank()) && (maxTeamsField.getText().isBlank()) ) {
				createGenericAlert("Multiple Empty Fields", "Please Enter Valid Input Into All Fields.");
				return;
			}
			else if (leagueNameField.getText().isBlank()) {
				createGenericAlert("No League Name Entered", "Please Enter a Valid League Name.");
				leagueNameField.requestFocus();
				return;
			}
			else if (leagueOwnerField.getText().isBlank()) {
				createGenericAlert("No League Owner Specified", "Please Enter League Owner's Name.");
				leagueOwnerField.requestFocus();
				return;
			}
			else if (maxTeamsField.getText().isBlank()) {
				createGenericAlert("No Max Teams Specified", "Please Enter a Valid Max Number of Teams.");
				maxTeamsField.requestFocus();
				return;
			}
			else if (!maxTeamsField.getText().isBlank()) {
				String maxTeamsFieldStr = maxTeamsField.getText();
				boolean result = false;
				for (int i = 0; i < maxTeamsFieldStr.length(); i++) {
					if (!Character.isDigit(maxTeamsFieldStr.charAt(i))) {
						result = true;
					}
				}
				if (result) {
					createGenericAlert("Letters Not Allowed", "Please Enter a Valid Positive Max Number of Teams.");
					maxTeamsField.requestFocus();
					maxTeamsField.selectAll();
					return;
				}
				if (Integer.parseInt(maxTeamsFieldStr) > 12) {
					createGenericAlert("Max Team Limit Exceeded", "League Cannot Contain More Than 12 Teams.\n"
							+ "Please Enter a Value No Greater Than 12.");
					maxTeamsField.requestFocus();
					maxTeamsField.selectAll();
					return;
				}
			}
			String leagueNameFieldStr = leagueNameField.getText();
			String leagueOwnerFieldStr = leagueOwnerField.getText();
			Integer maxTeamsFieldInt = Integer.parseInt(maxTeamsField.getText());
			int maxTeams = maxTeamsFieldInt;
			
			// check if file already exists for league - if yes, then league already exists - must rename
		    boolean result = dataManager.doesFileExist(leagueNameFieldStr);
		    if (result == true) {
				createGenericAlert("League Already Exists", "Please Enter a Different League Name.");
				return;
		    }
			
			createLeague(leagueNameFieldStr, leagueOwnerFieldStr, maxTeams);
			createLeagueStage.close();
			
			String alertFormat = String.format("Your league, " + leagueNameFieldStr + ", has been created. "
					+ "Best of luck to you.");
			Alert alert = new Alert(AlertType.INFORMATION, alertFormat);
			alert.setHeaderText("Congratulations!");
			alert.showAndWait();
		}); // end eventhandler
	} // end method createLeagueWindow
	
	private League createLeague(String leagueName, String leagueOwner, int maxTeams) {
		League league = new League(leagueName, leagueOwner, maxTeams);
		
		this.league = league; // tie this GUI to this league created
	
		this.leagueName = league.getLeagueName(); // set name of league,owner,maxteams
		this.leagueOwner = league.getLeagueOwner();
		this.maxTeams = league.getMaxTeams();
		
		String viewTeamsLabelString = "Teams in League: " + leagueSize;
		leagueSizeSP.setValue(viewTeamsLabelString);
		
	    String stageTitle = "Fantasy Football League: " + leagueName + " | Owner: " + leagueOwner
	    		+ " | Max Teams: " + maxTeams;
	    leagueNameSPStageTitle.setValue(stageTitle);
	    

	    	// create a new file for the league and write to league
	    	dataManager.createFile(league);
	    	dataManager.writeFile(league);
	
    	return league;
	} // end method createLeague
	
	// to load a league into window
	private void createLoadWindow() {
		// create new window to create new league
		BorderPane borderPane = new BorderPane();
		borderPane.setPadding(new Insets(5, 5, 5, 5)); // top, right, bottom, left
		
		// top region
		Label createLeagueLabel = createLabel("Welcome! Load your league.", 25);
		borderPane.setTop(createLeagueLabel);
		
		// center region contains listview of saved leagues
			// initialize list view
		leaguesListView = new ListView<String>();
		final int ROW_HEIGHT = 24;
		leaguesListView.setPrefHeight(4 * ROW_HEIGHT + 2);
		borderPane.setCenter(leaguesListView);
		leaguesListView.setMinWidth(460); // min width 460
		
			// create leaguesList to load into listview
		ArrayList<String> leaguesList = dataManager.loadLeagueWindowData();
		ObservableList<String> leagues = FXCollections.observableArrayList(leaguesList);
		leaguesListView.setItems(leagues);
		
		// bottom region
		Button loadLeagueButton = new Button("Load League");
		loadLeagueButton.setDefaultButton(true);
		borderPane.setBottom(loadLeagueButton);
		
		loadWindowStage = new Stage();
		loadWindowStage.setScene(new Scene(borderPane, 470, 150));
		loadWindowStage.setTitle("Load Your League");
		loadWindowStage.setResizable(false);
		loadWindowStage.toFront();
		loadWindowStage.show();
		
		// league loaded when loadLeagueButton is pressed
		loadLeagueButton.addEventHandler(ActionEvent.ACTION, e -> {
			if (leaguesList.isEmpty()) {
				createGenericAlert("No Leagues Saved.", "Please Restart Program and Create a League To Continue.");
				return;
			}
			
		    String selectedItem = leaguesListView.getSelectionModel().getSelectedItem();
		    if (selectedItem == null) {
		    	createGenericAlert("No League Selected", "Please Select a League From the List to Load League.");
		    	return;
		    }
		    loadLeague(selectedItem);
		    loadWindowStage.close();
		}); // end eventhandler
	} // end method createLoadWindow
	
	private void loadLeague(String leagueName) {
		String leagueFileName = leagueName + ".csv";
		try (Scanner fileIn = new Scanner(Path.of(leagueFileName))) {
			// 1st row is league info -- create league using this info
			String record = fileIn.nextLine();
			String[] cols = record.split(","); // split a comma separated line
			String leagueNameField = cols[0].trim();
			String leagueOwnerField = cols[1].trim();
			int maxTeamsField = Integer.parseInt(cols[2].trim());
			createLeague(leagueNameField, leagueOwnerField, maxTeamsField); // create league
			
			// rows after this contain teams in league and players - create teams using this info
			while (fileIn.hasNextLine()) {
				String teamsAndPlayers = fileIn.nextLine();
				String[] teamsAndPlayersCols = teamsAndPlayers.split(",");
				
				// add teams to league
				if (!teamsAndPlayersCols[0].trim().equals("Position")) {
					String teamNameField = teamsAndPlayersCols[0].trim();
					String teamOwnerField = teamsAndPlayersCols[1].trim();
					String timeCreatedField = teamsAndPlayersCols[2].trim();
					createTeam(teamNameField, teamOwnerField, timeCreatedField);
				} // end if
				
				// add players to team
				else if (teamsAndPlayersCols[0].trim().equals("Position")) {
					String playerPositionField = teamsAndPlayersCols[1].trim();
					String playerNameField = teamsAndPlayersCols[2].trim();
					String playerTeamField = teamsAndPlayersCols[3].trim();
					// if player is not defense, use three parameter constructor
					if (!playerNameField.equals("Null")) {
						Player player = new Player(playerPositionField, playerNameField, playerTeamField);
						league.getTeamsInLeague().get(league.getTeamsInLeague().size() - 1).addPlayer(player);
					}
					// if player is defense, use two parameter constructor
					if (playerNameField.equals("Null")) {
						Player player = new Player(playerPositionField, playerTeamField);
						league.getTeamsInLeague().get(league.getTeamsInLeague().size() - 1).addPlayer(player);
					}
				} // end else if
			} // end while
			
			fileIn.close(); // close the file
		} // end try
		
		catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		
	} // end method loadLeague
	
	private void viewLeagueRules() {
		// create new window to create new league
		BorderPane borderPane = new BorderPane();
		borderPane.setPadding(new Insets(5, 5, 5, 5)); // top, right, bottom, left
		
		// top region
		String leagueRulesHeaderLabelStr = "View/Edit Your League Rules.";
		Label leagueRulesHeaderLabel = createLabel(leagueRulesHeaderLabelStr, 20);
		leagueRulesHeaderLabel.setPadding(new Insets(0, 0, 25, 0));
		borderPane.setTop(leagueRulesHeaderLabel);
		
		// left region
			// create label for each league rule
		String maxTeamsLabelStr = "Max Teams in League: ";
		Label maxTeamsLabel = createLabel(maxTeamsLabelStr, 16);
		
		String maxPositionsLabelStr = "Max Players By Position: ";
		Label maxPositionsLabel = createLabel(maxPositionsLabelStr, 16);
		
		Label qbLabel = createLabel("	QB", 14);
		Label rbLabel = createLabel("	RB", 14);
		Label wrLabel = createLabel("	WR", 14);
		Label teLabel = createLabel("	TE", 14);
		Label defenseLabel = createLabel("	DEFENSE", 14);
		Label kLabel = createLabel("	K", 14);
		
		VBox maxPositionsVBox = new VBox(5, maxTeamsLabel, maxPositionsLabel, qbLabel, rbLabel, wrLabel, teLabel, defenseLabel, kLabel);
		borderPane.setLeft(maxPositionsVBox);
		
		leagueRulesStage = new Stage();
		leagueRulesStage.setScene(new Scene(borderPane, 470, 470));
		leagueRulesStage.setTitle("View/Edit League Rules");
		leagueRulesStage.setResizable(false);
		leagueRulesStage.show();
	} // end method viewLeagueRules
	
	public static void main(String[] args) {
		launch(args);
	} // end main

} // end class FantasyFootballGUI
