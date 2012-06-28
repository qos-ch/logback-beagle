package ch.qos.logback.beagle.tests;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class FirstTest {

    private static SWTWorkbenchBot bot;

    @BeforeClass
    public static void beforeClass() throws Exception {
        bot = new SWTWorkbenchBot();
        bot.viewByTitle("Welcome").close();
    }

    @Test
    public void dummy() throws Exception {
    }

    @AfterClass
    public static void sleep() {
        bot.sleep(2000);
    }

}