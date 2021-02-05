package com.github.tristest.spi.util;

import com.github.tristest.spi.annotation.ServiceProviderInterface;
import com.github.tristest.spi.exception.SpiException;

import java.util.Objects;

public final class ServiceProviderUtil {
    private ServiceProviderUtil() {
    }

    public static void validateServiceProviderInterface(Class<?> interfaceClass) {
        Objects.requireNonNull(interfaceClass);
        if (!interfaceClass.isInterface()) {
            throw new SpiException("Spi class is not interface, " + interfaceClass);
        }
        if (!interfaceClass.isAnnotationPresent(ServiceProviderInterface.class)) {
            throw new SpiException("Spi class is must be annotated with @ServiceProviderInterface, " + interfaceClass);
        }
    }
}
