import java.util.ArrayList;
import java.util.Collections;

public class HuntCard {

	int phase;
	boolean target;
	boolean artemia;
	int choose;
	String name;
	String power;
	int choosePlace;
	int chooseHunted;
	
	public HuntCard(String name, String power, int phase, boolean target, boolean artemia, int choosePlace, int chooseHunted) {
		this.phase = phase;
		this.target = target;
		this.artemia = artemia;
		this.name = name;
		this.power = power;
		this.choosePlace = choosePlace;
		this.chooseHunted = chooseHunted;
	}
	
	public int getPhase() {
		return phase;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}
	
	public int getChoose() {
		return choose;
	}

	public void setChoose(int choose) {
		this.choose = choose;
	}

	public boolean isTarget() {
		return target;
	}

	public void setTarget(boolean target) {
		this.target = target;
	}

	public boolean isArtemia() {
		return artemia;
	}

	public void setArtemia(boolean artemia) {
		this.artemia = artemia;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		this.power = power;
	}


	public static ArrayList<HuntCard> getInitialDeck() {
		ArrayList<HuntCard> deck = new ArrayList<HuntCard>();
		
		// create every card and add to deck
		HuntCard hc1 = new HuntCard("Tracking", "Next turn, you may play up to 2 Hunt cards", 4, false, false, 0, 0); deck.add(hc1);
		HuntCard hc2 = new HuntCard("Forbidden Zone", "All Hunted discard 1 Place card simultaneously", 2, false, false, 0, 0); deck.add(hc2);
		HuntCard hc3 = new HuntCard("Interference", "The powers of the Beach and the Wreck and ineffective", 2, false, false, 0, 0); deck.add(hc3);
		HuntCard hc4 = new HuntCard("Cataclysm", "The place's power of your choice is ineffective", 3, false, false, 1, 0); deck.add(hc4);// how to have it choose 
		HuntCard hc5 = new HuntCard("Stasis", "Prevent the Rescue counter moving forward during this phase", 4, false, false, 0, 0); deck.add(hc5);
		HuntCard hc6 = new HuntCard("Anticipation", "Choose one Hunted. If you catch him with the Creature token, move the Assimilation counter forward 1 extra space.", 2, false, false, 0, 1); deck.add(hc6);
		HuntCard hc7 = new HuntCard("Detour", "After the Hunted reveal their Place cards, move one Hunted to an adjacent place", 3, false, false, 0, 1); deck.add(hc7);
		HuntCard hc8 = new HuntCard("Fierceness", "Hunted caught by the Creature token will lose 1 extra Will", 2, false, false, 0, 0); deck.add(hc8);
		HuntCard hc9 = new HuntCard("Ascendancy", "Force one Hunted to discard all but 2 Place cards from his hand", 2, false, false, 0, 1); deck.add(hc9);
		HuntCard hc10 = new HuntCard("Persecution", "Each Hunted may only take back 1 Place card when using the power of a Place card", 2, false, false, 0, 0); deck.add(hc10);
		HuntCard hc11 = new HuntCard("Scream", "Each Hunted on the target place must discard 2 Place cards or lose 1 Will", 2, true, false, 1, 0); deck.add(hc11);
		HuntCard hc12 = new HuntCard("Force Field", "Before the Hunted play, target 2 adjacent places. Neither may be played this turn.", 1, true, false, 2, 0); deck.add(hc12);
		HuntCard hc13 = new HuntCard("Toxin", "Each Hunted on the targeted place discards 1 Survival card. The power of the place is ineffective.", 2, true, false, 1, 0); deck.add(hc13);
		HuntCard hc14 = new HuntCard("Mirage", "Target 2 adjacent places. Both are ineffective.", 2, true, false, 2, 0); deck.add(hc14);
		HuntCard hc15 = new HuntCard("Clone", "Consider the Target token as a second Creature token.", 2, true, false, 1, 0); deck.add(hc15);
		HuntCard hc16 = new HuntCard("Virus", "Target 2 adjacent places. Apply the effects of the Artemia token on both places.", 2, false, true, 2, 9); deck.add(hc16);
		HuntCard hc17 = new HuntCard("Mutation", "In addition to its effects, the Artemia token inflicts the loss of 1 Will", 2, false, true, 1, 0); deck.add(hc17);
		HuntCard hc18 = new HuntCard("Phobia", "Force one Hunted to show you all but 2 Place cards from his hand.", 2, false, true, 1, 1); deck.add(hc18);
		HuntCard hc19 = new HuntCard("Despair", "No Survival cards may be played or drawn for the remainder of the turn.", 1, false, false, 0, 0); deck.add(hc19);
		HuntCard hc20 = new HuntCard("Flashback", "Copy the last Hunt card you discarded.", 0, false, false, 0, 0); deck.add(hc20);
		Collections.shuffle(deck);
		return deck;
	}

}