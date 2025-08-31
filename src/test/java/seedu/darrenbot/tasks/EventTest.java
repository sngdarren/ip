package seedu.darrenbot.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventTest {
    @Test
    public void eventStringTest() {
        assertEquals("[E][ ] sleep now (from: today to: tomorrow)", new Event("sleep now", "today", "tomorrow").toString());

        assertEquals("[E][ ] sleep later (from: yesterday to: day before)", new Event("sleep later", "yesterday", "day before").toString());
    }
}
