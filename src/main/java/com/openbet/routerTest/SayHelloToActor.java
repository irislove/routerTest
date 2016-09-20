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
			
			System.out.println(getSelf() + " says: Hello" + " " + ((LinkedHashSet.Entry<?>) message).key());
			getSender().tell("Hello", getContext().parent());
		} 
		else if (message instanceof String) {
			System.out.println(getSelf() + " says: Hello" + " " + message);
			getSender().tell("Hello", getContext().parent());
		}
		else if (message instanceof Exception) {
			throw (Exception) message;
		}
		else {
			System.out.println(getSelf() + " says: Unreadable message.");
			getSender().tell("Invalid", getContext().parent());
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
		throw new IndexOutOfBoundsException();
	}
	
	@Override
	public void postStop() throws Exception {
		super.postStop();
		System.out.println("SayHelloToActor stopped: " + getSelf());
	}
}
