package server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import game.Player;

public class SaveServer extends JFrame {

	private JTextArea wordsBox;
	//private ObjectOutputStream outputToFile;
	private ObjectInputStream inputFromClient;
	private PreparedStatement queryStmtGameName, insertStatement;
	private Connection conn;
	private Player p;
	private LocalDateTime timeSaved;
	private DateTimeFormatter dtf;
	ArrayList<String> gameNamesFromDb;
	
	public SaveServer() {
		createMainPanel();
		wordsBox.append("Ready to Accept Connections");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600,400);
		setVisible(true);
		try {
			     // Create a server socket
			     ServerSocket serverSocket = new ServerSocket(8000);
			     System.out.println("Server started ");

//			     // Create an object output stream
//			     outputToFile = new ObjectOutputStream(
//			       new FileOutputStream("student.dat", true));

			     while (true) {
			       // Listen for a new connection request
			       Socket socket = serverSocket.accept();

			       // Create an input stream from the socket
			       inputFromClient = new ObjectInputStream(socket.getInputStream());

			       // Read from input
			       Object object = inputFromClient.readObject();
			       
			       // Date and time
			       dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			       timeSaved = LocalDateTime.now();  
			       System.out.println(dtf.format(timeSaved));  
			       
			       if (object.getClass() == Player.class){
			    	// Write to the database : Save Game
				       p = (Player)object;
				       System.out.println("got object ");
				       
				       try {
							conn = DriverManager.getConnection("jdbc:sqlite:yahtzee.db");
							System.out.println("Connected to DataBase...");
							
							String insertSQL = "INSERT INTO SavedGames (gameName, playerName, round, aces, twos, "
									+ "threes, fours, fives, sixes, scoreSubtotalUppersection, bonusUpperSection, "
									+ "grandTotalUpperSection, threeOfAKind, fourOfAKind, fullHouse, smallStraight, "
									+ "largeStraight, yahtzee, chance, yahtzeeBonus, totalOfLowerSection, grandTotal, "
									+ "acesLocked, twosLocked, threesLocked, foursLocked, fivesLocked, sixesLocked, "
									+ "scoreSubtotalUppersectionLocked, bonusUpperSectionLocked, grandTotalUpperSectionLocked, "
									+ "threeOfAKindLocked, fourOfAKindLocked, fullHouseLocked, smallStraightLocked, largeStraightLocked, "
									+ "yahtzeeLocked, chanceLocked, yahtzeeBonusLocked, totalOfLowerSectionLocked, grandTotalLocked, timeSaved) " +
									"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
							insertStatement = conn.prepareStatement(insertSQL);
							insertData();
							//System.out.println("Game Name: "+ p.getGameName());
				       }  catch (SQLException e) {
				    	   // TODO Auto-generated catch block
				    	   e.printStackTrace();
					}	
					} 
			       if (object.getClass() == String.class) {
			    	   
			    	   // Load Game
			    	   // Retrieve a List of Games from the DB and send to the client
			    	   gameNamesFromDb = new ArrayList<>();
			    	   getGameNames();
			    	   ObjectOutputStream outputToClient = new ObjectOutputStream(socket.getOutputStream());
			           outputToClient.writeObject(gameNamesFromDb);
			    	   
			    	   // Listen for a game name
				       Object gameChoiceFromClient = inputFromClient.readObject();
				       
				       // get game from database and send to Client
				       ArrayList<Player> playersFromDb = new ArrayList<>();
				       playersFromDb = getGameFromDb((String)gameChoiceFromClient);
				       outputToClient = new ObjectOutputStream(socket.getOutputStream());
			           outputToClient.writeObject(playersFromDb);   
			       }
			     }
			   }
			   catch(ClassNotFoundException ex) {
			     ex.printStackTrace();
			   }
			   catch(IOException ex) {
			     ex.printStackTrace();
			   }
			   finally {
			     try {
			       inputFromClient.close();
			       //outputToFile.close();
			     }
			     catch (Exception ex) {
			       ex.printStackTrace();
			     }
			   }
	}
	
	public void getGameNames(){
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:yahtzee.db");
			System.out.println("Connected to DataBase...");
			
			String getGameNamesSQL = "SELECT gameName from SavedGames";
			queryStmtGameName = conn.prepareStatement(getGameNamesSQL );
			ResultSet rset = queryStmtGameName.executeQuery();
			ResultSetMetaData rsmd = rset.getMetaData();
			int numColumns = rsmd.getColumnCount();
			String rowString = "";
			do { 
				for (int i=1;i<=numColumns;i++) {
					Object o = rset.getObject(i);
					rowString = o.toString();
					System.out.print("Game Name:  " + rowString);
					System.out.println();
					if (!gameNamesFromDb.contains(rowString)){
						gameNamesFromDb.add(rowString);
					}
				}
				//rowString += "\n"; 
				} while (rset.next()); 
			
		}  catch (SQLException e) {
    	   // TODO Auto-generated catch block
    	   e.printStackTrace();
		}		
	}
	
	public ArrayList<Player> getGameFromDb(String gameChoiceFromClient){
		ArrayList<Player> playerListFromDb = new ArrayList<>();
		Object[] player1Fields, player2Fields, player3Fields, player4Fields;
		Player p1, p2, p3, p4;
		String[] setMethods = {"setGameName", "setplayerName", "setRound", "setAces", "setTwos", 
				"setThrees", "setFours", "setFives", "setSixes", "setScoreSubtotalUppersection", 
				"setBonusUpperSection","setGrandTotalUpperSection", "setThreeOfAKind", "setFourOfAKind", 
				"setFullHouse", "setSmallStraight", "setLargeStraight", "setYahtzee", "setChance", "setYahtzeeBonus", 
				"setTotalOfLowerSection", "getGrandTotal", "setAcesLocked", "setTwosLocked", "setThreesLocked", "seTFoursLocked", 
				"setFivesLocked", "setSixesLocked" , "setScoreSubtotalUppersectionLocked", "setBonusUpperSectionLocked", 
				"setGrandTotalUpperSectionLocked", "setThreeOfAKindLocked", "setFourOfAKindLocked", "setFullHouseLocked", 
				"setSmallStraightLocked", "setLargeStraightLocked", "setYahtzeeLocked", "setChanceLocked", "setYahtzeeBonusLocked", 
				"setTotalOfLowerSectionLocked", "setGrandTotalLocked", "setTimeSaved"};
			
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:yahtzee.db");
			System.out.println("Connected to DataBase...");
			
			String getGameNamesSQL = "SELECT * from SavedGames WHERE gameName = ?";
			queryStmtGameName = conn.prepareStatement(getGameNamesSQL);
			PreparedStatement stmt = queryStmtGameName;
			stmt.setString(1, gameChoiceFromClient);
			ResultSet rset = queryStmtGameName.executeQuery();
			ResultSetMetaData rsmd = rset.getMetaData();
			int numColumns = rsmd.getColumnCount();
			String rowString = "";
			int numberIter = 0;
			java.lang.reflect.Method method;
			while (rset.next()) {
				for (int i=1;i<=numColumns;i++) {
					Object o = rset.getObject(i);
//					method = Player.class.getDeclaredMethod(setMethods[i-1]);
//					method.invoke(playerListFromDb.get(numberIter),o);
					rowString += o.toString()+ ",";
				}
				rowString += "\n"; 
				numberIter++;
			}
			
			System.out.print("rowString  is:\n" + rowString);
			
			try {
				
				
				switch(numberIter) {
					case 1:
						p1 = new Player(); 
						player1Fields = rowString.split(",");
						
						updatePlayerFields(p1, player1Fields);
						playerListFromDb.add(p1);
//						for (int i=0;i<player1Fields.length-1;i++) {
//							
//							method = Player.class.getDeclaredMethod(setMethods[i]);
//							method.invoke(p1,player1Fields[i]);
//							playerListFromDb.add(p1);
//						}
						break;
					case 2:
						p1 = new Player(); 
						p2 = new Player(); 
						player1Fields = rowString.split("\n")[0].split(",");
						player2Fields = rowString.split("\n")[1].split(",");
						updatePlayerFields(p1, player1Fields);
						updatePlayerFields(p2, player2Fields);
						playerListFromDb.add(p1);
						playerListFromDb.add(p2);
//						for (int i=0;i<player1Fields.length-1;i++) {
//							
//							method = Player.class.getDeclaredMethod(setMethods[i]);
//							method.invoke(p1,player1Fields[i]);
//							method.invoke(p2,player1Fields[i]);
//							playerListFromDb.add(p1);
//							playerListFromDb.add(p2);
//						}
						break;
					case 3:
						p1 = new Player(); 
						p2 = new Player();
						p3 = new Player();
						player1Fields = rowString.split("\n")[0].split(",");
						player2Fields = rowString.split("\n")[1].split(",");
						player3Fields = rowString.split("\n")[2].split(",");
						updatePlayerFields(p1, player1Fields);
						updatePlayerFields(p2, player2Fields);
						updatePlayerFields(p3, player3Fields);
						playerListFromDb.add(p1);
						playerListFromDb.add(p2);
						playerListFromDb.add(p3);
//						for (int i=0;i<player1Fields.length-1;i++) {
//							
//							method = Player.class.getDeclaredMethod(setMethods[i]);
//							method.invoke(p1,player1Fields[i]);
//							method.invoke(p2,player1Fields[i]);
//							method.invoke(p3,player1Fields[i]);
//							playerListFromDb.add(p1);
//							playerListFromDb.add(p2);
//							playerListFromDb.add(p3);
//							
//						}
						break;
					case 4:
						p1 = new Player(); 
						p2 = new Player(); 
						p3 = new Player(); 
						p4 = new Player(); 
						player1Fields = rowString.split("\n")[0].split(",");
						player2Fields = rowString.split("\n")[1].split(",");
						player3Fields = rowString.split("\n")[2].split(",");
						player4Fields = rowString.split("\n")[3].split(",");
						updatePlayerFields(p1, player1Fields);
						updatePlayerFields(p2, player2Fields);
						updatePlayerFields(p3, player3Fields);
						updatePlayerFields(p4, player4Fields);
						playerListFromDb.add(p1);
						playerListFromDb.add(p2);
						playerListFromDb.add(p3);
						playerListFromDb.add(p4);
//						for (int i=0;i<player1Fields.length-1;i++) {
//							
//							method = Player.class.getDeclaredMethod(setMethods[i]);
//							method.invoke(p1,player1Fields[i]);
//							method.invoke(p2,player1Fields[i]);
//							method.invoke(p3,player1Fields[i]);
//							method.invoke(p4,player1Fields[i]);
//							playerListFromDb.add(p1);
//							playerListFromDb.add(p2);
//							playerListFromDb.add(p3);
//							playerListFromDb.add(p4);
//						}
						break;
					}
				} catch (Exception ex) {
					 ex.printStackTrace();
			}
			System.out.println();
		}  catch (SQLException | IllegalArgumentException | SecurityException e) {
    	   // TODO Auto-generated catch block
    	   e.printStackTrace();
		}	
		return playerListFromDb;
	}
	
	public void updatePlayerFields(Player p, Object[] playerFields) {
		p.setGameName(String.valueOf( playerFields[0])); 
		p.setplayerName( String.valueOf(playerFields[1])); 
		System.out.println("Game name: "+ String.valueOf(playerFields[0]));
		System.out.println("Player Name: "+ String.valueOf(playerFields[1]));
		p.setRound(Integer.parseInt(String.valueOf( playerFields[2]))); 
		p.setAces(Integer.parseInt(String.valueOf( playerFields[3]))); 
		p.setTwos(Integer.parseInt(String.valueOf( playerFields[4]))); 
		p.setThrees(Integer.parseInt(String.valueOf( playerFields[5]))); 
		p.setFours(Integer.parseInt(String.valueOf( playerFields[6]))); 
		p.setFives(Integer.parseInt(String.valueOf( playerFields[7])));
		p.setSixes(Integer.parseInt(String.valueOf( playerFields[8]))); 
		p.setScoreSubtotalUppersection(Integer.parseInt(String.valueOf( playerFields[9]))); 
		p.setBonusUpperSection(Integer.parseInt(String.valueOf( playerFields[10])));
		p.setGrandTotalUpperSection(Integer.parseInt(String.valueOf( playerFields[11]))); 
		p.setThreeOfAKind(Integer.parseInt(String.valueOf( playerFields[12]))); 
		p.setFourOfAKind(Integer.parseInt(String.valueOf( playerFields[13]))); 
		p.setFullHouse(Integer.parseInt(String.valueOf( playerFields[14]))); 
		p.setSmallStraight(Integer.parseInt(String.valueOf( playerFields[15]))); 
		p.setLargeStraight(Integer.parseInt(String.valueOf( playerFields[16]))); 
		p.setYahtzee(Integer.parseInt(String.valueOf( playerFields[17]))); 
		p.setChance(Integer.parseInt(String.valueOf( playerFields[18]))); 
		p.setYahtzeeBonus(Integer.parseInt(String.valueOf( playerFields[19]))); 
		p.setTotalOfLowerSection(Integer.parseInt(String.valueOf( playerFields[20]))); 
		p.setGrandTotal(Integer.parseInt(String.valueOf( playerFields[21]))); 
		p.setAcesLocked(Boolean.parseBoolean(String.valueOf( playerFields[22]))); 
		p.setTwosLocked(Boolean.parseBoolean(String.valueOf( playerFields[23]))); 
		p.setThreesLocked(Boolean.parseBoolean(String.valueOf( playerFields[24]))); 
		p.setFoursLocked(Boolean.parseBoolean(String.valueOf( playerFields[25]))); 
		p.setFivesLocked(Boolean.parseBoolean(String.valueOf( playerFields[26]))); 
		p.setSixesLocked(Boolean.parseBoolean(String.valueOf( playerFields[27]))); 
		p.setScoreSubtotalUppersectionLocked(Boolean.parseBoolean(String.valueOf( playerFields[28]))); 
		p.setBonusUpperSectionLocked(Boolean.parseBoolean(String.valueOf( playerFields[29]))); 
		p.setGrandTotalUpperSectionLocked(Boolean.parseBoolean(String.valueOf( playerFields[30]))); 
		p.setThreeOfAKindLocked(Boolean.parseBoolean(String.valueOf( playerFields[31]))); 
		p.setFourOfAKindLocked(Boolean.parseBoolean(String.valueOf( playerFields[32]))); 
		p.setFullHouseLocked(Boolean.parseBoolean(String.valueOf( playerFields[33]))); 
		p.setSmallStraightLocked(Boolean.parseBoolean(String.valueOf( playerFields[34]))); 
		p.setLargeStraightLocked(Boolean.parseBoolean(String.valueOf( playerFields[35]))); 
		p.setYahtzeeLocked(Boolean.parseBoolean(String.valueOf( playerFields[36]))); 
		p.setChanceLocked(Boolean.parseBoolean(String.valueOf( playerFields[37]))); 
		p.setYahtzeeBonusLocked(Boolean.parseBoolean(String.valueOf( playerFields[38]))); 
		p.setTotalOfLowerSectionLocked(Boolean.parseBoolean(String.valueOf( playerFields[39]))); 
		p.setGrandTotalLocked(Boolean.parseBoolean(String.valueOf( playerFields[40]))); 
		p.setTimeSaved(String.valueOf( playerFields[41]));		
	}
	
	
	public void createMainPanel() {
		wordsBox = new JTextArea(35,10);

		JScrollPane listScroller = new JScrollPane(wordsBox);
		this.add(listScroller, BorderLayout.CENTER);
		listScroller.setPreferredSize(new Dimension(250, 80));
	}
	
	String[] methods = {"getGameName", "getplayerName", "getRound", "getAces", "getTwos", 
				"getThrees", "getFours", "getFives", "getSixes", "getScoreSubtotalUppersection", 
				"getBonusUpperSection","getGrandTotalUpperSection", "getThreeOfAKind", "getFourOfAKind", 
				"getFullHouse", "getSmallStraight", "getLargeStraight", "getYahtzee", "getChance", "getYahtzeeBonus", 
				"getTotalOfLowerSection", "getGrandTotal", "isAcesLocked", "isTwosLocked", "isThreesLocked", "isFoursLocked", 
				"isFivesLocked", "isSixesLocked" , "isScoreSubtotalUppersectionLocked", "isBonusUpperSectionLocked", 
				"isGrandTotalUpperSectionLocked", "isThreeOfAKindLocked", "isFourOfAKindLocked", "isFullHouseLocked", 
				"isSmallStraightLocked", "isLargeStraightLocked", "isYahtzeeLocked", "isChanceLocked", "isYahtzeeBonusLocked", 
				"isTotalOfLowerSectionLocked", "isGrandTotalLocked"};
	
	private void insertData() {
		try
		{
			for (int i=0; i<41;i++) {
				java.lang.reflect.Method method;
				method = Player.class.getDeclaredMethod(methods[i]);
				String s = String.valueOf(method.invoke(p));
				//System.out.println("String s: "+ s);
				insertStatement.setString(i+1, s);
				//insertStatement.setString(i+1, p.getGameName());
			}
			insertStatement.setString(42, dtf.format(timeSaved));
			insertStatement.execute();
			createMessageBox("Inserted Successfully");
		}
		catch(Exception e)
		{
			//createMessageBox(e.getMessage());
			System.out.println("Error:"+ e.getMessage());
		}
	}
	
	private void createMessageBox(String msg)
	{
		JFrame frame = new JFrame("Result");
		JLabel lbl = new JLabel(msg);
		frame.add(lbl);
		frame.setSize(200,200);
		frame.setVisible(true);
	}
	
	public static void main(String[] main) {
		SaveServer saveServer = new SaveServer();
	}
}
