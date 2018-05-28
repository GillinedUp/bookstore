import akka.actor.AbstractActor;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

public class ServerActor extends AbstractActor {

    private static SupervisorStrategy strategy
            = new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
            matchAny(o -> restart()).
            build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }


    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private final String[] books = {
            "Harry Potter | 100",
            "Lord Of The Rings | 200",
            "1984 | 300",
    };

//    @Override
//    public void preStart() {
//        context().actorOf(Props.create(DBWorker.class), "DBWorker");
//    }

    private String getBookPrice(String bookTitle) {
        String price = "";
        for (String book : books) {
            if (book.startsWith(bookTitle)) {
                price = book.split("|")[1];
            }
        }
        return price;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    if (s.startsWith("find")) {
                        String query = s.split("\\$")[1];
                        String price = getBookPrice(query);
                        if (price.isEmpty()) {
                            getSender().tell(false, getSelf());
                        } else {
                            getSelf().tell(Integer.parseInt(price), getSelf());
                        }
                    }
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

}
