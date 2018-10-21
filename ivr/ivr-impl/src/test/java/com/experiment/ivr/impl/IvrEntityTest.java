package com.experiment.ivr.impl;

import akka.Done;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver.Outcome;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;

import com.experiment.ivr.impl.IvrCallCommand.NewCall;

import static org.junit.Assert.assertEquals;

public class IvrEntityTest {
    private static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("IvrEntityTest");
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testIvrEntity() {
        PersistentEntityTestDriver<IvrCallCommand, IvrCallEvent, IvrCallState> driver = new PersistentEntityTestDriver<>(system,
                new IvrCallEntity(), "world-1");

        Outcome<IvrCallEvent, IvrCallState> outcome1 = driver.run(new NewCall("Alice"));
        assertEquals("NewCall, Alice!", outcome1.getReplies().get(0));
        assertEquals(Collections.emptyList(), outcome1.issues());

        Outcome<IvrCallEvent, IvrCallState> outcome2 = driver.run(new IvrCallCommand.ContinueCall("Hi"),
                new IvrCallCommand.NewCall("Bob"));
        assertEquals(1, outcome2.events().size());
        assertEquals(new IvrCallEvent.NewCallStarted("world-1", "Hi"), outcome2.events().get(0));
        assertEquals("Hi", outcome2.state().sessionId);
        assertEquals(Done.getInstance(), outcome2.getReplies().get(0));
        assertEquals("Hi, Bob!", outcome2.getReplies().get(1));
        assertEquals(2, outcome2.getReplies().size());
        assertEquals(Collections.emptyList(), outcome2.issues());
    }
}
