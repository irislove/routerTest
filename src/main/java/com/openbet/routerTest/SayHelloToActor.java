package com.openbet.routerTest;

import akka.actor.UntypedActor;
import scala.Option;
import scala.collection.mutable.LinkedHashSet;

public class SayHelloToActor extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof LinkedHashSet.Entry<?>) {
			if (((LinkedHashSet.Entry<?>) message).key() instanceof Exception) {
				throw (Exception) ((LinkedHashSet.Entry<?>) message).key();
			}
			
			getSender().tell("Hello", getSelf());
			System.out.println("Hello" + " " + ((LinkedHashSet.Entry<?>) message).key());
		} else {
			getSender().tell("Invalid", getSelf());
			System.out.println("Unreadable message.");
		}
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();
		System.out.println("SayHelloToActor started: " + getSelf());
	}
	
	@Override
	public void preRestart(Throwable reason, Option<Object> object) throws Exception {
		super.preRestart(reason, object);
		System.out.println("SayHelloToActor restarted: " + getSelf() +
				" Reason: " +  reason);
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		throw new NullPointerException();
	}
	
	@Override
	public void postStop() throws Exception {
		super.postStop();
		System.out.println("SayHelloToActor stopped: " + getSelf());
	}
}
