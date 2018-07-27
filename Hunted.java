import java.util.Scanner;

//import java.util.ArrayList;

// representing one of the people being hunted
public class Hunted {
	static Scanner scan = new Scanner(System.in);
	Game game;
	int will;
	int playerNum;
	//ArrayList<Place> hand;
	int[] hand;
	int[] discard;
	boolean playedArtefact;
	
	public Hunted(int playerNum, Game game) {
		this.playerNum = playerNum;
		this.game = game;
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
	
	public void applyPlaceCard(int place, boolean copied) {
		playedArtefact = false;
		switch (place) {
		//the lair
		case 1:
			System.out.println("1. Take back to your hand the Place cards from your discard pile");
			System.out.println("2. Copy the power of the place card with the Creature token");
			System.out.print("Choose one (1 or 2): ");
			int choice = getInt(scan);
			if (choice == 1) {
				if (!game.persecution) {
					resetDiscard();
				} else {
					System.out.println("You may only take back 1 place card this turn.");
					returnCards(1);
					game.persecution = true;
				}
			} else {
				if (game.huntTokenPlace == 10) {
					System.out.println("You may not copy The Artefact. Choose again.");
					applyPlaceCard(game.huntTokenPlace, false);
				}
				System.out.println("Choose option 2");
				applyPlaceCard(game.huntTokenPlace, true);
			}
			
			getDiscard()[place-1] = 1;
			break;
		//the jungle
		case 2:
			returnCards(1);
			
			if (copied) {
				getDiscard()[0] = 0;
			}
			break;
		//the river
		case 3:
			System.out.println("Next turn, play 2 Place cards. Before revealing, choose one and return the second to your hand");
			if (!copied)
				getDiscard()[place-1] = 1;
			break;
		//the beach
		case 4:
			game.beachPlayed = true;
			//move marker onto/off of beach
			if (game.onBeach) {
				System.out.println("Marker counter removed from beach, move the Rescue counter forward 1 space.");
				game.onBeach = false;
				game.moveRescueCounter();
			} else {
				System.out.println("Marker counter placed on the Beach.");
				game.onBeach = true;
			}
			if (!copied)
				getDiscard()[place-1] = 1;
			break;
		//the rover
		case 5:
			//choose new card that is not already in hand/discard
			System.out.print("Choose a new card to add to your hand. ");
			choice = getInt(scan);
			hand[choice-1] = 1; // adds newly choosen card to hand
			game.ableToPlay.add(choice); // informs creature that this card is now a place hunted can go
			game.available[choice-1]++;
			if (!copied)
				getDiscard()[place-1] = 1;
			break;
		//the swamp
		case 6:
			//choose two cards to take back + the jungle stays in hand
			if (!game.persecution) {
				returnCards(2);
			} else {
				System.out.println("You may only take back 1 place card this turn.");
				returnCards(1);
				game.persecution = false;
			}
			if (copied) {
				getDiscard()[0] = 0;
			}
			break;
		//the shelter
		case 7:
			System.out.println("Draw 2 Survival cards, choose one and discard the second.");
			if (!copied)
				getDiscard()[place-1] = 1;
			break;
		//the wreck
		case 8:
			game.wreckPlayed = true;
			// move up counter if we decide to have game take care of this
			System.out.println("Move the Rescue counter forward 1 space.");
			game.moveRescueCounter();
			if (!copied)
				getDiscard()[place-1] = 1;
			break;
		//the source
		case 9:
			System.out.println("1. Choose a Hunted to regain 1 Will.");
			System.out.println("2. Draw 1 Survival card.");
			System.out.print("Choose one (1 or 2): ");
			choice = getInt(scan);
			if (choice == 1) {
				System.out.print("Which player would you like to regain Will? ");
				choice = getInt(scan);
				if (game.hunted.get(choice-1).will == 3) {
					System.out.println("This player already has 3 Will.");
				}
				game.hunted.get(choice-1).will++;
			} else {
				drawSurvivalCard();
			}
			
			if (!copied)
				getDiscard()[place-1] = 1;
			break;
		//the artefact
		case 10:
			System.out.println("Next turn, play 2 Place cards. Resolve both Places.");
			
			playedArtefact = true;
			getDiscard()[place-1] = 1;
			break;
		}
	}
	
	public void playPlace(int j) {
		System.out.printf("\nPlayer %d: Reveal your place card. ", j);
		int place = getInt(scan);
		// check to make sure entered card is not in their discard
		if (getDiscard()[place-1] == 1) {
			System.out.println("You cannot play this card. It is discarded.");
			playPlace(j);
		} else if (getHand()[place-1] == 0) { // check to make sure entered card is in their hand
			System.out.println("You cannot play this card. You do not have it.");
			playPlace(j);
		} else if (place == game.ineffectivePlace1 || place == game.ineffectivePlace2) {
			System.out.println("This place is ineffective this round.");
			getDiscard()[place-1] = 1;
		} else {
			System.out.println(game.getPlace(place));
			// check if they were caught by any tokens
			if (place == game.huntTokenPlace || (game.cloned && place == game.targetTokenPlace)) {
				game.cloned = false;
				if (playerNum == game.chosenPlayer) {
					game.moveCreatureExtra = true;
				}
				System.out.printf("You have been caught. -%d Will. Place ineffective. Move the Assimilation token.\n",(place == 1 ? 2 : 1));
				if (game.loseExtraWill) {
					System.out.println("You lose an additional will due to Fierceness.");
					will --;
					game.loseExtraWill = false;
				}
				if (place == 1) {
					will -= 2;
				} else {
					will--;
				}
				game.moveAssimilationCounter();
				if (will < 1) {
					giveUp();
				}
				getDiscard()[place-1] = 1;
			} else if (place == game.artemiaTokenPlace || place == game.artemiaTokenPlace2){ 
				System.out.println("You are caught by the assimilation token. Place ineffective. Discard 1 Place card: ");
				getDiscard()[place-1] = 1;
				discardPlaceCard(1);
				if (game.loseWillArtemia) {
					System.out.println("You also lose 1 additional Will due to the Artemia token.");
					will--;
					if (will < 1) {
						giveUp();
					}
				}
			} else if (place == game.targetTokenPlace && game.scream == true) {
				System.out.printf("\nPlayer %d (%d will): \n", playerNum, will);
				System.out.println("1. Discard 2 place cards.");
				System.out.println("2. Lose 1 will.");
				System.out.print("Choose one (1 or 2): ");
				int choice = getInt(scan);
				if (choice == 1) {
					discardPlaceCard(playerNum+1);
					discardPlaceCard(playerNum+1);
				}
				if (choice == 2) {
					will--;
				}
			} else {
				// ask if they would like to use places power or take back 1 place card
				System.out.println("1. Use the place's power.");
				System.out.println("2. Take back 1 discarded Place card.");
				System.out.print("Choose one (1 or 2): ");
				int choice = getInt(scan);
				if (choice == 1) {
					// TODO if beachPlayed and place is 4 or wreckPlayed and place is 8, don't let them apply place card
					applyPlaceCard(place, false);
				} else {
					returnCards(1); //return one card to hand
					getDiscard()[place-1] = 1; // puts used card into discard pile
				}
			}
			j++;
		}
	}
	
	public void returnCards(int n) {
		boolean noDiscards = true;
		for (int h = 0; h < getDiscard().length; h++) {
			if (getDiscard()[h] == 1) {
				noDiscards = false;
			}
		}
		if (noDiscards) {
			System.out.println("You have nothing in your discards.");
			return;
		}
		System.out.println("Choose " + n + " cards to return to your hand.");
		for (int i = 1; i <= n; i++) {
			System.out.print("Card "+i+": ");
			int choice = getInt(scan);
			// something is going wrong here, if they try to choose again, it fails?
			// infinite loop if there is nothing in your discard pile, need to check
			if (getDiscard()[choice-1] == 0) {
				System.out.println("This card is not in your discard pile. Choose again.");
				returnCards(n - (i) + 1);
			}
			getDiscard()[choice-1] = 0;
		}
	}
	
	public void giveUp() {
		System.out.println("Regain all Will counters and take back all of discarded Place cards. Move forward the Assimilation counter.");
		setWill(3);
		resetDiscard();
		game.moveAssimilationCounter();
	}
	
	public int getInt(Scanner s) {
		int n = s.nextInt();
		s.nextLine();
		return n;
	}
	
	public void discardPlaceCard(int j) {
		System.out.printf("\nPlayer %d: You must discard a place card", j);
		System.out.print("\nCard to discard: ");
		int place = getInt(scan);
		// check to make sure entered card is not in their discard
		if (getDiscard()[place-1] == 1) {
			System.out.println("You cannot play this card. It is discarded.");
			discardPlaceCard(j);
		} else if (getHand()[place-1] == 0) { // check to make sure entered card is in their hand
			System.out.println("You cannot play this card. You do not have it.");
			discardPlaceCard(j);
		} else {
			getDiscard()[place-1] = 1;
		}
	}
}
