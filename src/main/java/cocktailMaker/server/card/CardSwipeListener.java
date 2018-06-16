package cocktailMaker.server.card;

import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CardSwipeListener implements Runnable {

    private static final Logger logger = Logger.getLogger(CardSwipeListener.class);
    public static final int QUARTER_A_SECOND_IN_NANOS = 250000000;

    private Stage stage;
    private String cardPattern;
    private int cardLength;
    private String cardBegins;
    private Collection<SwipeEventListener> listeners;

    private StringBuilder cardBuilder;
    private Instant readingStart;
    private boolean reading;

    private int readSoFar = 0;
    private boolean firingQueued = false;

    private List<KeyEvent> eventQueue = new LinkedList<>();

    public CardSwipeListener(Stage stage, String cardPatter, int cardLength, String cardBegins, Collection<SwipeEventListener> listeners) {
        this.stage = stage;
        this.cardPattern = cardPatter;
        this.cardLength = cardLength;
        this.cardBegins = cardBegins;
        this.listeners = listeners;
    }

    @Override
    public void run() {
        stage.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!firingQueued) {
                if (event.getCharacter().equalsIgnoreCase(cardBegins)) {
                    beginReading();
                }
                if (reading) {
                    cardBuilder.append(event.getCharacter());
                    readSoFar++;
                    eventQueue.add(event.copyFor(event.getSource(), event.getTarget(), event.getEventType()));

                    if (Duration.between(readingStart, Instant.now()).toNanos() > QUARTER_A_SECOND_IN_NANOS) {
                        stopReading();
                        fireQueuedEvents();
                    }

                    if (readSoFar >= cardLength) {
                        cardDetected();
                        stopReading();
                    }
                    event.consume();
                }
            }
        });
    }

    private void cardDetected() {
        String card = cardBuilder.toString();
        card = card.replaceAll("[\\s]", "");

        if (card.matches(cardPattern)) {
            logger.info("Card swipe detected");
            for (SwipeEventListener listener : listeners) {
                    listener.cardSwiped(card);
            }
        }
    }

    private void fireQueuedEvents() {
        firingQueued = true;
        for (KeyEvent event : eventQueue) {
            ((Node) event.getTarget()).fireEvent(event);
        }
        firingQueued = false;
    }

    private void stopReading() {
        reading = false;
        readingStart = null;
        readSoFar = 0;
    }

    private void beginReading() {
        cardBuilder = new StringBuilder();
        reading = true;
        readingStart = Instant.now();
        readSoFar = 0;
        eventQueue.clear();
    }

}
