package com.lohika.rtb.actor;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.lohika.rtb.dto.AdAsk;
import com.lohika.rtb.dto.Bid;

import java.util.Random;

/**
 * Actor for pretending as bidder. Simple returns random bid on any ad ask.
 *
 * @author spichkurov
 */
public class BiddingActor extends UntypedActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final String adUrl;
    private static final Random random = new Random();

    public BiddingActor(final String adUrl) {
        this.adUrl = adUrl;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof AdAsk) {
            log.debug("received AdAsk message in BiddingActor: {}", message);
            Bid bid = new Bid(random.nextInt(100), adUrl);
            log.debug("bidder response: {}", bid);
            getSender().tell(bid, getSelf());
        } else {
            unhandled(message);
        }
    }
}
