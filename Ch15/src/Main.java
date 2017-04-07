/*
* Name: Kinjal Shah
* Project Name: Week 10: Exercise 15-3
* Date: 3/27/2017
* Description: Calculate the seconds it took to guess the right number
*/

import java.util.Scanner;

public class Main {

    public static void main(String args[]) {
        System.out.println("Welcome to the Number Guessing Game");
        System.out.println();

        Scanner sc = new Scanner(System.in);
        NumberGame game = new NumberGame();
        System.out.println("I have selected a number between 0 and " +
                game.getUpperLimit());
        System.out.println();
        
        game.setStartTime();
        System.out.print("Enter your guess: ");
        int guess = Integer.parseInt(sc.nextLine());
        while (guess != game.getNumber()) {
            if (guess < game.getNumber()) {
                System.out.println("Your guess is too low.\n");
            } else if (guess > game.getNumber()) {
                System.out.println("Your guess is too high.\n");
            }
            game.incrementGuessCount();
            System.out.print("Enter your guess: ");
            guess = Integer.parseInt(sc.nextLine());
        }        
        game.setEndTime();
        System.out.println("Correct!\n");
        
        System.out.println("You guessed the correct number in " +
                game.getGuessCount() + " guesses.\n");
        System.out.println("You guessed the correct number in " +
                game.getElapsedTime() + " seconds.\n"); // display the the output time difference.
        System.out.println("Bye!");
    }
}