package cocktailMaker.server.card;

import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class CardSwipeDispatcher {

    private static final Logger logger = Logger.getLogger(CardSwipeDispatcher.class);
    private static final String CARD_PATTERN_PROPERTY = "card.pattern";
    private static final String CARD_LENGTH_PROPERTY = "card.length";
    private static final String CARD_BEGINS_PROPERTY = "card.begins";

    private static CardSwipeDispatcher instance;

    private static Stage stage;
    private static String cardPattern;
    private static Integer cardLength;
    private static String cardBegins;

    private static CardSwipeListener cardSwipeListener;
    private static Set<SwipeEventListener> listeners;

    private CardSwipeDispatcher() {
    }

    public static CardSwipeDispatcher getInstance() {
        if (instance == null) {
            instance = new CardSwipeDispatcher();
        }
        return instance;
    }

    public void init(Stage stage, Properties properties) {
        this.stage = stage;
        cardPattern = properties.getProperty(CARD_PATTERN_PROPERTY);
        cardLength = Integer.valueOf(properties.getProperty(CARD_LENGTH_PROPERTY));
        cardBegins = properties.getProperty(CARD_BEGINS_PROPERTY);

        listeners = new HashSet<>();

        if (cardLength == null || cardPattern == null || cardBegins == null) {
            logger.error("Card Manager not initialized due to missing config properties");
        } else {
            logger.info("Cart manager initialized");
            logger.info("Card pattern: " + cardPattern);
            logger.info("Card length: " + cardLength);
            logger.info("Card pattern begins with : " + cardBegins);
        }

        cardSwipeListener = new CardSwipeListener(stage, cardPattern, cardLength, cardBegins, listeners);

        cardSwipeListener.run();

    }

    public void subscribe(SwipeEventListener listener) {
        listeners.add(listener);
    }

    public void unsubscribe(SwipeEventListener listener) {
        listeners.remove(listener);
    }

}
