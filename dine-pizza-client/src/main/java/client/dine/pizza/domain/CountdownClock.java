package client.dine.pizza.domain;

import org.springframework.util.StopWatch;

public class CountdownClock {

    public CountdownClock(int start, String prefix) {
        StopWatch clock = new StopWatch("CrunchifyThreads");
        clock.start("CrunchifyThread-2");
        System.out.printf("\n\n");
        for (int i = 1; i <= start; i++) {
			try {
                Thread.sleep(1000);
                System.out.print(String.format("\033[%dA",1)); // Move up
                System.out.print("\033[2K"); // Erase line content
				System.out.printf(prefix + "Order Baking Timer: " + i + "/" + start, "*");

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
        clock.stop();
    }
}
