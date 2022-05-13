package com.majisto.game.logic;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static com.majisto.game.logic.Players.COMPUTER;
import static com.majisto.game.logic.Players.HUMAN;
import static com.majisto.game.logic.PositionToOtherCard.*;

/**
 * tripleTriad Created by Majisto on 4/28/2022.
 */
public class Game {

    Map<Integer, Card> masterCardMap = new HashMap<>();
    List<Card> masterCardList;
    Board board;
    Hand human;
    Hand computer;
    Random r = new Random();

    public void playGame () throws IOException {
        masterCardList = new CsvToBeanBuilder<Card>(new FileReader("C:\\Users\\Majisto\\Desktop\\tripleTriad\\src\\main\\resources\\cards.csv"))
                .withType(Card.class)
                .build()
                .parse();

        masterCardList.forEach(card -> masterCardMap.put(card.getId(), card));

        // Using Scanner for Getting Input from User
        dealHands();
        Scanner in = new Scanner(System.in);
        while (human.getCards().size() > 0) {
            System.out.print("Enter command: ");
            String a = in.nextLine();
            processInput(a);
        }

    }

    private void dealHands () {
        Random r = new Random();
        human = new Hand();
        computer = new Hand();
        board = new Board();
        Collections.shuffle(masterCardList);
        for (int i = 0; i < 5; i++) {
            human.cards.put(masterCardList.get(i).getId(), masterCardList.get(i));
        }
        Collections.shuffle(masterCardList);
        for (int i = 0; i < 5; i++) {
            computer.cards.put(masterCardList.get(i).getId(), masterCardList.get(i));
        }
        System.out.println(human.cards);
        System.out.println(computer.cards);
    }

    private void processInput (String input) {
        String[] split = input.split(",");
        if (split.length == 1) {
            String word = split[0];
            switch (word) {
                case "hand":
                    System.out.println("Player hand is: " + human.getCards());
                    break;
                case "handc":
                    System.out.println("Computer hand is: " + computer.getCards());
                    break;
                case "shuffle":
                    Collections.shuffle(masterCardList);
                    System.out.println(masterCardList);
                    break;
                case "exit":
                case "quit":
                    System.exit(0);
            }
            return;
        }
        int [] intInput = Arrays.stream(input.split(",")).mapToInt(Integer::parseInt).toArray();
        System.out.println(Arrays.toString(intInput));
        Position position = new Position(intInput[0], intInput[1]);

        Entry entry = addCardToBoard(human.cards.get(intInput[2]), position, HUMAN);
        ai_turn(entry, board);
    }

    public void ai_turn (Entry entry, Board inputBoard) {
        List<AiEntry> checkList = new ArrayList<>();
        buildEntryChecklist(entry, checkList);
        Card strongestPlay = null;
        Position positionToPlay = null;
        System.out.println(checkList);
        for (AiEntry e: checkList) {
            Position pos = e.getPosition();
            if (inputBoard.getBoard()[pos.row][pos.column] != null) {
                continue;
            }
            strongestPlay = findStrongestPlay(e, entry, computer);
            positionToPlay = pos;
            break;
        }
        if (strongestPlay != null) {
            addCardToBoard(strongestPlay, positionToPlay, COMPUTER);
        } else {
            //Pick random spot to play.
            search:
                for (int i = 0; i < inputBoard.getBoard().length; i ++) {
                    for (int j = 0; j < inputBoard.getBoard().length; j++) {
                        if (inputBoard.getBoard()[i][j] == null) {
                            //found empty spot
                            addCardToBoard(computer.getRandomCard(), new Position(i, j), COMPUTER);
                            break search;
                        }
                    }
                }
        }
    }

    private Entry addCardToBoard (Card card, Position position, Players owner) {
        Entry entry = Entry.builder().card(card).owner(owner).position(position).build();
        board.getBoard()[position.row][position.column] = entry;
        switch (owner) {
            case HUMAN :
                human.getCards().remove(card.getId());
                System.out.println("Human hand after removal: " + human.getCards());
                break;
            case COMPUTER :
                computer.getCards().remove(card.getId());
                System.out.println("Computer hand after removal: " + computer.getCards());
                break;
        }
        printBoard();
        return entry;
    }

    public static Card findStrongestPlay (AiEntry aiEntry, Entry humanEntry, Hand hand) {
        Card humanCard = humanEntry.getCard();
        Card highestDifference = findHighestDifference(humanCard, aiEntry.getPositionToHumanCard(), hand);
        System.out.printf("Strongest Difference is: %s and AI Entry is: %s%n%n", highestDifference, aiEntry);
        return highestDifference;
    }

    public static Card findHighestDifference (Card humanCard, PositionToOtherCard positionToOtherCard, Hand hand) {
        int difference = 0;
        Card cardToPlay = null;
        for (Card aiCard: hand.getCards().values()) {
            int diff = 0;
            switch (positionToOtherCard) {
                case ABOVE:
                    diff = aiCard.lower - humanCard.upper;
                    break;
                case RIGHT:
                    diff = aiCard.left - humanCard.right;
                    break;
                case BELOW:
                    diff = aiCard.upper - humanCard.lower;
                    break;
                case LEFT:
                    diff = aiCard.right - humanCard.left;
                    break;
            }
            if (diff > difference) {
                cardToPlay = aiCard;
                difference = diff;
                break;
            }
        }
        System.out.println("Card to play: " + cardToPlay);
        return cardToPlay;
    }

    private void printBoard () {
        Entry[][] entries = board.getBoard();
        for (int i = 0; i < entries.length; i++) {
            if (i == 0) {
                System.out.print("   ");
                for (int j = 0; j < entries[0].length; j++) {
                    System.out.printf("%2d ", j + 1);
                }
                System.out.println();
            }
            for (int j = 0; j < entries[i].length; j++) {
                if (j == 0) {
                    System.out.printf("%2d:", i + 1);
                }
                System.out.printf("%2s ", entries[i][j] == null ? "N" : entries[i][j].card.getName());
            }
            System.out.println();
        }
    }

    public static void buildEntryChecklist(Entry entry, List<AiEntry> checkList) {
        if (entry.position.row == 0) {
            if (entry.position.column == 0) {
                checkList.add(new AiEntry(0, 1, RIGHT));
                checkList.add(new AiEntry(1, 0, BELOW));
            }
            else if (entry.position.column == 1) {
                checkList.add(new AiEntry(0, 0, LEFT));
                checkList.add(new AiEntry(1,1, BELOW));
                checkList.add(new AiEntry(0, 2, RIGHT));
            } else {
                checkList.add(new AiEntry(0,1, LEFT));
                checkList.add(new AiEntry(1,2, BELOW));
            }
        } else if (entry.position.row ==1) {
            if (entry.position.column == 0) {
                checkList.add(new AiEntry(0,0, ABOVE));
                checkList.add(new AiEntry(1,1, RIGHT));
                checkList.add(new AiEntry(2, 0, BELOW));
            } else if (entry.position.column == 1) {
                checkList.add(new AiEntry(0, 1, ABOVE));
                checkList.add(new AiEntry(1, 0, LEFT));
                checkList.add(new AiEntry(1, 2, RIGHT));
                checkList.add(new AiEntry(2, 1, BELOW));
            } else {
                checkList.add(new AiEntry(0, 2, ABOVE));
                checkList.add(new AiEntry(1,1, LEFT));
                checkList.add(new AiEntry(2, 2, BELOW));
            }
        } else {
            if (entry.position.column == 0) {
                checkList.add(new AiEntry(1,0, ABOVE));
                checkList.add(new AiEntry(2,1, RIGHT));
            } else if (entry.position.column == 1) {
                checkList.add(new AiEntry(2,0, LEFT));
                checkList.add(new AiEntry(1,1, ABOVE));
                checkList.add(new AiEntry(2, 2, RIGHT));
            } else {
                checkList.add(new AiEntry(1,2, ABOVE));
                checkList.add(new AiEntry(2,1, LEFT));
            }
        }
    }
}
