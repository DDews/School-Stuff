/*
* Name: Kinjal Shah
* Project Name: Week 10: Exercise 15-3
* Date: 3/27/2017
* Description: Calculate the seconds it took to guess the right number
*/


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

public class NumberGame {
    private int upperLimit;
    private int number;
    private int guessCount;
    
   private LocalDateTime startTime;
   private LocalDateTime endTime;
    public NumberGame() {
        this(50);        
    }
    
    public NumberGame(int upperLimit) {
        this.upperLimit = upperLimit;
        Random random = new Random();
        number = random.nextInt(upperLimit + 1) ;
        guessCount = 1;
    }

    public int getNumber() {
        return number;
    }

    public int getGuessCount() {
        return guessCount;
    }
    
    public int getUpperLimit() {
        return upperLimit;
    }
    
    public void incrementGuessCount() {
        guessCount = guessCount + 1;
    }
     public void setStartTime(){
       startTime = LocalDateTime.now(); // set the start time
   }
   public LocalDateTime getStartTime(){
       return startTime; // get the start time
   }
   public void setEndTime(){
       endTime = LocalDateTime.now(); // Set the end time
   }
   public LocalDateTime getEndTime(){
       return endTime; // get the end time
   }
  
   public long getElapsedTime(){
		long startSeconds = startTime.toInstant(ZoneOffset.UTC)
				.getEpochSecond(); // get time in milliseconds
		long endSeconds = endTime.toInstant(ZoneOffset.UTC).getEpochSecond(); // get time in milliseconds
		return endSeconds - startSeconds; // substract startseconds from end seconds.
   }
}