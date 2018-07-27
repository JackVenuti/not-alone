import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.Random;

public class Game {
	
	static Scanner scan = new Scanner(System.in);
	ArrayList<HuntCard> huntCardDeck = HuntCard.getInitialDeck();
	ArrayList<HuntCard> creatureHand = new ArrayList<HuntCard>();
	int currentPhase;
	boolean playing = true;
	ArrayList<Hunted> hunted = new ArrayList<>();
	int huntTokenPlace;
	int targetTokenPlace;
	int artemiaTokenPlace;
	int artemiaTokenPlace2;
	int ineffectivePlace1;
	int ineffectivePlace2;
	boolean onBeach = false;
	ArrayList<Integer> ableToPlay = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
	int[] available;
	int rescueCounter; //win at 13 artemia begins at 7
	int assimilationCounter; //win at 7
	static Random rand = new Random();
	boolean beachPlayed = false;
	boolean wreckPlayed = false;
	boolean playTwoHuntCards = false; 
	boolean cloned = false;
	boolean stasis = false;
	boolean moveCreatureExtra = false;
	boolean loseExtraWill = false;
	boolean persecution = false;
	boolean loseWillArtemia = false;
	boolean scream = false;
	int chosenPlayer = -1;
	int numPlayers;
	HuntCard lastHuntCard;
	
	public void play() {
		
		startGame();
		System.out.print("How many players? ");
		numPlayers = getInt(scan);
		available = new int[] {numPlayers, numPlayers, numPlayers, numPlayers, numPlayers, 0, 0, 0, 0, 0};
		for (int i = 0; i < numPlayers; i++) {
			Hunted player = new Hunted(i, this);
			hunted.add(player);
		}
		
		rescueCounter = 12 + numPlayers; // win spot counts as 0
		assimilationCounter = -6 - numPlayers; //win spot counts as 0
		
		drawHuntCard();
		drawHuntCard();
		drawHuntCard();
		
		while(playing) {
			
			// chose hunt card to play
			HuntCard hc = chooseHuntCard();
			
			//special cases: flashback and playing two hunt cards
			if (hc.getName().equals("Flashback")) {
				if (lastHuntCard != null) {
					hc = lastHuntCard;
				} else {
					chooseHuntCard();
				}
			}
			HuntCard hc2 = null;
			if (playTwoHuntCards) {
				hc2 = chooseHuntCard();
				playTwoHuntCards = false;
			}
			
			// phase 1
			System.out.println("\n-------PHASE 1-------");
			System.out.println("-----Exploration-----");
			currentPhase = 1;
			if (hc.getPhase() == 1) {
				playHuntCard(hc);
			}
			if (hc2 != null) {
				if (hc2.getPhase() == 1) {
					playHuntCard(hc2);
				}
			}
			phase1();
			
			// phase 2
			System.out.println("\n-------PHASE 2-------");
			System.out.println("-------Hunting-------\n");
			currentPhase = 2;
			if (hc.getPhase() == 2) {
				playHuntCard(hc);
			}
			if (hc2 != null) {
				if (hc2.getPhase() == 2) {
					playHuntCard(hc2);
				}
			}
			phase2();
			
			// phase 3
			System.out.println("\n-------PHASE 3-------");
			System.out.println("------Reckoning------");
			currentPhase = 3;
			if (hc.getPhase() == 3) {
				playHuntCard(hc);
			}
			if (hc2 != null) {
				if (hc2.getPhase() == 3) {
					playHuntCard(hc2);
				}
			}
			phase3();
				
			// phase 4
			System.out.println("\n-------PHASE 4-------");
			System.out.println("-----End of Turn-----\n");
			currentPhase = 4;
			if (hc.getPhase() == 4) {
				playHuntCard(hc);
			}
			if (hc2 != null) {
				if (hc2.getPhase() == 4) {
					playHuntCard(hc2);
				}
			}
			phase4();
			lastHuntCard = hc;
		}
	}
	
	public void phase1() {
		int k = 1;
		for (Hunted player : hunted) {
			System.out.printf("\nPlayer %d (%d will): \n", k++, player.will);
			while (true) {
				System.out.println("1. Resist");
				System.out.println("2. Give up");
				System.out.println("3. Neither");
				System.out.print("Choose one (1, 2, or 3): ");
				int choice = getInt(scan);
				if (choice == 1) {
					System.out.print("How many will are you forfeiting? ");
					int forfeit = getInt(scan);
					if (forfeit >= player.will) {
						System.out.println("You cannot forfeit this many will. You have " + player.will + " Will.");
						continue;
					}
					if (forfeit == 1) {
						player.will--;
						player.returnCards(2);
					}
					else {
						player.will -= 2;
						player.returnCards(4);
					}
				} else if (choice == 2) {
					player.giveUp();
				}
				break;
			}
		}
		
		System.out.println("\nChoose your cards and place them facedown.");
		System.out.print("Press enter when ready");
		scan.nextLine();
	}
	
	public void phase2() {
		// keep track of how many players have played a Place card
		// so that creature doesn't choose to play where no one can go
		int[] allPlayed = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		
		for (Hunted player : hunted) {
			for (int i = 0; i < 10; i++) {
				if (player.getDiscard()[i] == 1) {
					allPlayed[i]++;
				}
			}
		}
		// should choose a random place from the places the hunted can go
		huntTokenPlace = getTargetPlace();
		System.out.printf("Creature token placed on %d\n", huntTokenPlace);
		
		// similarly choose artemia token location if needed
		if (rescueCounter < 7) {
			artemiaTokenPlace = getTargetPlace();
			while (artemiaTokenPlace == huntTokenPlace) {
				artemiaTokenPlace = getTargetPlace();
			}
			System.out.printf("Artemia token placed on %d\n", artemiaTokenPlace);
		}
		System.out.print("Press enter when ready");
		scan.nextLine();
	}
	
	public void phase3() {
		int j = 1;
		for (Hunted player : hunted) {
			if (player.playedArtefact) {
				player.playPlace(j);
			}
			player.playPlace(j);
			j++;
		}
	}
	
	public void phase4() {
		drawHuntCard();
		if (!stasis) {
			moveRescueCounter();
		}
		beachPlayed = false;
		wreckPlayed = false;
		artemiaTokenPlace = 0;
		artemiaTokenPlace2 = 0;
		targetTokenPlace = 0;
		ineffectivePlace1 = 0;
		ineffectivePlace2 = 0;
		chosenPlayer = -1;
		System.out.println("Discard the played Place cards");
		if (!stasis) {
			System.out.println("Move the Rescue token.");
		}
		System.out.print("Press enter when ready");
		stasis = false;
		scan.nextLine();
	}
	
	public void drawHuntCard() {
		HuntCard card = huntCardDeck.remove(0);
		creatureHand.add(card);
	}
	
	public HuntCard chooseHuntCard() {
		int h = rand.nextInt(3);
		// to make sure that it doesn't pick flashback as the first card
		while (creatureHand.get(h).getName().equals("Flashback") && lastHuntCard == null) {
			h = rand.nextInt(3);
		}
		return creatureHand.remove(h);
	}
	
	public String getPlace(int num) {
		switch (num) {
			case 1:
				return "The Lair"; 
			case 2:
				return "The Jungle"; 
			case 3:
				return "The River"; 
			case 4:
				return "The Beach"; 
			case 5: 
				return "The Rover";
			case 6:
				return "The Swamp";
			case 7:
				return "The Shelter";
			case 8:
				return "The Wreck";
			case 9:
				return "The Source";
			case 10:
				return "The Artefact";
			default:
				return null;
		}
	}
	
	public int getInt(Scanner s) {
		int n = s.nextInt();
		s.nextLine();
		return n;
	}
	
	public void startGame() {
		System.out.println(""+                                                                                                                                                                     
                
		"NNNNNNNN        NNNNNNNN                          tttt                              AAA               lllllll\n" +                                                        
		"N:::::::N       N::::::N                       ttt:::t                             A:::A              l:::::l\n" +                                                        
		"N::::::::N      N::::::N                       t:::::t                            A:::::A             l:::::l\n" +                                                        
		"N:::::::::N     N::::::N                       t:::::t                           A:::::::A            l:::::l\n" +                                                        
		"N::::::::::N    N::::::N   ooooooooooo   ttttttt:::::ttttttt                    A:::::::::A            l::::l    ooooooooooo   nnnn  nnnnnnnn        eeeeeeeeeeee\n" +    
		"N:::::::::::N   N::::::N oo:::::::::::oo t:::::::::::::::::t                   A:::::A:::::A           l::::l  oo:::::::::::oo n:::nn::::::::nn    ee::::::::::::ee\n" +  
		"N:::::::N::::N  N::::::No:::::::::::::::ot:::::::::::::::::t                  A:::::A A:::::A          l::::l o:::::::::::::::on::::::::::::::nn  e::::::eeeee:::::ee\n" +
		"N::::::N N::::N N::::::No:::::ooooo:::::otttttt:::::::tttttt                 A:::::A   A:::::A         l::::l o:::::ooooo:::::onn:::::::::::::::ne::::::e     e:::::e\n" +
		"N::::::N  N::::N:::::::No::::o     o::::o      t:::::t                      A:::::A     A:::::A        l::::l o::::o     o::::o  n:::::nnnn:::::ne:::::::eeeee::::::e\n" +
		"N::::::N   N:::::::::::No::::o     o::::o      t:::::t                     A:::::AAAAAAAAA:::::A       l::::l o::::o     o::::o  n::::n    n::::ne:::::::::::::::::e\n" + 
		"N::::::N    N::::::::::No::::o     o::::o      t:::::t                    A:::::::::::::::::::::A      l::::l o::::o     o::::o  n::::n    n::::ne::::::eeeeeeeeeee\n" +  
		"N::::::N     N:::::::::No::::o     o::::o      t:::::t    tttttt         A:::::AAAAAAAAAAAAA:::::A     l::::l o::::o     o::::o  n::::n    n::::ne:::::::e\n" +           
		"N::::::N      N::::::::No:::::ooooo:::::o      t::::::tttt:::::t        A:::::A             A:::::A   l::::::lo:::::ooooo:::::o  n::::n    n::::ne::::::::e\n" +          
		"N::::::N       N:::::::No:::::::::::::::o      tt::::::::::::::t       A:::::A               A:::::A  l::::::lo:::::::::::::::o  n::::n    n::::n e::::::::eeeeeeee\n" +  
		"N::::::N        N::::::N oo:::::::::::oo         tt:::::::::::tt      A:::::A                 A:::::A l::::::l oo:::::::::::oo   n::::n    n::::n  ee:::::::::::::e\n" +  
		"NNNNNNNN         NNNNNNN   ooooooooooo             ttttttttttt       AAAAAAA                   AAAAAAAllllllll   ooooooooooo     nnnnnn    nnnnnn    eeeeeeeeeeeeee\n\n"  
		                                                                                                                                                                     
		                                                                                                                                                                     
		                                                                                                                                                                     
		                                                                                                                                                                     
		                                                                                                                                                                     
		                                                                                                                                                                     
		                                                                                                                                                                     );
				System.out.println("--------------------------------------------A Stronghold Game by Ghislain Masson with Artwork by Sebastien Caiveau-----------------------------------------------------");
				System.out.print("-------------------------------------------------------------------------Press ENTER to begin--------------------------------------------------------------------------");
				scan.nextLine();
	}

	public int getTargetPlace() {
		
		int[] allPlayed = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		
		for (Hunted player : hunted) {
			for (int i = 0; i < 10; i++) {
				if (player.getDiscard()[i] == 1) {
					allPlayed[i]++;
				}
			}
		}
		// should choose a random place from the places the hunted can go
		int tokenPlace = ableToPlay.get(rand.nextInt(ableToPlay.size()));
		while (allPlayed[tokenPlace-1] == available[tokenPlace-1]) {
			tokenPlace = ableToPlay.get(rand.nextInt(ableToPlay.size()));
		}
		return tokenPlace;
	}
	
	public int getPlayer() {
		return rand.nextInt(hunted.size());
	}
	
	public void playHuntCard(HuntCard card) {
		if (card == lastHuntCard) {
			System.out.println("Creature played Flashback");
			System.out.println("Copy the last Hunt card you discarded.");
		}
		System.out.println("Creature played " + card.getName());
		System.out.println(card.getPower() + "\n");
		//TODO Tracking - NOPE THIS IS NOT WORKING 
		if (card.getName().equals("Tracking")) {
			playTwoHuntCards = true;
		}
		//Forbidden Zone
		if (card.getName().equals("Forbidden Zone")) {
			int j = 1;
			for (Hunted player : hunted) {
				player.discardPlaceCard(j);
				j++;
			}
		}
		//Interference
		if (card.getName().equals("Interference")) {
			ineffectivePlace1 = 4;
			ineffectivePlace2 = 8;
		}
		//Cataclysm
		if (card.getName().equals("Cataclysm")) {
			ineffectivePlace1 = getTargetPlace();
			while (ineffectivePlace1 == huntTokenPlace || ineffectivePlace1 == 0) {
				ineffectivePlace1 = getTargetPlace();
			}
			System.out.printf("Creature chooses %d to be ineffective this round.\n", ineffectivePlace1);
		}
		//Stasis
		if (card.getName().equals("Stasis")) {
			stasis = true;
		}
		//Anticipation
		if (card.getName().equals("Anticipation")) {
			chosenPlayer = getPlayer();
			System.out.printf("Creature choses player %d as the target player.\n", chosenPlayer + 1);
		}
		//TODO Detour
		if (card.getName().equals("Detour")) {
			//im just getting lazy
			//unfortunately, our creature chooses to play this card before place cards
			//are revealed so then they could play this card when none of the hunted are 
			//in a place adjacent to their creature token, which wouldn't be ideal
			//but the way we have it now, we probably would just have it looks at all the places
			//where the hunted are, and if it is adjacent to a tokenPlace, then move them there?
			//yeah ill do this later
		}
		//Fierceness
		if (card.getName().equals("Fierceness")) {
			loseExtraWill = true;
		}
		//Ascendancy
		if (card.getName().equals("Ascendancy")) {
			chosenPlayer = getPlayer(); //choose a hunted
			System.out.printf("Creature choses player %d as the target player.\n", chosenPlayer + 1);
			//call discardPlaceCard until the size of their hand is 2
			while (IntStream.of(hunted.get(chosenPlayer).getHand()).sum() > 2) {
				hunted.get(chosenPlayer).discardPlaceCard(chosenPlayer+1);
			}
		}
		//Persecution
		if (card.getName().equals("Persecution")) {
			persecution = true;
		}
		//Scream
		if (card.getName().equals("Scream")) {
			scream = true;
			while (targetTokenPlace == huntTokenPlace || targetTokenPlace == 0) {
				targetTokenPlace = getTargetPlace();
			}
			System.out.printf("Target token placed on %d", targetTokenPlace);
		}
		//Force Field
		// I think it will still allow the player to play this card atm
		if (card.getName().equals("Force Field")) {
			while (!checkAdjacency(ineffectivePlace1, ineffectivePlace2)) {
				ineffectivePlace1 = getTargetPlace();
				ineffectivePlace2 = getTargetPlace();
			}
			System.out.printf("Target token placed on %d and %d\n", ineffectivePlace1, ineffectivePlace2);
		}
		//Toxin
		if (card.getName().equals("Toxin")) {

			ineffectivePlace1 = getTargetPlace();
			System.out.printf("/nHunted chooses %d to be ineffective this round. Players must also discard 1 Survival card. \n", ineffectivePlace1);
			//thus far, we haven't accounted for survival cards
			//probably will also need a boolean that tells the player to discard a survival card?
		}
		//Mirage
		if (card.getName().equals("Mirage")) {
			int a = 0;
			while (!checkAdjacency(ineffectivePlace1, ineffectivePlace2) && a < 100) {
				ineffectivePlace1 = getTargetPlace();
				ineffectivePlace2 = getTargetPlace();
				a++;
			}
			int b = 1;
			while (a == 100 && !checkAdjacency(ineffectivePlace1, ineffectivePlace2)) {
				ineffectivePlace1 = getTargetPlace();
				ineffectivePlace2 = ineffectivePlace1 + b;
			}
			System.out.printf("Target token placed on %d and %d\n", ineffectivePlace1, ineffectivePlace2);
		}
		//Clone
		if (card.getName().equals("Clone")) {
			cloned = true;
			while (targetTokenPlace == huntTokenPlace || targetTokenPlace == 0) {
				targetTokenPlace = getTargetPlace();
			}
			System.out.printf("Target token placed on %d", targetTokenPlace);
		}
		//Virus
		if (card.getName().equals("Virus")) {
			//putting a single token on more than one place?
			//yeah probably need to be able to do this for other cards too, so 
			//maybe instead of an int, it could be an int array?
			int a = 0;
			while (!checkAdjacency(artemiaTokenPlace, artemiaTokenPlace2) && a < 100) {
				artemiaTokenPlace = getTargetPlace();
				artemiaTokenPlace2 = getTargetPlace();
				a++;
			}
			while (a == 100 && !checkAdjacency(artemiaTokenPlace, artemiaTokenPlace2)) {
				artemiaTokenPlace = getTargetPlace();
				artemiaTokenPlace2 = artemiaTokenPlace + 1;
			}
			System.out.printf("Artemia token placed on %d and %d\n", artemiaTokenPlace, artemiaTokenPlace2);
		}
		//Mutation
		if (card.getName().equals("Mutation")) {
			//place artemia token
			while (artemiaTokenPlace == huntTokenPlace || artemiaTokenPlace == 0) {
				artemiaTokenPlace = getTargetPlace();
			}
			System.out.printf("Artemia token placed on %d", artemiaTokenPlace);
			loseWillArtemia = true;
			//boolean to lose a will when landed on artemia token
		}
		//TODO Phobia
		if (card.getName().equals("Phobia")) {
			//this seems a little bit silly for the computer really...
			//the computer should know what cards you have in your hand
			//though I guess it tells you where the hunted did not go?
			//im not sure how to do this anyway, ill probably do it last
			//and the computer in the meantime can just do its normal thing
		}
		//Despair
		if (card.getName().equals("Despair")) {
			//nothing needs to be done here, assuming that the hunted play according to the rules 
		}
		// Flashback
		if (card.getName().equals("Flashback")) {
			//actually i dont think we need to do anything here
		}
	}
	
	public void moveRescueCounter() {
		rescueCounter--;
		if (rescueCounter < 1) {
			System.out.println("Hunted have won the game. Congratulations!");
			System.exit(0);
		}
	}
	
	public void moveAssimilationCounter() {
		if (moveCreatureExtra) {
			assimilationCounter++;
			moveCreatureExtra = false;
		}
		assimilationCounter++;
		if (assimilationCounter > -1) {
			System.out.println("The creature has won the game.");
			System.exit(0);
		}
	}
	
	public boolean checkAdjacency(int place1, int place2) {
		switch (place1) {
		case 1:
			if (place2 == 2 || place2 == 6) {
				return true;
			} return false;
		case 2:
			if (place2 == 1 || place2 == 3 || place2 == 7) {
				return true;
			} return false;
		case 3:
			if (place2 == 2 || place2 == 4 || place2 == 8) {
				return true;
			} return false;
		case 4:
			if (place2 == 3 || place2 == 5 || place2 == 9) {
				return true;
			} return false;
		case 5:
			if (place2 == 4 || place2 == 10) {
				return true;
			} return false;
		case 6:
			if (place2 == 1 || place2 == 7) {
				return true;
			} return false;
		case 7:
			if (place2 == 2|| place2 == 6|| place2 == 8) {
				return true;
			} return false;
		case 8:
			if (place2 == 3 || place2 == 7 || place2 == 9) {
				return true;
			} return false;
		case 9:
			if (place2 == 4 || place2 == 8 || place2 == 10) {
				return true;
			} return false;
		case 10:
			if (place2 == 5 || place2 == 9) {
				return true;
			} return false;
		}
		return false;
	}
}
