import java.util.ArrayList;

public class Creature {

	ArrayList<HuntCard> hand;
}

/*
 * List of stupid things the computer does (not bugs, just not good choice in how it is programmed)
 * 
 * 1. Computer plays a Hunt card that made a place ineffective, but still plays the Creature token there.
 * Not that this is always bad. I mean people could go to those places thinking they would be safe there
 * But it seems kinda silly to me
 * 
 * 2. Computer will put tokens on the same place as other tokens, especially when one of those tokens is placed
 * on two places at once.
 * 
 * 
 * 
 * Actual problems to be dealt with:
 * 
 * 1. Because the computer is placing tokens in the same place as ineffective places, the ineffective problem happens first,
 * so it doesn't recognize that the player got caught
 * 
 * 2. Choosing adjacent spots considers what places the people have already played, and will only choose places where hunted
 * havent already gone. This causes a problem if there are no adjacent spots where players could play 
 * (i.e. if in a single player game, they only have 2, 4 and 6. none of these are adjacent and it will not chose any other spots)
 */
