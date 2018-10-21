package com.experiment.ivr.api;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import java.util.Optional;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;

public interface IvrService extends Service {

    ServiceCall<NotUsed, String> handleIVRCall(String appName,
                                               Optional<String> sessionId,
                                               Optional<String> userInput);

    @Override
    default Descriptor descriptor() {
        return named("ivr")
                .withCalls(
                        restCall(Method.POST, "/ivr/:appName?sessionId&userInput",
                                this::handleIVRCall)
                )
                .withAutoAcl(true);
    }
}
