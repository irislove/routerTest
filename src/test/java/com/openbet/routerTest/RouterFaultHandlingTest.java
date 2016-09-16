package com.openbet.routerTest;

import static akka.pattern.Patterns.ask;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.ConsistentHashingPool;
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope;
import akka.testkit.JavaTestKit;
import scala.collection.mutable.LinkedHashSet.Entry;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

public class RouterFaultHandlingTest {

	static ActorSystem system;
	Duration timeout = Duration.create(5, TimeUnit.SECONDS);
	
	@BeforeClass
	public static void start() {
		system = ActorSystem.create("RouterTest");
	}
	
	@AfterClass
	public static void cleanup() {
		JavaTestKit.shutdownActorSystem(system);
		system = null;
	}
	
	@Test
	public void testSupervisorTrategy() throws Exception {
        ActorRef router = system.actorOf(new ConsistentHashingPool(2).props(Props.create(SayHelloToActor.class)));
        
        assert Await.result(ask(router, new ConsistentHashableEnvelope(new Entry<String>("Pikachu"), "message"), 5000), timeout).equals("Hello");
        assert Await.result(ask(router, new ConsistentHashableEnvelope(new Entry<Exception>(new NullPointerException()), "message"), 5000), timeout).equals("Invalid");
	}
}