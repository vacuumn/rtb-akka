package com.lohika.rtb;

import akka.actor.ActorSystem;
import akka.routing.RoundRobinRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.lohika.rtb.actor.AskingActor;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.internal.inject.Injections;
import scala.concurrent.duration.Duration;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Application entry point.
 * Registers ActorSystem and other dependencies in CDI.
 */
public class RtbApplication extends Application {

    private ActorSystem system;

    @Inject
    public RtbApplication(ServiceLocator serviceLocator) {

        system = ActorSystem.create("RtbSystem");

        int biddersSize = readBiddersSize();

        system.actorOf(AskingActor.mkProps(biddersSize).withRouter(new RoundRobinRouter(5)), "askingRouter");

        DynamicConfiguration dc = Injections.getConfiguration(serviceLocator);
        Injections.addBinding(Injections.newBinder(system).to(ActorSystem.class), dc);
        dc.commit();

    }

    private int readBiddersSize() {
        //try from application.config
        int biddersSize = system.settings().config().getInt("rtb.bidders");

        //from system property, e.g. -Dbidders = 5
        String bidders = System.getProperty("bidders");

        if (bidders != null) {
            biddersSize = Integer.parseInt(bidders);
        }
        return biddersSize;
    }

    @PreDestroy
    private void shutdown() {
        system.shutdown();
        system.awaitTermination(Duration.create(15, TimeUnit.SECONDS));
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(RtbService.class);
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<>();

        // Add this (w/ corresponding POM changes) to get "pretty printed" JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        singletons.add(new JacksonJsonProvider(mapper));

        return singletons;
    }

}