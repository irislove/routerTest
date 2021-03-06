package com.openbet.routerTest;

import static akka.pattern.Patterns.ask;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.OneForOneStrategy;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.contrib.pattern.ClusterSingletonManager;
import akka.contrib.pattern.ClusterSingletonProxy;
import akka.routing.FromConfig;
import akka.routing.RoundRobinPool;
import akka.testkit.JavaTestKit;
import scala.collection.script.End;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

public class RouterFaultHandlingTest {

	static ActorSystem system;
	Duration timeout = Duration.create(10, TimeUnit.SECONDS);
	
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
	public void testSupervisorStrategy() throws Exception {
		final SupervisorStrategy strategy =
				  new OneForOneStrategy(1, Duration.create(10, TimeUnit.MINUTES),
				    Collections.<Class<? extends Throwable>>singletonList(Exception.class));
        ActorRef router = system.actorOf(new RoundRobinPool(3).withSupervisorStrategy(strategy).props(Props.create(SayHelloToActor.class)), "router");
//		ActorRef router = system.actorOf(new RoundRobinPool(3).props(Props.create(SayHelloToActor.class)), "router");
		
        assert Await.result(ask(router, "Pikachu", 5000), timeout).equals("Hello");
        assert Await.result(ask(router, "Charmander", 5000), timeout).equals("Hello");
        ask(router, new NullPointerException(), 5000);
        Thread.sleep(5000);
        assert Await.result(ask(router, "Jigglypuff", 5000), timeout).equals("Hello");
        assert Await.result(ask(router, "Squirtle", 5000), timeout).equals("Hello");
        
//        ActorRef router = system.actorOf(new ConsistentHashingPool(3).withSupervisorStrategy(strategy).props(Props.create(SayHelloToActor.class)), "router");
//        
//        assert Await.result(ask(router, new ConsistentHashableEnvelope(new Entry<String>("Pikachu"), "message"), 5000), timeout).equals("Hello");
//        assert Await.result(ask(router, new ConsistentHashableEnvelope(new Entry<String>("Charmander"), "message"), 5000), timeout).equals("Hello");
//        ask(router, new ConsistentHashableEnvelope(new Entry<Exception>(new NullPointerException()), "message"), 5000);
//        assert Await.result(ask(router, new ConsistentHashableEnvelope(new Entry<String>("Jigglypuff"), "message"), 5000), timeout).equals("Hello");
	}
	
	@Test
	public void testSupervisorStrategyClusterSingleton() throws Exception {
		final SupervisorStrategy strategy =
        		new OneForOneStrategy(5, Duration.create(1, TimeUnit.MINUTES),
        				Collections.<Class<? extends Throwable>>singletonList(Exception.class));
		
		system.actorOf(ClusterSingletonManager
			.defaultProps(
				FromConfig.getInstance()
				.withSupervisorStrategy(strategy)
				.props(Props.create(SayHelloToActor.class)),
				"sayHello",
				PoisonPill.getInstance(),
				null
			), "singleton"
		);
		
		ActorRef router = system.actorOf(ClusterSingletonProxy.defaultProps("user/singleton/sayHello", null), "sayHelloProxy");
        
		assert Await.result(ask(router, "Pikachu", 5000), timeout).equals("Hello");
        assert Await.result(ask(router, "Charmander", 5000), timeout).equals("Hello");
        ask(router, new NullPointerException(), 5000);
        Thread.sleep(5000);
        assert Await.result(ask(router, "Jigglypuff", 5000), timeout).equals("Hello");
        assert Await.result(ask(router, "Squirtle", 5000), timeout).equals("Hello");
	}
}