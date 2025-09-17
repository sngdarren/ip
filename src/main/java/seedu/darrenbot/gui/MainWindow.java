package seedu.darrenbot.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Main GUI controller for the DarrenBot application.
 *
 * <p>This class manages the primary user interface of the chatbot. It handles user input,
 * displays the conversation as dialog boxes, and interacts with the backend {@link DarrenBot}
 * instance to generate responses. The layout is defined via an FXML file, with injected fields
 * representing UI components such as the scroll pane, text input, and dialog container.</p>
 *
 * <p>The controller also initializes automatic scrolling of the dialog container
 * and binds event handlers to capture and process user interactions.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Binds the scroll pane to follow new messages dynamically.</li>
 *   <li>Injects and manages a {@link DarrenBot} instance to produce responses.</li>
 *   <li>Appends user and bot dialog boxes to the chat container.</li>
 *   <li>Clears user input after submission.</li>
 * </ul>
 *
 * <h2>FXML Elements:</h2>
 * <ul>
 *   <li>{@code scrollPane} – Scrollable container for the dialog list.</li>
 *   <li>{@code dialogContainer} – Holds dialog boxes for the conversation.</li>
 *   <li>{@code userInput} – Text field for entering user commands/messages.</li>
 *   <li>{@code sendButton} – Button to trigger message submission.</li>
 * </ul>
 *
 * <p>Associated images (user and bot avatars) are loaded from the {@code /images} resource folder.</p>
 */

public class MainWindow extends AnchorPane {
    /**
     * Controller for the main GUI.
     */
    public static final String userImagePath = "/images/gloria.jpg";
    public static final String dukeImagePath = "/images/motomoto.jpg";
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private DarrenBot darrenBot;

    private final Image userImage = new Image(this.getClass().getResourceAsStream(userImagePath));
    private final Image dukeImage = new Image(this.getClass().getResourceAsStream(dukeImagePath));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Duke instance */
    public void setDuke(DarrenBot d) {
        darrenBot = d;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = darrenBot.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDukeDialog(response, dukeImage)
        );
        userInput.clear();
    }
}
