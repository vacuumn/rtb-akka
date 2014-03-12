package com.lohika.rtb;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.lohika.rtb.dto.AdAsk;
import org.glassfish.jersey.server.ManagedAsync;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

/**
 * Service(or controller) which handles incoming ask requests.
 */
@Path("/place-bid/{value}")
public class RtbService {

    @Context
    private ActorSystem actorSystem;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void getBids(
            @PathParam("value") String value,
            @Suspended final AsyncResponse res) {

        // For Akka 2.2, should use actorSelection,
        // but how to do this outside of an actor?
        ActorRef askingActor = actorSystem.actorFor("/user/askingRouter");

        Timeout timeout = new Timeout(Duration.create(10, "seconds"));

        Future<Object> future = Patterns.ask(askingActor, new AdAsk(value), timeout);

        future.onComplete(new OnComplete<Object>() {

            public void onComplete(Throwable failure, Object result) {

                if (failure != null) {

                    if (failure.getMessage() != null) {
                        HashMap<String, String> response = new HashMap<String, String>();
                        response.put("error", failure.getMessage());
                        res.resume(Response.serverError().entity(response).build());
                    } else {
                        res.resume(Response.serverError());
                    }

                } else {
                    HashMap<String, Object> response = new HashMap<String, Object>();
                    response.put("results", result);
                    res.resume(Response.ok().entity(response).build());
                }

            }
        }, actorSystem.dispatcher());

    }



}
