package com.lohika.rtb.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Mapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.google.common.collect.Lists;
import com.lohika.rtb.dto.AdAsk;
import com.lohika.rtb.dto.Bid;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static akka.dispatch.Futures.sequence;
import static akka.pattern.Patterns.pipe;

/**
 * Main actor which asks all bidders and process all results.
 *
 * @author spichkurov
 */
public class AskingActor extends UntypedActor {
    private final List<ActorRef> bidders = new ArrayList<>();
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final int biddersSize;
    private final Timeout timeout = new Timeout(Duration.create(5, "seconds"));

    public AskingActor(final int biddersSize) {
        //allocate bidder actors
        for (int i = 0; i < biddersSize; i++) {
            String adUrl = "http://example.org/ad/someAd" + i;
            log.debug("creating bidder actor: ad-url:{} ", adUrl);
            bidders.add(getContext().actorOf(
                    Props.create(BiddingActor.class, adUrl)));
        }
        this.biddersSize = biddersSize;
    }

    public static Props mkProps(int biddersSize) {
        return Props.create(AskingActor.class, biddersSize);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof AdAsk) {
            log.debug("received AdAsk message: {}", message);
            List<Future<Bid>> listOfFutureInts = Lists.newArrayList();
            for (int i = 0; i < biddersSize; i++) {
                Future<Bid> bidderFuture = Patterns.ask(bidders.get(i), message, timeout).map(new Mapper<Object, Bid>() {
                    public Bid apply(Object s) {
                        return (Bid) s; //TODO: remove ugly mapper caster
                    }
                }, getContext().dispatcher());
                listOfFutureInts.add(bidderFuture);
            }

            Future<Iterable<Bid>> futureListOfInts = sequence(listOfFutureInts, getContext().dispatcher());
            Future<Bid> futureSum = futureListOfInts.map(
                    new Mapper<Iterable<Bid>, Bid>() {
                        public Bid apply(Iterable<Bid> bids) {
                            //compute max bid and return it
                            Bid max = Collections.max((Collection<Bid>) bids);
                            return max;
                        }
                    }, getContext().dispatcher());

            //result return to the caller(RtbService in this case)
            pipe(futureSum, getContext().dispatcher()).to(getSender());

        } else {
            unhandled(message);
        }

    }
}
