import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class ClientActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    if (s.startsWith("find")) {
                        getContext()
                                .actorSelection("akka.tcp://server_system@127.0.0.1:3552/user/remote")
                                .tell(s, getSelf());
                    }
                })
                .match(Integer.class, i -> System.out.println("Price is: " + i))
                .match(Boolean.class, b -> {
                    if (!b) {
                        System.out.println("Book hasn't been found.");
                    }
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
