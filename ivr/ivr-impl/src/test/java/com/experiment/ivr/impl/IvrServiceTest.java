package com.experiment.ivr.impl;

import org.junit.Test;

import com.experiment.ivr.api.IvrService;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.withServer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class IvrServiceTest {
    @Test
    public void shouldStorePersonalizedGreeting() {
        withServer(defaultSetup().withCassandra(), server -> {
            IvrService service = server.client(IvrService.class);

            String msg1 = service.newCall("Alice").invoke().toCompletableFuture().get(5, SECONDS);
            assertEquals("NewCall, Alice!", msg1); // default greeting

            service.handleIVRCall("Alice").invoke(new GreetingMessage("Hi")).toCompletableFuture().get(5, SECONDS);
            String msg2 = service.newCall("Alice").invoke().toCompletableFuture().get(5, SECONDS);
            assertEquals("Hi, Alice!", msg2);

            String msg3 = service.newCall("Bob").invoke().toCompletableFuture().get(5, SECONDS);
            assertEquals("NewCall, Bob!", msg3); // default greeting
        });
    }
}
