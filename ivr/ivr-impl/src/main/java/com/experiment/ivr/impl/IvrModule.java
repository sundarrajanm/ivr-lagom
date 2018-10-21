package com.experiment.ivr.impl;

import com.experiment.ivr.core.core.storage.AppStorage;
import com.experiment.ivr.core.core.storage.SessionStorage;
import com.experiment.ivr.dataprovider.storage.AppStorageImpl;
import com.experiment.ivr.dataprovider.storage.SessionStorageImpl;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

import com.experiment.ivr.api.IvrService;

/**
 * The module that binds the IvrService so that it can be served.
 */
public class IvrModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindService(IvrService.class, IvrServiceImpl.class);
        bind(AppStorage.class).to(AppStorageImpl.class).asEagerSingleton();
        bind(SessionStorage.class).to(SessionStorageImpl.class).asEagerSingleton();
    }
}
