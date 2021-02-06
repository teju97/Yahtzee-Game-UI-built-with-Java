package game;

public class Player implements java.io.Serializable {
	private static int next_id = 0;
	private int playerId;
	private String playerName, gameName, timeSaved;
	private int round=1, aces, twos, threes, fours, fives, sixes, scoreSubtotalUppersection, bonusUpperSection, grandTotalUpperSection, threeOfAKind, fourOfAKind, fullHouse, smallStraight, largeStraight, yahtzee, chance, yahtzeeBonus, totalOfLowerSection, grandTotal;
	private boolean acesLocked, twosLocked, threesLocked, foursLocked, fivesLocked, sixesLocked, scoreSubtotalUppersectionLocked, bonusUpperSectionLocked, grandTotalUpperSectionLocked, threeOfAKindLocked, fourOfAKindLocked, fullHouseLocked, smallStraightLocked, largeStraightLocked, yahtzeeLocked, chanceLocked, yahtzeeBonusLocked, totalOfLowerSectionLocked, grandTotalLocked;
	//private int die1, die2, die3, die4, die5;
	
	public Player(){
		this.playerId = next_id;
		next_id++;
	}
	
	public Player(String name){
		this.playerId = next_id;
		next_id++;
		this.playerName = name;
	}
	
	public int getRound() {
		return this.round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public boolean isAcesLocked() {
		return this.acesLocked;
	}

	public void setAcesLocked(boolean acesLocked) {
		this.acesLocked = acesLocked;
	}

	public boolean isFoursLocked() {
		return this.foursLocked;
	}

	public void setFoursLocked(boolean foursLocked) {
		this.foursLocked = foursLocked;
	}

	public boolean isFivesLocked() {
		return this.fivesLocked;
	}

	public void setFivesLocked(boolean fivesLocked) {
		this.fivesLocked = fivesLocked;
	}

	public boolean isBonusUpperSectionLocked() {
		return this.bonusUpperSectionLocked;
	}

	public void setBonusUpperSectionLocked(boolean bonusUpperSectionLocked) {
		this.bonusUpperSectionLocked = bonusUpperSectionLocked;
	}

	public boolean isGrandTotalUpperSectionLocked() {
		return this.grandTotalUpperSectionLocked;
	}

	public void setGrandTotalUpperSectionLocked(boolean grandTotalUpperSectionLocked) {
		this.grandTotalUpperSectionLocked = grandTotalUpperSectionLocked;
	}

	public boolean isFourOfAKindLocked() {
		return this.fourOfAKindLocked;
	}

	public void setFourOfAKindLocked(boolean fourOfAKindLocked) {
		this.fourOfAKindLocked = fourOfAKindLocked;
	}

	public boolean isFullHouseLocked() {
		return this.fullHouseLocked;
	}

	public void setFullHouseLocked(boolean fullHouseLocked) {
		this.fullHouseLocked = fullHouseLocked;
	}

	public boolean isLargeStraightLocked() {
		return this.largeStraightLocked;
	}

	public void setLargeStraightLocked(boolean largeStraightLocked) {
		this.largeStraightLocked = largeStraightLocked;
	}

	public boolean isChanceLocked() {
		return this.chanceLocked;
	}

	public void setChanceLocked(boolean chanceLocked) {
		this.chanceLocked = chanceLocked;
	}

	public boolean isGrandTotalLocked() {
		return this.grandTotalLocked;
	}

	public void setGrandTotalLocked(boolean grandTotalLocked) {
		this.grandTotalLocked = grandTotalLocked;
	}

	public int getAces() {
		return this.aces;
	}

	public void setAces(int aces) {
		this.aces = aces;
	}

	public int getTwos() {
		return this.twos;
	}

	public void setTwos(int twos) {
		this.twos = twos;
	}

	public int getThrees() {
		return this.threes;
	}

	public void setThrees(int threes) {
		this.threes = threes;
	}

	public int getFours() {
		return this.fours;
	}

	public void setFours(int fours) {
		this.fours = fours;
	}

	public int getFives() {
		return this.fives;
	}

	public void setFives(int fives) {
		this.fives = fives;
	}

	public int getSixes() {
		return this.sixes;
	}

	public void setSixes(int sixes) {
		this.sixes = sixes;
	}

	public int getScoreSubtotalUppersection() {
		return this.scoreSubtotalUppersection;
	}

	public void setScoreSubtotalUppersection(int scoreSubtotalUppersection) {
		this.scoreSubtotalUppersection = scoreSubtotalUppersection;
	}

	public int getBonusUpperSection() {
		return this.bonusUpperSection;
	}

	public void setBonusUpperSection(int bonusUpperSection) {
		this.bonusUpperSection = bonusUpperSection;
	}

	public int getGrandTotalUpperSection() {
		return this.grandTotalUpperSection;
	}

	public void setGrandTotalUpperSection(int grandTotalUpperSection) {
		this.grandTotalUpperSection = grandTotalUpperSection;
	}

	public int getThreeOfAKind() {
		return this.threeOfAKind;
	}

	public void setThreeOfAKind(int threeOfAKind) {
		this.threeOfAKind = threeOfAKind;
	}

	public int getFourOfAKind() {
		return this.fourOfAKind;
	}

	public void setFourOfAKind(int fourOfAKind) {
		this.fourOfAKind = fourOfAKind;
	}

	public int getFullHouse() {
		return this.fullHouse;
	}

	public void setFullHouse(int fullHouse) {
		this.fullHouse = fullHouse;
	}

	public int getSmallStraight() {
		return this.smallStraight;
	}

	public void setSmallStraight(int smallStraight) {
		this.smallStraight = smallStraight;
	}

	public int getLargeStraight() {
		return this.largeStraight;
	}

	public void setLargeStraight(int largeStraight) {
		this.largeStraight = largeStraight;
	}

	public int getYahtzee() {
		return this.yahtzee;
	}

	public void setYahtzee(int yahtzee) {
		this.yahtzee = yahtzee;
	}

	public int getChance() {
		return this.chance;
	}

	public void setChance(int chance) {
		this.chance = chance;
	}

	public int getYahtzeeBonus() {
		return this.yahtzeeBonus;
	}

	public void setYahtzeeBonus(int yahtzeeBonus) {
		this.yahtzeeBonus = yahtzeeBonus;
	}

	public int getTotalOfLowerSection() {
		return this.totalOfLowerSection;
	}

	public void setTotalOfLowerSection(int totalOfLowerSection) {
		this.totalOfLowerSection = totalOfLowerSection;
	}

	public int getGrandTotal() {
		return this.grandTotal;
	}

	public void setGrandTotal(int grandTotal) {
		this.grandTotal = grandTotal;
	}

	public int getPlayerId() {
		return this.playerId;
	}

	public String getplayerName() {
		return this.playerName;
	}

	public boolean isTotalOfLowerSectionLocked() {
		return totalOfLowerSectionLocked;
	}

	public void setTotalOfLowerSectionLocked(boolean totalOfLowerSectionLocked) {
		this.totalOfLowerSectionLocked = totalOfLowerSectionLocked;
	}

	public boolean isTwosLocked() {
		return twosLocked;
	}

	public void setTwosLocked(boolean twosLocked) {
		this.twosLocked = twosLocked;
	}

	public boolean isThreesLocked() {
		return threesLocked;
	}

	public void setThreesLocked(boolean threesLocked) {
		this.threesLocked = threesLocked;
	}

	public boolean isSixesLocked() {
		return sixesLocked;
	}

	public void setSixesLocked(boolean sixesLocked) {
		this.sixesLocked = sixesLocked;
	}

	public boolean isScoreSubtotalUppersectionLocked() {
		return scoreSubtotalUppersectionLocked;
	}

	public void setScoreSubtotalUppersectionLocked(boolean scoreSubtotalUppersectionLocked) {
		this.scoreSubtotalUppersectionLocked = scoreSubtotalUppersectionLocked;
	}

	public boolean isThreeOfAKindLocked() {
		return threeOfAKindLocked;
	}

	public void setThreeOfAKindLocked(boolean threeOfAKindLocked) {
		this.threeOfAKindLocked = threeOfAKindLocked;
	}

	public boolean isSmallStraightLocked() {
		return smallStraightLocked;
	}

	public void setSmallStraightLocked(boolean smallStraightLocked) {
		this.smallStraightLocked = smallStraightLocked;
	}

	public boolean isYahtzeeLocked() {
		return yahtzeeLocked;
	}

	public void setYahtzeeLocked(boolean yahtzeeLocked) {
		this.yahtzeeLocked = yahtzeeLocked;
	}

	public boolean isYahtzeeBonusLocked() {
		return yahtzeeBonusLocked;
	}

	public void setYahtzeeBonusLocked(boolean yahtzeeBonusLocked) {
		this.yahtzeeBonusLocked = yahtzeeBonusLocked;
	}

	public String getGameName() {
		return this.gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	
	public void setplayerName(String playerName) {
		this.playerName = playerName;
	}

	public void setTimeSaved(String timeSaved) {
		this.timeSaved = timeSaved;
	}

}
