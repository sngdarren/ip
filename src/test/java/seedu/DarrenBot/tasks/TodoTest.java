package seedu.DarrenBot.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoTest {
    @Test
    public void todoStringTest(){
        assertEquals("[T][ ] sleep now", new Todo("sleep now").toString());

        assertEquals("[T][ ] sleep later", new Todo("sleep later").toString());
    }
}
