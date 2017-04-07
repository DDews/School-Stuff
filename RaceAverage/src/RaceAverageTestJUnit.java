

import org.junit.Test;
import static org.junit.Assert.*;

public class RaceAverageTestJUnit {
	@Test
	public void ExampleOne() {
		assertEquals(RaceAverage.avgMinutes(new String[] {"12:00 PM, DAY 1",
 "12:01 PM, DAY 1"}),241);
	}
	@Test
	public void ExampleTwo() {
		assertEquals(RaceAverage.avgMinutes(new String[] {"12:00 AM, DAY 2"}),960);
	}
	@Test
	public void ExampleThree() {
		assertEquals(RaceAverage.avgMinutes(new String[] {"02:00 PM, DAY 19",
				 "02:00 PM, DAY 20",
				 "01:58 PM, DAY 20"}),27239);
	}
}
