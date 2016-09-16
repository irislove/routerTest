package com.openbet.routerTest;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.ConsistentHashingPool;
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope;
import scala.collection.mutable.LinkedHashSet.Entry;

/**
 * Hello world!
 *
 */
@SuppressWarnings("deprecation")
public class App 
{
    public static void main( String[] args )
    {
        ActorSystem actorSystem = ActorSystem.create("RouterTest");
        ActorRef router = actorSystem.actorOf(new ConsistentHashingPool(2).props(Props.create(SayHelloToActor.class)));
        router.tell(new ConsistentHashableEnvelope(new Entry<String>("Pikachu"), "message"), ActorRef.noSender());
    }
}
