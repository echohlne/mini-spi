package com.github.tristest.spi.core;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class ServiceProviderManager {
    private ServiceProviderManager(){
    }

    private static final Map<Class,ServiceProviderLoader> LOADERS = new ConcurrentHashMap<>();

    public static <T>ServiceProviderLoader<T> load(Class<T> clazz) {
        Objects.requireNonNull(clazz);

        ServiceProviderLoader serviceProviderLoader = LOADERS.get(clazz);
        if(serviceProviderLoader != null) {
            return serviceProviderLoader;
        }
        synchronized (LOADERS) {
            serviceProviderLoader = LOADERS.get(clazz);
            if(serviceProviderLoader == null) {
                serviceProviderLoader = new ServiceProviderLoader(clazz);
            }
        }

        return serviceProviderLoader;
    }
}
