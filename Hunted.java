//import java.util.ArrayList;

// representing one of the people being hunted
public class Hunted {
	int will;
	//ArrayList<Place> hand;
	int[] hand;
	int[] discard;
	boolean playedArtefact;
	
	public Hunted() {
		will = 3;
		hand = new int[] {1, 1, 1, 1, 1, 0, 0, 0, 0, 0}; // 1 for cards we have in hand, 0 for ones we do not
		discard = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // 1 for cards in discard, 0 for ones not
		playedArtefact = false;
	}
	
	public void drawSurvivalCard() {
		//TODO
	}
	
	public int getWill() {
		return will;
	}

	public void setWill(int will) {
		this.will = will;
	}

	public int[] getHand() {
		return hand;
	}

	public void setHand(int[] hand) {
		this.hand = hand;
	}

	public int[] getDiscard() {
		return discard;
	}

	public void setDiscard(int[] discard) {
		this.discard = discard;
	}
	
	public void resetDiscard() {
		discard = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	}
}
