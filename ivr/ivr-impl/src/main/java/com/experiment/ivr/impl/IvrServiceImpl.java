package com.experiment.ivr.impl;

import akka.NotUsed;
import akka.japi.Pair;
import com.experiment.ivr.api.IvrService;
import com.experiment.ivr.core.core.exception.ApplicationNotFoundException;
import com.experiment.ivr.core.core.exception.SessionNotFoundException;
import com.experiment.ivr.core.core.storage.AppStorage;
import com.experiment.ivr.core.core.storage.SessionStorage;
import com.experiment.ivr.usecase.ContinueCall;
import com.experiment.ivr.usecase.StartCall;
import com.experiment.ivr.usecase.impl.ContinueCallImpl;
import com.experiment.ivr.usecase.impl.StartCallImpl;
import com.experiment.ivr.usecase.model.Request;
import com.experiment.ivr.usecase.model.Response;
import com.google.common.net.HttpHeaders;
import com.lightbend.lagom.javadsl.api.transport.MessageProtocol;
import com.lightbend.lagom.javadsl.api.transport.ResponseHeader;
import com.lightbend.lagom.javadsl.server.HeaderServiceCall;
import lombok.extern.flogger.Flogger;
import org.pcollections.HashTreePMap;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Flogger
public class IvrServiceImpl implements IvrService {

    private final AppStorage appStorage;

    private final SessionStorage sessionStorage;

    private StartCall newCall = new StartCallImpl();

    private ContinueCall existingCall = new ContinueCallImpl();

    @Inject
    public IvrServiceImpl(AppStorage appStorage, SessionStorage sessionStorage) {

        this.appStorage = appStorage;
        this.sessionStorage = sessionStorage;
    }

    @Override
    public HeaderServiceCall<NotUsed, String> handleIVRCall(
            String appName, Optional<String> sessionId, Optional<String> userInput) {
        return (requestHeader, notUsed) -> sessionId

                .map(id -> this.handleExistingCall(appName, id, userInput.orElse("")))
                .orElseGet(() -> this.handleNewCall(appName))

                .thenApply(response -> {
                    log.atInfo().log("Sending response: %s", response);

                    ResponseHeader resHeaders = ResponseHeader.OK
                            .withProtocol(new MessageProtocol().withContentType("application/xml"))
                            .withHeader(HttpHeaders.LOCATION, response.getSessionId());
                    return Pair.create(resHeaders, response.getDocument());
                })

                .exceptionally(error -> {
                    Throwable cause = error.getCause();
                    log.atWarning().log("Received: %s", cause);
                    if(cause instanceof SessionNotFoundException || cause instanceof ApplicationNotFoundException) {
                        ResponseHeader resHeaders = new ResponseHeader(404,
                                new MessageProtocol(), HashTreePMap.empty());
                        return Pair.create(resHeaders, cause.toString());
                    }
                    ResponseHeader resHeaders = new ResponseHeader(500,
                            new MessageProtocol(), HashTreePMap.empty());
                    return Pair.create(resHeaders, cause.toString());
                });
    }

    private CompletableFuture<Response> handleNewCall(String appName) {
        Request useCaseReq = Request.builder()
                .app(appName)
                .build();

        return newCall.handle(useCaseReq);
    }

    private CompletableFuture<Response> handleExistingCall(String appName, String id, String userInput) {
        Request useCaseReq = Request.builder()
                .app(appName)
                .sessionId(id)
                .userInput(userInput)
                .build();

        return existingCall.handle(useCaseReq);
    }

}
