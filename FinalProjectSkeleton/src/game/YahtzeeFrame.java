package game;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import server.SaveServer;

public class YahtzeeFrame extends JFrame {

	private JPanel topPlayerNamePanel, dicePanel, yahtzeeScorePanel, yahtzeeGameUpperSectionPanel, yahtzeeGameLowerSectionPanel;
	//private JLabel playerNameLabel, scoreSubtotalLabel, bonusLabel, grandTotalLabel;
	private JTextField acesField, twosField, threesField, foursField, fivesField, sixesField, scoreSubtotalField, bonusField, grandTotalField, threeOfAKindField, fourOfAKindField, fullHouseField, smallStraightField, largeStraightField, yahtzeeField, chanceField, yahtzeeBonusField, totalOfLowerSectionField, grandTotalField2;
	private int roll=0, round = 1, totalNumberOfPlayers, playerId, currentPlayerId=0;
	private boolean isSectionSet, isYahtzee;
	private String[] imagesPaths = {"die1.png","die2.png","die3.png","die4.png","die5.png","die6.png"};
	private int imgValue;
	private ArrayList<String> playerNames;
	private static final int MAX_ROUNDS = 13;
	private Player player1, player2, player3, player4;
	private ArrayList<Integer> dieValues;
	private ArrayList<Player> players;
	private ImagePanel imagePanel1, imagePanel2, imagePanel3, imagePanel4, imagePanel5; 
	JLabel rollLabel, roundLabel, playerNameLabel2, scoreSubtotalLabel, bonusLabel, grandTotalLabel;
	JCheckBox c1, c2, c3, c4, c5;
	
	public YahtzeeFrame() {
		
		// Welcome message
		new welcomeToYahtzeePane();
		
		// Number of players
		new enterNumberOfPlayersPane();
		// Enter names 
		playerNames = new ArrayList<String>();
		players = new ArrayList<Player>();
		new enterPlayerNamesPane();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400,850);
		setResizable(false);
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// Menu bar
		createMenus();
		
		//panel at the top
		topPlayerNamePanel = new JPanel();
		topPlayerNamePanel.setLayout(new GridBagLayout());
		//there should be objects in the top panel
		JLabel playerNameLabel = new JLabel("Current Player: ");
		playerNameLabel2 = new JLabel();
		playerNameLabel2.setText(getCurrentPlayerName());
		
		c.gridx = 0;
	    c.gridy = 0;
		topPlayerNamePanel.add(playerNameLabel,c);
		c.gridx = 1;
	    c.gridy = 0;
		topPlayerNamePanel.add(playerNameLabel2,c);
		c.gridx = 0;
	    c.gridy = 0;
	    c.fill = GridBagConstraints.HORIZONTAL;
		this.add(topPlayerNamePanel, c);
		
		// Dice Panel
		dicePanel = new JPanel();
		addComponentsToDicePanel(dicePanel);
        c.gridx = 0;
	    c.gridy = 1;
	    c.fill = GridBagConstraints.HORIZONTAL;
        this.add(dicePanel, c);
        dieValues = new ArrayList<Integer>();
        for (int i=0;i<5;i++) {
        	dieValues.add(0);
        }
               
        // yahtzeeScorePanel contains the Upper and Lower sections
        yahtzeeScorePanel = new JPanel();
        yahtzeeScorePanel.setLayout(new BoxLayout(yahtzeeScorePanel, BoxLayout.Y_AXIS));
        // Yahtzee Upper Section Panel
        yahtzeeGameUpperSectionPanel = new JPanel();
        addComponentsToYahtzeeUppersectionPanel(yahtzeeGameUpperSectionPanel);
        yahtzeeScorePanel.add(yahtzeeGameUpperSectionPanel);
        // Yahtzee Lower Section Panel
        yahtzeeGameLowerSectionPanel = new JPanel();
        addComponentsToyahtzeeGameLowerSectionPanel(yahtzeeGameLowerSectionPanel);
	    yahtzeeScorePanel.add(yahtzeeGameLowerSectionPanel);
	    c.gridx = 0;
	    c.gridy = 2;
	    c.fill = GridBagConstraints.HORIZONTAL;
	    this.add(yahtzeeScorePanel,c);
	    
	    // Panel for Submit button: this changes the player
	    JPanel submitPanel = new JPanel();
	    JButton submitButton = new JButton("Submit");
	    submitPanel.add(submitButton);
	    c.gridx = 0;
	    c.gridy = 3;
	    this.add(submitPanel,c); 
	    submitButton.addActionListener(new ActionListener(){
		    public void actionPerformed(ActionEvent e){
		    	if (isSectionSet==false ) {
		    		// Round is not over
		    		new RoundIsNotOverError();
		    	} else {
		    		roll = 0;
		    		isYahtzee = false;
		    		rollLabel.setText(getRoll());
			    	clearBoard();
			    	isSectionSet = false;
			    	//savePlayerScores(players.get(currentPlayerId));
			    	System.out.println("Submit Button clicked");
			    	if (currentPlayerId < totalNumberOfPlayers-1) {
			    		currentPlayerId ++;
			    	} else {
			    		currentPlayerId = 0;
			    		round ++;
			    		players.get(currentPlayerId).setRound(round);
			    	}	    
			    	updateBoard(players.get(currentPlayerId));
			    	playerNameLabel2.setText(getCurrentPlayerName());
			    	roundLabel.setText(getRound());
		    	}
		    }

		});
	}
	
	public JMenuItem createFileExitItem() {
 		JMenuItem item = new JMenuItem("Exit");      
 		class MenuItemListener implements ActionListener
 		{
 			public void actionPerformed(ActionEvent event)
 			{
 				System.exit(0);
 			}	
 		}      
 		ActionListener listener = new MenuItemListener();
 		item.addActionListener(listener);
 		return item;
 	}
	
	public void clearBoard() {
		c1.setSelected(false);
		c2.setSelected(false);
		c3.setSelected(false);
		c4.setSelected(false);
		c5.setSelected(false);
		c1.setEnabled(false);
	    c2.setEnabled(false);
	    c3.setEnabled(false);
	    c4.setEnabled(false);
	    c5.setEnabled(false);
		acesField.setText("");
		twosField.setText("");
		threesField.setText("");
		foursField.setText("");
		fivesField.setText("");
		sixesField.setText("");
		scoreSubtotalField.setText(""); 
		bonusField.setText(""); 
		grandTotalField.setText("");
		threeOfAKindField.setText("");
		fourOfAKindField.setText("");
		fullHouseField.setText("");
		smallStraightField.setText("");
		largeStraightField.setText("");
		yahtzeeField.setText("");
		chanceField.setText("");
		yahtzeeBonusField.setText(""); 
		totalOfLowerSectionField.setText(""); 
		grandTotalField2.setText("");
	}
	
	public void updateBoard(Player p) {
		resetDies();
		if(p.isAcesLocked()) {
			acesField.setText(String.valueOf(p.getAces()));
		}
		if (p.isTwosLocked()) {
			twosField.setText(String.valueOf(p.getTwos()));
		}
		if (p.isThreesLocked()) {
			threesField.setText(String.valueOf(p.getThrees()));
		}
		if (p.isFoursLocked()) {
			foursField.setText(String.valueOf(p.getFours()));
		}
		if (p.isFivesLocked()) {
			fivesField.setText(String.valueOf(p.getFives()));
		}
		if (p.isSixesLocked()) {
			sixesField.setText(String.valueOf(p.getSixes()));
		}
		if (p.isThreeOfAKindLocked()) {
			threeOfAKindField.setText(String.valueOf(p.getThreeOfAKind()));
		}
		if (p.isFourOfAKindLocked()) {
			fourOfAKindField.setText(String.valueOf(p.getFourOfAKind()));
		}
		if (p.isFullHouseLocked()) {
			fullHouseField.setText(String.valueOf(p.getFullHouse()));
		}
		if (p.isSmallStraightLocked()) {
			smallStraightField.setText(String.valueOf(p.getSmallStraight()));
		}
		if (p.isLargeStraightLocked()) {
			largeStraightField.setText(String.valueOf(p.getLargeStraight()));
		}
		if (p.isYahtzeeLocked()) {
			yahtzeeField.setText(String.valueOf(p.getYahtzee()));
		}
		if (p.isChanceLocked()) {
			chanceField.setText(String.valueOf(p.getChance()));
		}
		updateUpperBonusAndTotal();
		updateLowerBonusAndTotal();		
	}
	
	public void resetDies() {
		imagePanel1.setImage(imagesPaths[0]);
		imagePanel1.scaleImage(0.4);
		imagePanel2.setImage(imagesPaths[0]);
		imagePanel2.scaleImage(0.4);
		imagePanel3.setImage(imagesPaths[0]);
		imagePanel3.scaleImage(0.4);
		imagePanel4.setImage(imagesPaths[0]);
		imagePanel4.scaleImage(0.4);
		imagePanel5.setImage(imagesPaths[0]);
		imagePanel5.scaleImage(0.4);	
	}

	public class welcomeToYahtzeePane {  
		JFrame f;  
		welcomeToYahtzeePane(){  
		    f = new JFrame();  
		    JOptionPane.showMessageDialog(f,"Welcome! Let's play Yahtzee");     
		}
	}
	
	public class TryAgain {  
		JFrame f;  
		TryAgain(){  
		    f=new JFrame();  
		    JOptionPane.showMessageDialog(f,"Sorry Game already exits, try Again with another name.","Alert",JOptionPane.WARNING_MESSAGE);     
		}
	}
	
	public class MaximumRollsReached {  
		JFrame f;  
		MaximumRollsReached(){  
		    f=new JFrame();  
		    JOptionPane.showMessageDialog(f,"Maximum Rolls limit has been reached. If done updating the scores, please press Submit.","Alert",JOptionPane.WARNING_MESSAGE);     
		}
	}
	
	public class RoundIsNotOverError {  
		JFrame f;  
		RoundIsNotOverError(){  
		    f=new JFrame();  
		    JOptionPane.showMessageDialog(f,"The Round is not completed. Please roll the dice atleast once and fill atleast a field in either section..","Alert",JOptionPane.WARNING_MESSAGE);     
		}
	}
	
	public class ItIsYahtzee {  
		JFrame f;  
		ItIsYahtzee(){  
		    f=new JFrame();  
		    JOptionPane.showMessageDialog(f,"Yahtzee!\n\nForced Joker rules:\n	1.If the corresponding Upper Section box is unused then that category must be used.\n	2.If the corresponding Upper Section box has been used already, a Lower Section box must be used.\nThe Yahtzee acts as a Joker so that the Full House, Small Straight and Large Straight categories can be used to score 25, 30 or 40 (respectively).\n	3.If the corresponding Upper Section box and all Lower Section boxes have been used, an unused Upper Section box must be used, scoring 0.","Alert",JOptionPane.WARNING_MESSAGE);     
		}
	}
	
	public class FieldAlreadySet {  
		JFrame f;  
		FieldAlreadySet(){  
		    f=new JFrame();  
		    JOptionPane.showMessageDialog(f,"The score can be set only once.","Alert",JOptionPane.WARNING_MESSAGE);     
		}
	}
	
	public class NotAnYahtzee {  
		JFrame f;  
		NotAnYahtzee(){  
		    f=new JFrame();  
		    JOptionPane.showMessageDialog(f,"Sorry this is not an Yahtzee!","Alert",JOptionPane.WARNING_MESSAGE);     
		}
	}

	public class zeroRollsError {  
		JFrame f;  
		zeroRollsError(){  
		    f=new JFrame();  
		    JOptionPane.showMessageDialog(f,"The dice must be rolled atleast once!","Alert",JOptionPane.WARNING_MESSAGE);     
		}
	}
	
	public class ScoreSubmittedCannotRoll {  
		JFrame f;  
		ScoreSubmittedCannotRoll(){  
		    f=new JFrame();  
		    JOptionPane.showMessageDialog(f,"The score is already submitted. Cannot roll now! \nPress Submit button to continue.","Alert",JOptionPane.WARNING_MESSAGE);     
		}
	}
	
	public class enterNumberOfPlayersPane {  
		JFrame f;  
		enterNumberOfPlayersPane(){  
		    f = new JFrame(); 
		    String[] options = {"1", "2", "3", "4"};
		    totalNumberOfPlayers = JOptionPane.showOptionDialog(f, "How many peeps are playing Yahtzee today? ",
	                "Choose one",
	                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, null) + 1;
		    //totalNumberOfPlayers = Integer.parseInt(JOptionPane.showInputDialog(f,"How many peeps are playing today? "));
		    System.out.println("Total Number of pLayers are: " + totalNumberOfPlayers);
		}
	}
	
	public class enterPlayerNamesPane {  
		JFrame f;  
		enterPlayerNamesPane(){  
		    f = new JFrame();
		    switch(totalNumberOfPlayers) {
		    case 1:   
		    	playerNames.add(JOptionPane.showInputDialog(f,"Enter your name player: "));
		    	player1 = new Player(playerNames.get(0));
		    	players.add(player1);
		    	break;
		    case 2:
		    	playerNames.add(JOptionPane.showInputDialog(f,"Enter your name player 1: "));
		    	playerNames.add(JOptionPane.showInputDialog(f,"Enter your name player 2: "));
		    	player1 = new Player(playerNames.get(0));
		    	player2 = new Player(playerNames.get(1));
		    	players.add(player1);
		    	players.add(player2);
		    	break;
		    case 3: 
		    	playerNames.add(JOptionPane.showInputDialog(f,"Enter your name player 1: "));
		    	playerNames.add(JOptionPane.showInputDialog(f,"Enter your name player 2: "));
		    	playerNames.add(JOptionPane.showInputDialog(f,"Enter your name player 3: "));
		    	player1 = new Player(playerNames.get(0));
		    	player2 = new Player(playerNames.get(1));
		    	player3 = new Player(playerNames.get(2));
		    	players.add(player1);
		    	players.add(player2);
		    	players.add(player3);
		    	break;
		    case 4: 
		    	playerNames.add(JOptionPane.showInputDialog(f,"Enter your name player 1: "));
		    	playerNames.add(JOptionPane.showInputDialog(f,"Enter your name player 2: "));
		    	playerNames.add(JOptionPane.showInputDialog(f,"Enter your name player 3: "));
		    	playerNames.add(JOptionPane.showInputDialog(f,"Enter your name player 4: "));
		    	player1 = new Player(playerNames.get(0));
		    	player2 = new Player(playerNames.get(1));
		    	player3 = new Player(playerNames.get(2));
		    	player4 = new Player(playerNames.get(3));
		    	players.add(player1);
		    	players.add(player2);
		    	players.add(player3);
		    	players.add(player4);
		    	break;
		    }
		}
	}

	public void addComponentsToyahtzeeGameLowerSectionPanel(JPanel panel) {
		panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
		Border borderLower = BorderFactory.createTitledBorder("Lower Section");
        panel.setBorder(borderLower);
        final int FIELD_WIDTH = 8;
        // Fields
        // 3 of a kind
        JButton threeOfAKindButton = new JButton("3 of a Kind");
        threeOfAKindField = new JTextField(FIELD_WIDTH);
        threeOfAKindField.setText("");
        c.gridx = 0;
	    c.gridy = 0;
	    panel.add(threeOfAKindButton,c);
        c.gridx = 1;
	    c.gridy = 0;
	    panel.add(threeOfAKindField, c);
        // 4 of a Kind
        JButton fourOfAKindButton = new JButton("4 of a Kind");
        fourOfAKindField = new JTextField(FIELD_WIDTH);
        fourOfAKindField.setText("");
        c.gridx = 0;
	    c.gridy = 1;
	    panel.add(fourOfAKindButton,c);
        c.gridx = 1;
	    c.gridy = 1;
	    panel.add(fourOfAKindField, c);
        // Full House
        JButton fullHouseButton = new JButton("Full House");
        fullHouseField = new JTextField(FIELD_WIDTH);
        fullHouseField.setText("");
        c.gridx = 0;
	    c.gridy = 2;
	    panel.add(fullHouseButton,c);
        c.gridx = 1;
	    c.gridy = 2;
	    panel.add(fullHouseField, c);
        // Small Straight
        JButton smallStraightButton = new JButton("Small Straight");
        smallStraightField = new JTextField(FIELD_WIDTH);
        smallStraightField.setText("");
        c.gridx = 0;
	    c.gridy = 3;
	    panel.add(smallStraightButton,c);
        c.gridx = 1;
	    c.gridy = 3;
	    panel.add(smallStraightField, c);
        // Large Straight
        JButton largeStraightButton = new JButton("Large Straight");
        largeStraightField = new JTextField(FIELD_WIDTH);
        largeStraightField.setText("");
        c.gridx = 0;
	    c.gridy = 4;
	    panel.add(largeStraightButton,c);
        c.gridx = 1;
	    c.gridy = 4;
	    panel.add(largeStraightField, c);
        // Yahtzee
        JButton yahtzeeButton = new JButton("Yahtzee");
        yahtzeeField = new JTextField(FIELD_WIDTH);
        yahtzeeField.setText("");
        c.gridx = 0;
	    c.gridy = 5;
	    panel.add(yahtzeeButton,c);
        c.gridx = 1;
	    c.gridy = 5;
	    panel.add(yahtzeeField, c);
	    // Chance
        JButton chanceButton = new JButton("Chance");
        chanceField = new JTextField(FIELD_WIDTH);
        chanceField.setText("");
        c.gridx = 0;
	    c.gridy = 6;
	    panel.add(chanceButton,c);
        c.gridx = 1;
	    c.gridy = 6;
	    panel.add(chanceField, c);
	    // Yahtzee Bonus
        JLabel yahtzeeBonusLabel = new JLabel("Yahtzee Bonus");
        yahtzeeBonusField = new JTextField(FIELD_WIDTH);
        yahtzeeBonusField.setText(""); 
        c.gridx = 0;
	    c.gridy = 7;
	    panel.add(yahtzeeBonusLabel,c);
        c.gridx = 1;
	    c.gridy = 7;
	    panel.add(yahtzeeBonusField, c);
	    // Total of Lower Section
	    JLabel totalOfLowerSectionLabel = new JLabel("Total of Lower Section");
	    totalOfLowerSectionField = new JTextField(FIELD_WIDTH);
	    totalOfLowerSectionField.setText("");
        c.gridx = 0;
	    c.gridy = 8;
	    panel.add(totalOfLowerSectionLabel,c);
        c.gridx = 1;
	    c.gridy = 8;
	    panel.add(totalOfLowerSectionField, c);
	    // Grand Total
        JLabel grandTotalLabel = new JLabel("Grand Total");
        grandTotalField2 = new JTextField(FIELD_WIDTH);
        grandTotalField2.setText("");
        c.gridx = 0;
	    c.gridy = 9;
	    panel.add(grandTotalLabel,c);
        c.gridx = 1;
	    c.gridy = 9;
	    panel.add(grandTotalField2, c);
	    
	    // Action Listeners
	    threeOfAKindButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
		    	if(roll !=0) {
		    		if(!players.get(currentPlayerId).isThreeOfAKindLocked() && !isSectionSet) {
		    			if (forcedJokerRule()!= "Upper") {
		    				System.out.println("Three of a Kind Button clicked");
					    	int threeOfAKindScore = calculateScore("3 of a Kind",players.get(currentPlayerId));
					    	players.get(currentPlayerId).setThreeOfAKind(threeOfAKindScore);
					    	players.get(currentPlayerId).setThreeOfAKindLocked(true);
					    	threeOfAKindField.setText(String.valueOf(threeOfAKindScore));
					    	isSectionSet = true;
					    	updateLowerBonusAndTotal();
		    			} else {
		    				new ItIsYahtzee();
		    			}
			    	} else {
			    		new FieldAlreadySet();
			    	}
		    	} else {
		    		new zeroRollsError();
		    	}
		    }
		});
	    
	    fourOfAKindButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
		    	if(roll !=0) {
		    		if(!players.get(currentPlayerId).isFourOfAKindLocked() && !isSectionSet) {
		    			if (forcedJokerRule()!= "Upper") {
		    				System.out.println("Four of a Kind Button clicked");
		    				int fourOfAKindScore = calculateScore("4 of a Kind",players.get(currentPlayerId));
					    	players.get(currentPlayerId).setFourOfAKind(fourOfAKindScore);
					    	players.get(currentPlayerId).setFourOfAKindLocked(true);
					    	fourOfAKindField.setText(String.valueOf(fourOfAKindScore));
					    	isSectionSet = true;
					    	updateLowerBonusAndTotal();
		    			} else {
		    				new ItIsYahtzee();
		    			}	
			    	} else {
			    		new FieldAlreadySet();
			    	}
		    	} else {
		    		new zeroRollsError();
		    	}
		    }
		});
	    
	    fullHouseButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
		    	if(roll !=0) {
		    		if(!players.get(currentPlayerId).isFullHouseLocked() && !isSectionSet) {
		    			if (forcedJokerRule()!= "Upper") {
		    				System.out.println("Full House Button clicked");
					    	int fullHouseScore = calculateScore("Full House",players.get(currentPlayerId));
					    	players.get(currentPlayerId).setFullHouse(fullHouseScore);
					    	players.get(currentPlayerId).setFullHouseLocked(true);
					    	fullHouseField.setText(String.valueOf(fullHouseScore));
					    	isSectionSet = true;
					    	updateLowerBonusAndTotal();
		    			} else {
		    				new ItIsYahtzee();
		    			}
		    			
			    	} else {
			    		new FieldAlreadySet();
			    	}
		    	} else {
		    		new zeroRollsError();
		    	}
		    }
		});  
	    
	    smallStraightButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
		    	if(roll !=0) {
		    		if(!players.get(currentPlayerId).isSmallStraightLocked() && !isSectionSet) {
		    			if (forcedJokerRule()!= "Upper") {
		    				System.out.println("Small Straight Button clicked");
					    	int smallStraightScore = calculateScore("Small Straight",players.get(currentPlayerId));
					    	players.get(currentPlayerId).setSmallStraight(smallStraightScore);
					    	players.get(currentPlayerId).setSmallStraightLocked(true);
					    	smallStraightField.setText(String.valueOf(smallStraightScore));
					    	isSectionSet = true;
					    	updateLowerBonusAndTotal();
		    			} else {
		    				new ItIsYahtzee();
		    			}
			    	} else {
			    		new FieldAlreadySet();
			    	}
		    	} else {
		    		new zeroRollsError();
		    	}
		    }
		});  
	    
	    largeStraightButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
		    	if(roll !=0) {
		    		if(!players.get(currentPlayerId).isLargeStraightLocked() && !isSectionSet) {
		    			if (forcedJokerRule()!= "Upper") {
		    				System.out.println("Large Straight Button clicked");
					    	int largeStraightScore = calculateScore("Large Straight",players.get(currentPlayerId));
					    	players.get(currentPlayerId).setLargeStraight(largeStraightScore);
					    	players.get(currentPlayerId).setLargeStraightLocked(true);
					    	largeStraightField.setText(String.valueOf(largeStraightScore));
					    	isSectionSet = true;
					    	updateLowerBonusAndTotal();
		    			} else {
		    				new ItIsYahtzee();
		    			}
			    	} else {
			    		new FieldAlreadySet();
			    	}
		    	} else {
		    		new zeroRollsError();
		    	}
		    }
		}); 
	    
	    yahtzeeButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
		    	if(roll !=0) {
		    		if(!players.get(currentPlayerId).isYahtzeeLocked() && !isSectionSet) {
		    			System.out.println("Yahtzee Button clicked");
				    	int yahtzeeScore = calculateScore("Yahtzee",players.get(currentPlayerId));
				    	players.get(currentPlayerId).setYahtzee(yahtzeeScore);
				    	players.get(currentPlayerId).setYahtzeeLocked(true);
				    	yahtzeeField.setText(String.valueOf(yahtzeeScore));
				    	isSectionSet = true;
				    	updateLowerBonusAndTotal();
			    	} else {
			    		new FieldAlreadySet();
			    	}
		    	} else {
		    		new zeroRollsError();
		    	}
		    }
		}); 
	    
	    chanceButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
		    	if(roll !=0) {
		    		if(!players.get(currentPlayerId).isChanceLocked() && !isSectionSet) {
		    			if (forcedJokerRule()!= "Upper") {
		    				System.out.println("Chance Button clicked");
					    	int chanceScore = calculateScore("Chance",players.get(currentPlayerId));
					    	players.get(currentPlayerId).setChance(chanceScore);
					    	players.get(currentPlayerId).setChanceLocked(true);
					    	chanceField.setText(String.valueOf(chanceScore));
					    	isSectionSet = true;
					    	updateLowerBonusAndTotal();
		    			} else {
		    				new ItIsYahtzee();
		    			}
			    	} else {
			    		new FieldAlreadySet();
			    	}
		    	} else {
		    		new zeroRollsError();
		    	}
		    }
		}); 
	}
	
	public void updateLowerBonusAndTotal() {
		int totalOfLowerSection = 0, grandTotal = 0;
		
		if (players.get(currentPlayerId).isThreeOfAKindLocked()) {
			totalOfLowerSection += players.get(currentPlayerId).getThreeOfAKind(); 
		}
		if (players.get(currentPlayerId).isFourOfAKindLocked()) {
			totalOfLowerSection += players.get(currentPlayerId).getFourOfAKind(); 
		}
		if (players.get(currentPlayerId).isFullHouseLocked()) {
			totalOfLowerSection += players.get(currentPlayerId).getFullHouse(); 
		}
		if (players.get(currentPlayerId).isSmallStraightLocked()) {
			totalOfLowerSection += players.get(currentPlayerId).getSmallStraight(); 
		}
		if (players.get(currentPlayerId).isLargeStraightLocked()) {
			totalOfLowerSection += players.get(currentPlayerId).getLargeStraight(); 
		}
		if (players.get(currentPlayerId).isYahtzeeLocked()) {
			totalOfLowerSection += players.get(currentPlayerId).getYahtzee(); 
		}
		if (players.get(currentPlayerId).isChanceLocked()) {
			totalOfLowerSection += players.get(currentPlayerId).getChance(); 
		}
		totalOfLowerSectionField.setText(String.valueOf(totalOfLowerSection));
		yahtzeeBonusField.setText(String.valueOf(players.get(currentPlayerId).getYahtzeeBonus()));
		grandTotal = players.get(currentPlayerId).getYahtzeeBonus() + totalOfLowerSection + players.get(currentPlayerId).getGrandTotalUpperSection();
		grandTotalField2.setText(String.valueOf(grandTotal));		
	}
	
	public void addComponentsToDicePanel(JPanel panel) {
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		//dicePanel.setLayout(new GridLayout(2,5));
		imagePanel1 = new ImagePanel("die1.png");
		c.gridx = 0;
	    c.gridy = 0;
	    imagePanel1.scaleImage(0.4);
	    panel.add(imagePanel1, c);
		imagePanel2 = new ImagePanel("die1.png");
		c.gridx = 1;
	    c.gridy = 0;
	    imagePanel2.scaleImage(0.4);
	    panel.add(imagePanel2, c);
		imagePanel3 = new ImagePanel("die1.png");
		c.gridx = 2;
	    c.gridy = 0;
	    imagePanel3.scaleImage(0.4);
	    panel.add(imagePanel3, c);
		imagePanel4 = new ImagePanel("die1.png");
		c.gridx = 3;
	    c.gridy = 0;
	    imagePanel4.scaleImage(0.4);
	    panel.add(imagePanel4, c);
		imagePanel5 = new ImagePanel("die1.png");
		c.gridx = 4;
	    c.gridy = 0;
	    imagePanel5.scaleImage(0.4);
	    panel.add(imagePanel5, c);
		c1 = new JCheckBox("Keep"); 
		c2 = new JCheckBox("Keep");
        c3 = new JCheckBox("Keep");
        c4 = new JCheckBox("Keep");
        c5 = new JCheckBox("Keep");
        c1.setHorizontalAlignment(JCheckBox.CENTER);
        c2.setHorizontalAlignment(JCheckBox.CENTER);
        c3.setHorizontalAlignment(JCheckBox.CENTER);
        c4.setHorizontalAlignment(JCheckBox.CENTER);
        c5.setHorizontalAlignment(JCheckBox.CENTER);
        c.gridx = 0;
	    c.gridy = 1;
	    panel.add(c1,c);
        c.gridx = 1;
	    c.gridy = 1;
	    panel.add(c2,c);
        c.gridx = 2;
	    c.gridy = 1;
	    panel.add(c3,c);
        c.gridx = 3;
	    c.gridy = 1;
	    panel.add(c4,c);
        c.gridx = 4;
	    c.gridy = 1;
	    panel.add(c5,c); 
	    c1.setEnabled(false);
	    c2.setEnabled(false);
	    c3.setEnabled(false);
	    c4.setEnabled(false);
	    c5.setEnabled(false);
        JButton rollButton = new JButton("Roll");
        rollButton.setSize(20,20);
		c.gridx = 4;
	    c.gridy = 2;
	    panel.add(rollButton,c);
        rollLabel = new JLabel();
        rollLabel.setText(getRoll());
		c.gridx = 0;
	    c.gridy = 2;
	    panel.add(rollLabel, c);
		roundLabel = new JLabel();
		roundLabel.setText(getRound());
		c.gridx = 1;
	    c.gridy = 2;
	    panel.add(roundLabel, c);
	    rollButton.addActionListener(new ActionListener(){
		    public void actionPerformed(ActionEvent e){
		    	System.out.println("Roll Button clicked");
		    	if (isSectionSet) {
		    		new ScoreSubmittedCannotRoll(); 
		    	} 
		    	else if(isYahtzee) {
		    		new ItIsYahtzee();
		    	}
		    	else {
		    		if (roll == 3) {
			    		new MaximumRollsReached(); 
			    	} else {
			    		roll = roll + 1;
						rollLabel.setText(getRoll());
			    		if (roll == 1) {
			    			c1.setEnabled(true);
			    		    c2.setEnabled(true);
			    		    c3.setEnabled(true);
			    		    c4.setEnabled(true);
			    		    c5.setEnabled(true);
			    		}
			    		if (c1.isSelected()) {
			    			c1.setEnabled(false);
			    		}
			    		if (c2.isSelected()) {
			    			c2.setEnabled(false);
			    		}
			    		if (c3.isSelected()) {
			    			c3.setEnabled(false);
			    		}
			    		if (c4.isSelected()) {
			    			c4.setEnabled(false);
			    		}
			    		if (c5.isSelected()) {
			    			c5.setEnabled(false);
			    		}
			    		//dieValues.clear();
			    		Random rand = new Random();
				    	for (int i=0;i<5;i++) {
							switch(i) {	
								case 0:
									if (!c1.isSelected()) {
										imgValue = rand.nextInt(6) + 1;
										//imgValue =1;
										dieValues.set(i,imgValue);
										System.out.println("ImgValue is: "+ imgValue);
										imagePanel1.setImage(imagesPaths[imgValue-1]);
										imagePanel1.scaleImage(0.4);
									}
									break;
								case 1:
									if (!c2.isSelected()) {
										imgValue = rand.nextInt(6) + 1;
										//imgValue =1;
										dieValues.set(i,imgValue);
										System.out.println("ImgValue is: "+ imgValue);
										imagePanel2.setImage(imagesPaths[imgValue-1]);
										imagePanel2.scaleImage(0.4);
									}
									break;
								case 2:
									if (!c3.isSelected()) {
										imgValue = rand.nextInt(6) + 1;
										//imgValue =1;
										dieValues.set(i,imgValue);
										System.out.println("ImgValue is: "+ imgValue);
										imagePanel3.setImage(imagesPaths[imgValue-1]);
										imagePanel3.scaleImage(0.4);
									}
									break;
								case 3:
									if (!c4.isSelected()) {
										imgValue = rand.nextInt(6) + 1;
										//imgValue =1;
										dieValues.set(i,imgValue);
										System.out.println("ImgValue is: "+ imgValue);
										imagePanel4.setImage(imagesPaths[imgValue-1]);
										imagePanel4.scaleImage(0.4);
									}
									break;
								case 4:
									if (!c5.isSelected()) {
										imgValue = rand.nextInt(6) + 1;
										//imgValue =1;
										dieValues.set(i,imgValue);
										System.out.println("ImgValue is: "+ imgValue);
										imagePanel5.setImage(imagesPaths[imgValue-1]);
										imagePanel5.scaleImage(0.4);
									}
									break;
							}	
				    	}
			    	checkForYahtzee();
			    	checkForYahtzeeBonus();
			    	}
		    	}
		    }
		});
	}
	

	private void checkForYahtzee() {
		int countY;
		for (int i=1;i<=6;i++) {
			countY = 0;
			for (int die: dieValues) {
				if (i==die) {
					countY += 1;
				}
			}
			if (countY ==5) {
				isYahtzee =true;
				// If Yahtzee Implement forced Jokers Rule
				forcedJokerRule();
				new ItIsYahtzee();
			}
		}
		
	}	
	
	private void checkForYahtzeeBonus() {
		int countY;
		for (int i=1;i<=6;i++) {
			countY = 0;
			for (int die: dieValues) {
				if (i==die) {
					countY += 1;
				}
			}
			if (countY ==5) {
				if (players.get(currentPlayerId).getYahtzee() == 50) {
					players.get(currentPlayerId).setYahtzeeBonus(players.get(currentPlayerId).getYahtzeeBonus() + 100);
					yahtzeeBonusField.setText(String.valueOf(players.get(currentPlayerId).getYahtzeeBonus()));
					updateLowerBonusAndTotal();
					break;
				}
			} 
		}
	}
	
	public void addComponentsToYahtzeeUppersectionPanel(JPanel panel) {
		panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        Border borderUpper = BorderFactory.createTitledBorder("Upper Section");
        panel.setBorder(borderUpper);
        final int FIELD_WIDTH = 8;
        // Fields
        // Aces
        JButton acesButton = new JButton("Aces");
        acesField = new JTextField(FIELD_WIDTH);
        acesField.setText("");
        c.gridx = 0;
	    c.gridy = 0;
	    panel.add(acesButton,c);
        c.gridx = 1;
	    c.gridy = 0;
	    panel.add(acesField, c);
        // Twos
        JButton twosButton = new JButton("Twos");
        twosField = new JTextField(FIELD_WIDTH);
        twosField.setText("");
        c.gridx = 0;
	    c.gridy = 1;
	    panel.add(twosButton,c);
        c.gridx = 1;
	    c.gridy = 1;
	    panel.add(twosField, c);
        // Threes
        JButton threesButton = new JButton("Threes");
        threesField = new JTextField(FIELD_WIDTH);
        threesField.setText("");
        c.gridx = 0;
	    c.gridy = 2;
	    panel.add(threesButton,c);
        c.gridx = 1;
	    c.gridy = 2;
	    panel.add(threesField, c);
        // Fours
        JButton foursButton = new JButton("Fours");
        foursField = new JTextField(FIELD_WIDTH);
        foursField.setText("");
        c.gridx = 0;
	    c.gridy = 3;
	    panel.add(foursButton,c);
        c.gridx = 1;
	    c.gridy = 3;
	    panel.add(foursField, c);
        // Fives
        JButton fivesButton = new JButton("Fives");
        fivesField = new JTextField(FIELD_WIDTH);
        fivesField.setText("");
        c.gridx = 0;
	    c.gridy = 4;
	    panel.add(fivesButton,c);
        c.gridx = 1;
	    c.gridy = 4;
	    panel.add(fivesField, c);
        // Sixes
        JButton sixesButton = new JButton("Sixes");
        sixesField = new JTextField(FIELD_WIDTH);
        sixesField.setText("");
        c.gridx = 0;
	    c.gridy = 5;
	    panel.add(sixesButton,c);
        c.gridx = 1;
	    c.gridy = 5;
	    panel.add(sixesField, c);
        // Score Sub Total 
        scoreSubtotalLabel = new JLabel("Score Subtotal");
        scoreSubtotalField = new JTextField(FIELD_WIDTH);
        scoreSubtotalField.setText(""); 
        c.gridx = 0;
	    c.gridy = 6;
	    panel.add(scoreSubtotalLabel,c);
        c.gridx = 1;
	    c.gridy = 6;
	    panel.add(scoreSubtotalField, c);
        // Bonus
        bonusLabel = new JLabel("Bonus");
        bonusField = new JTextField(FIELD_WIDTH);
        bonusField.setText("");
        c.gridx = 0;
	    c.gridy = 7;
	    panel.add(bonusLabel,c);
        c.gridx = 1;
	    c.gridy = 7;
	    panel.add(bonusField, c);
        // Grand Total
        grandTotalLabel = new JLabel("Grand Total");
        grandTotalField = new JTextField(FIELD_WIDTH);
        grandTotalField.setText("");
        c.gridx = 0;
	    c.gridy = 8;
	    panel.add(grandTotalLabel,c);
        c.gridx = 1;
	    c.gridy = 8;
	    panel.add(grandTotalField, c);
	    
	    // Action Listeners
	    acesButton.addActionListener(new ActionListener(){
		    public void actionPerformed(ActionEvent e){
		    	if(roll !=0) {
		    		if(!players.get(currentPlayerId).isAcesLocked() && !isSectionSet) {
		    			if (forcedJokerRule() != "Lower") {
		    				System.out.println("Aces Button clicked");
				    		int acesScore = calculateScore("Aces",players.get(currentPlayerId));
					    	players.get(currentPlayerId).setAces(acesScore);
					    	players.get(currentPlayerId).setAcesLocked(true);
					    	acesField.setText(String.valueOf(acesScore));
					    	updateUpperBonusAndTotal();
					    	isSectionSet = true;
		    			} else {
		    				new ItIsYahtzee();
		    			}
			    	} else {
			    		new FieldAlreadySet();
			    	}
		    	} else {
		    		new zeroRollsError();
		    	}
		    }
		});
	    
	    twosButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
		    	if(roll !=0) {
		    		if(!players.get(currentPlayerId).isTwosLocked()&& !isSectionSet) {
		    			if (forcedJokerRule() != "Lower") {
		    				System.out.println("Twos Button clicked");
					    	int twosScore = calculateScore("Twos",players.get(currentPlayerId));
					    	players.get(currentPlayerId).setTwos(twosScore);
					    	players.get(currentPlayerId).setTwosLocked(true);
					    	twosField.setText(String.valueOf(twosScore));
					    	updateUpperBonusAndTotal();
					    	isSectionSet = true;
		    			} else {
		    				new ItIsYahtzee();
		    			}
			    	} else {
			    		new FieldAlreadySet();
			    	}
		    	} else {
		    		new zeroRollsError();
		    	}
		    }
		});
	    
	    threesButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
		    	if(roll !=0) {
		    		if(!players.get(currentPlayerId).isThreesLocked()&& !isSectionSet) {
		    			if (forcedJokerRule() != "Lower") {
		    				System.out.println("Threes Button clicked");
					    	int threesScore = calculateScore("Threes",players.get(currentPlayerId));
					    	players.get(currentPlayerId).setThrees(threesScore);
					    	players.get(currentPlayerId).setThreesLocked(true);
					    	threesField.setText(String.valueOf(threesScore));
					    	updateUpperBonusAndTotal();
					    	isSectionSet = true;
		    			} else {
		    				new ItIsYahtzee();
		    			}
			    	} else {
			    		new FieldAlreadySet();
			    	}
		    	} else {
		    		new zeroRollsError();
		    	}
		    }
		});
	    
	    foursButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
		    	if(roll !=0) {
		    		if(!players.get(currentPlayerId).isFoursLocked()&& !isSectionSet) {
		    			if (forcedJokerRule() != "Lower") {
		    				System.out.println("Fours Button clicked");
					    	int foursScore = calculateScore("Fours",players.get(currentPlayerId));
					    	players.get(currentPlayerId).setFours(foursScore);
					    	players.get(currentPlayerId).setFoursLocked(true);
					    	foursField.setText(String.valueOf(foursScore));
					    	updateUpperBonusAndTotal();
					    	isSectionSet = true;
		    			} else {
		    				new ItIsYahtzee();
		    			}
			    	} else {
			    		new FieldAlreadySet();
			    	}
		    	} else {
		    		new zeroRollsError();
		    	}
		    }
		});
	    
	    fivesButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
		    	if(roll !=0) {
		    		if(!players.get(currentPlayerId).isFivesLocked()&& !isSectionSet) {
		    			if (forcedJokerRule() != "Lower") {
		    				System.out.println("Fives Button clicked");
					    	int fivesScore = calculateScore("Fives",players.get(currentPlayerId));
					    	players.get(currentPlayerId).setFives(fivesScore);
					    	players.get(currentPlayerId).setFivesLocked(true);
					    	fivesField.setText(String.valueOf(fivesScore));
					    	updateUpperBonusAndTotal();
					    	isSectionSet = true;
		    			} else {
		    				new ItIsYahtzee();
		    			}
			    	} else {
			    		new FieldAlreadySet();
			    	}
		    	} else {
		    		new zeroRollsError();
		    	}
		    }
		});
	    
	    sixesButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
		    	if(roll !=0) {
		    		if(!players.get(currentPlayerId).isSixesLocked()&& !isSectionSet) {
		    			if (forcedJokerRule() != "Lower") {
		    				System.out.println("Aces Button clicked");
					    	int sixesScore = calculateScore("Sixes",players.get(currentPlayerId));
					    	players.get(currentPlayerId).setSixes(sixesScore);
					    	players.get(currentPlayerId).setSixesLocked(true);
					    	sixesField.setText(String.valueOf(sixesScore));
					    	updateUpperBonusAndTotal();
					    	isSectionSet = true;
		    			} else {
		    				new ItIsYahtzee();
		    			}
			    	} else {
			    		new FieldAlreadySet();
			    	}
		    	} else {
		    		new zeroRollsError();
		    	}
		    }
		});    
	}
	
	public String forcedJokerRule() {
		String rule = "";
		int value = dieValues.get(0);
		if (isYahtzee) {
			switch(value) {
			case 1:
				if(players.get(currentPlayerId).isAcesLocked() && (!players.get(currentPlayerId).isFullHouseLocked() || !players.get(currentPlayerId).isSmallStraightLocked() || !players.get(currentPlayerId).isLargeStraightLocked())) {
					rule = "Lower";
				} else {
					rule = "Upper";
				}
				break;
			case 2:
				if(players.get(currentPlayerId).isTwosLocked() && (!players.get(currentPlayerId).isFullHouseLocked() || !players.get(currentPlayerId).isSmallStraightLocked() || !players.get(currentPlayerId).isLargeStraightLocked())) {
					rule = "Lower";
				} else {
					rule = "Upper";
				}
				break;
			case 3:
				if(players.get(currentPlayerId).isThreesLocked() && (!players.get(currentPlayerId).isFullHouseLocked() || !players.get(currentPlayerId).isSmallStraightLocked() || !players.get(currentPlayerId).isLargeStraightLocked())) {
					rule = "Lower";
				} else {
					rule = "Upper";
				}
				break;
			case 4:
				if(players.get(currentPlayerId).isFoursLocked() && (!players.get(currentPlayerId).isFullHouseLocked() || !players.get(currentPlayerId).isSmallStraightLocked() || !players.get(currentPlayerId).isLargeStraightLocked())) {
					rule = "Lower";
				} else {
					rule = "Upper";
				}
				break;
			case 5:
				if(players.get(currentPlayerId).isFivesLocked() && (!players.get(currentPlayerId).isFullHouseLocked() || !players.get(currentPlayerId).isSmallStraightLocked() || !players.get(currentPlayerId).isLargeStraightLocked())) {
					rule = "Lower";
				} else {
					rule = "Upper";
				}
				break;	
			case 6:
				if(players.get(currentPlayerId).isSixesLocked() && (!players.get(currentPlayerId).isFullHouseLocked() || !players.get(currentPlayerId).isSmallStraightLocked() || !players.get(currentPlayerId).isLargeStraightLocked())) {
					rule = "Lower";
				} else {
					rule = "Upper";
				}
				break;	
			default:
				rule = "Upper";
			}
		}
		return rule;
	}
	
	public void updateUpperBonusAndTotal() {
		int subTotalScore = 0;
		int bonusScore = 0;
		int grandTotalScoreUpper;
		
		if (players.get(currentPlayerId).isAcesLocked()) {
			subTotalScore += players.get(currentPlayerId).getAces(); 
		}
		if (players.get(currentPlayerId).isTwosLocked()) {
			subTotalScore += players.get(currentPlayerId).getTwos(); 
		}
		if (players.get(currentPlayerId).isThreesLocked()) {
			subTotalScore += players.get(currentPlayerId).getThrees(); 
		}
		if (players.get(currentPlayerId).isFoursLocked()) {
			subTotalScore += players.get(currentPlayerId).getFours(); 
		}
		if (players.get(currentPlayerId).isFivesLocked()) {
			subTotalScore += players.get(currentPlayerId).getFives(); 
		}
		if (players.get(currentPlayerId).isSixesLocked()) {
			subTotalScore += players.get(currentPlayerId).getSixes(); 
		}
		
		scoreSubtotalField.setText(String.valueOf(subTotalScore));
		
		if (subTotalScore >=63) {
			bonusScore = 35;
			bonusField.setText(String.valueOf(bonusScore));
		}
		grandTotalScoreUpper = subTotalScore + bonusScore;
		players.get(currentPlayerId).setGrandTotalUpperSection(grandTotalScoreUpper);
		grandTotalField.setText(String.valueOf(grandTotalScoreUpper));		
	}
	
	public int calculateScore(String s, Player p) {
		int score = 0;
		switch(s) {
		case "Aces":
			for (int i: dieValues) {
				if (i==1) {
					score += 1;
				}
			}
			break;
		case "Twos":
			for (int i: dieValues) {
				if (i==2) {
					score += 2;
				}
			}
			break;
		case "Threes":
			for (int i: dieValues) {
				if (i==3) {
					score += 3;
				}
			}
			break;
		case "Fours":
			for (int i: dieValues) {
				if (i==4) {
					score += 4;
				}
			}
			break;
		case "Fives":
			for (int i: dieValues) {
				if (i==5) {
					score += 5;
				}
			}
			break;
		case "Sixes":
			for (int i: dieValues) {
				if (i==6) {
					score += 6;
				}
			}
			break;
		case "3 of a Kind":
			int count;
			for (int i=1;i<=6;i++) {
				count = 0;
				for (int die: dieValues) {
					if (i==die) {
						count += 1;
					}
				}
				if (count >=3) {
					score = i* count;
					break;
				}
			}
			break;
		case "4 of a Kind":
			int count1;
			for (int i=1;i<=6;i++) {
				count1 = 0;
				for (int die: dieValues) {
					if (i==die) {
						count1 += 1;
					}
				}
				if (count1 >=4) {
					score = i* count1;
					break;
				}
			}
			break;
		case "Full House":
			int counter, counter2 = 0, counter3 = 0;
			for (int i=1;i<=6;i++) {
				counter = 0;
				for (int die: dieValues) {
					if (i==die) {
						counter += 1;
					}
				}
				if (counter ==2) {
					counter2 = i;
				}
				if (counter == 3) {
					counter3 = i;
				}
				if (counter2 !=0 && counter3 != 0) {
					score = 25;
					break;
				}
			}
			break;
		case "Small Straight":
			Collections.sort(dieValues);
			if (dieValues.contains(1) && dieValues.contains(2) && dieValues.contains(3) && dieValues.contains(4)) {
				score = 30;
			}
			if (dieValues.contains(2) && dieValues.contains(3) && dieValues.contains(4) && dieValues.contains(5)) {
				score = 30;
			}
			if (dieValues.contains(3) && dieValues.contains(4) && dieValues.contains(5) && dieValues.contains(6)) {
				score = 30;
			}
			break;
		case "Large Straight":
			Collections.sort(dieValues);
			if (dieValues.contains(1) && dieValues.contains(2) && dieValues.contains(3) && dieValues.contains(4) && dieValues.contains(5)) {
				score = 40;
			}
			if (dieValues.contains(6) && dieValues.contains(2) && dieValues.contains(3) && dieValues.contains(4) && dieValues.contains(5)) {
				score = 40;
			}
			break;
		case "Yahtzee":
			int countY;
			for (int i=1;i<=6;i++) {
				countY = 0;
				for (int die: dieValues) {
					if (i==die) {
						countY += 1;
					}
				}
				if (countY ==5) {
					score = 50;
					break;
				}
			}
			break;
		case "Chance":
			for (int die: dieValues) {
				score += die;
			}
			break;
		}
		return score;	
	}
	
	public String getRoll() {
		return "Roll: " + roll;
	}
	
	public String getRound() {
		return "Round: " + round;
	}
	
	
	public String getCurrentPlayerName() {
		String currentPlayerName = players.get(currentPlayerId).getplayerName();
		return currentPlayerName;
	}
	
	private void createMenus() {     
	    JMenuBar menuBar = new JMenuBar();     
	    setJMenuBar(menuBar);
	    menuBar.add(createGameMenu());	   
	}
	
	public JMenu createGameMenu() {
		JMenu menu = new JMenu("Game");
		menu.add(loadGame());
		menu.add(saveGame()); 
		menu.add(exitGame());
		return menu;
	}
	
	private JMenuItem exitGame() {
		JMenuItem item = new JMenuItem("Exit");      
 		class MenuItemListener implements ActionListener
 		{
 			public void actionPerformed(ActionEvent event)
 			{
 				System.exit(0);
 			}	
 		}      
 		ActionListener listener = new MenuItemListener();
 		item.addActionListener(listener);
 		return item;
	}
	
	
	ArrayList<String> GameNames = new ArrayList<String>();
	public class SaveGameAs {  
		JFrame f;  
		SaveGameAs(){  
		    f = new JFrame();
		    String gameName;
		    gameName = JOptionPane.showInputDialog(f,"Save Game As: ");
		    if (GameNames.contains(gameName)) {
		    	new TryAgain();
		    	gameName = null;
		    } else {
		    	GameNames.add(gameName);
		    	for (int i=0;i<totalNumberOfPlayers;i++) {
		    		players.get(i).setGameName(gameName);
		    	}
		    }
		}
	}
	
	private JMenuItem saveGame() {
		
		JMenuItem item = new JMenuItem("Save Game"); 
		class MenuItemListener implements ActionListener
 		{
 			public void actionPerformed(ActionEvent event)
 			{
 				try {
 					new SaveGameAs();
 			        for (int i=0;i<totalNumberOfPlayers;i++) {
 			        	// Host name
 	 					String host = "localhost";
 	 			        // Establish connection with the server
 	 			        Socket socket = new Socket(host, 8000);
 	 			        // Create an output stream to the server
 	 			        ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
 			        	toServer.writeObject((Object)players.get(i));
 			        }
 			     } catch (IOException ex) {
 			    	 
 			    	 ex.printStackTrace();
 			     }
 		    }
 		}	
 		ActionListener listener = new MenuItemListener();
 		item.addActionListener(listener);
		return item;
	}
	
	private ArrayList<String> gameNamesFromDb;
	private String loadGameChoice;
	
	private JMenuItem loadGame() {
		JMenuItem item = new JMenuItem("Load Game"); 
		class MenuItemListener implements ActionListener
 		{
 			public void actionPerformed(ActionEvent event)
 			{
 				try {
 	 					String host = "localhost";
 	 			        // Establish connection with the server
 	 			        Socket socket = new Socket(host, 8000);
 	 			        // Create an output stream to the server
 	 			        ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());

 	 			        // Load Games  
 	 			        toServer.writeObject("Load Game");
 	 			        
 	 			        // Listen for Game names
 	 			        gameNamesFromDb = new ArrayList<>();
 	 			        ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
 	 			        try {
							gameNamesFromDb = (ArrayList<String>) fromServer.readObject();
							System.out.println("gameNamesFromDb size:" + gameNamesFromDb.size());
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
 	 			        
 	 			        // Choose a Game
 	 			        if (gameNamesFromDb.isEmpty()){
 	 			        	new NoSavedGames();
 	 			        } else {
 	 			        	new ChooseAGameToLoad();
 	 			        }
 	 			        
 	 			        // Send to server
 	 			        toServer.writeObject(loadGameChoice);
 	 			        
 	 			        // Retrieve Players array
	 				    try {
	 				    	// Create an input stream from the socket
	 	 			        ObjectInputStream inputFromServer = new ObjectInputStream(socket.getInputStream());

	 	 			        // Read from input
							Object objects = inputFromServer.readObject();
							
							// Load Players
							players.clear();
		 				    for (Player p : (ArrayList<Player>) objects) {
		 				    	players.add(p);
		 				    }
		 				   
		 				   // Refresh the board
		 				   updateBoard(players.get(0));
		 				   rollLabel.setText(getRoll());
		 				   roundLabel.setText(getRound());
		 				   playerNameLabel2.setText(players.get(0).getplayerName());
		 				   totalNumberOfPlayers = players.size();
		 				    
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	 				    
 			     } catch (IOException ex) {
 			    	 
 			    	 ex.printStackTrace();
 			     }
 			}	
 		}      
 		ActionListener listener = new MenuItemListener();
 		item.addActionListener(listener);
		return item;
	}
	
	public class NoSavedGames {  
		JFrame f;  
		NoSavedGames(){  
		    f=new JFrame();  
		    JOptionPane.showMessageDialog(f,"No Saved Games!","Alert",JOptionPane.WARNING_MESSAGE);     
		}
	}
	
	public class ChooseAGameToLoad {
		JFrame f;  
		ChooseAGameToLoad(){  
			f=new JFrame();
			// declaration and initialize String Array 
	        String[] choices = new String[gameNamesFromDb.size()]; 
	        // ArrayList to Array Conversion 
	        for (int j = 0; j < gameNamesFromDb.size(); j++) { 
	            // Assign each value to String array 
	        	choices[j] = gameNamesFromDb.get(j); 
	        } 
		    loadGameChoice = (String) JOptionPane.showInputDialog(f, "Choose a Game to Load..","Load A game", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]	);
		    System.out.println("Game Choice is: "+ loadGameChoice);
		}
	}

	public static void main(String args[]) {
		YahtzeeFrame yahtzee = new YahtzeeFrame();
		yahtzee.setVisible(true);
	}	
}


