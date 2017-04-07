import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RaceAverage {
	public static void main(String[] args) {
		//System.out.println(avgMinutes(new String[] {"02:00 PM, DAY 19","02:00 PM, DAY 20","01:58 PM, DAY 20"}));
		Scanner in = new Scanner(System.in);
		ArrayList<String> times = new ArrayList<String>();
		System.out.println("Get average minutes of finish times in the sailboat race!");
		System.out.println("This program will get the average of all the valid finish times you enter.");
		System.out.println();
		while (true) {
			System.out.print("Enter a time (or anything else to stop): ");
			String time = in.nextLine();
			try {
				/*
				 * We will try to get a duration. If the duration is invalid
				 * in any way, we will catch an exception, and break the loop.
				 */
				if (getDuration(time) <= 0) throw new IllegalArgumentException("Found an invalid time.");
			} catch (IllegalArgumentException ex) {
				System.out.println("You entered an invalid time.\nThe average minutes is:");
				break;
			}
			times.add(time);	
		}
		/*
		 * Now we display the results
		 */
		String[] input = new String[times.size()];
		for (int i = 0; i < times.size(); i++) {
			input[i] = times.get(i);
		}
		System.out.println(avgMinutes(input));
		in.close();
	}
	
	public static int avgMinutes(String[] times) {
		double total = 0.0;
		for (String time : times) {
			total += getDuration(time);
		}
		return (int)Math.round(total / times.length);
	}
	
	private static double getDuration(String input) {
		/*
		 * Let's use libraries provided by Java to get a time format we can work with.
		 * First I use regular expressions to grab the tokens I want.
		 */
		Matcher m = Pattern.compile("([0-9]+\\:[0-9]+\\s[AP]M)\\,\\sDAY\\s([0-9]+)").matcher(input);
		if (!m.find()) {
			throw new IllegalArgumentException("Invalid format for time: " + input);
		}
		Date start = null, offset = null;
		try {
			/*
			 * Here I simply make times out of them. Because the constraints ensure
			 * that there will never be a "day of the year" beyond 365, we can simply
			 * assume the SimpleDateFormat of hh = hour, mm = minute, a = AM/PM, and D = Day of the year
			 * 
			 * The regular expression matcher matches 2 groups:
			 * $1 = the time in SimpleDateFormat hh:mm a
			 * $2 = the day in integer form
			 */
			start = new SimpleDateFormat("hh:mm a D").parse("08:00 AM 1");
			offset = new SimpleDateFormat("hh:mm a D").parse(m.group(1) + " " + m.group(2));
		} catch (ParseException e) {
			throw new IllegalArgumentException("Time format invalid: " + input);
		}
		if (start == null || offset == null) return 0.0;
		return (offset.getTime() - start.getTime()) / 60000;
		
	}
}
