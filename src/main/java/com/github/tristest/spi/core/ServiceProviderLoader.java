package com.github.tristest.spi.core;

import com.github.tristest.spi.exception.SpiException;
import com.github.tristest.spi.util.ServiceProviderUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceProviderLoader<T> {
    private static final String ALIAS_SPLITTER = "=";
    private static final String RESOURCE_DIR = "META-INF.services/";
    private final Class<T> primaryInterfaceClass;
    private final ClassLoader currentClassLoader;

    private final Map<String, T> cachedServiceInstances = new ConcurrentHashMap<>();
    private final Map<String, String> cachedClassAlias = new ConcurrentHashMap<>();

    public ServiceProviderLoader(Class<T> primaryInterfaceClass) {
        this(primaryInterfaceClass, Thread.currentThread().getContextClassLoader());
    }

    public ServiceProviderLoader(Class<T> primaryInterfaceClass, ClassLoader classLoader) {
        ServiceProviderUtil.validateServiceProviderInterface(primaryInterfaceClass);

        this.primaryInterfaceClass = primaryInterfaceClass;
        this.currentClassLoader = classLoader;

        this.loadServiceConfig();
    }

    public T getServiceProvider(String alias) {
        Objects.requireNonNull(alias);
        T serviceInstance = cachedServiceInstances.get(alias);
        if (serviceInstance != null) {
            return serviceInstance;
        }

        synchronized (cachedServiceInstances) {
            serviceInstance = cachedServiceInstances.get(alias);
            if (serviceInstance == null) {
                serviceInstance = createServiceInstance(alias);
                cachedServiceInstances.put(alias, serviceInstance);
            }
        }
        return serviceInstance;
    }

    private T createServiceInstance(String alias) {
        String aliasClassName = cachedClassAlias.get(alias);
        if (aliasClassName == null || aliasClassName.trim().isEmpty()) {
            throw new SpiException(String.format("SPI config file for class %s not found.", this.primaryInterfaceClass.getName()));
        }
        try {
            Class clazz = Class.forName(aliasClassName);
            return (T) clazz.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new SpiException(e);
        }
    }

    private void loadServiceConfig() {
        String absolutePath = RESOURCE_DIR + this.primaryInterfaceClass.getName();

        try {
            Enumeration<URL> resources = this.currentClassLoader.getResources(absolutePath);

            if (resources.hasMoreElements()) {
                List<String> resourcesList = parseResourceFile(resources.nextElement());

                if (resourcesList.isEmpty()) {
                    throw new SpiException(String.format("SPI config file for class %s is empty.", this.primaryInterfaceClass.getName()));
                }

                resourcesList.forEach(resource -> {
                    String[] details = resource.split(ALIAS_SPLITTER);
                    cachedClassAlias.put(details[0], details[1]);
                });
            } else {
                throw new SpiException(String.format("SPI config file for class %s not found.", this.primaryInterfaceClass.getName()));
            }

        } catch (IOException e) {
            throw new SpiException(e);
        }
    }


    private List<String> parseResourceFile(URL resourceUrl) {
        Objects.requireNonNull(resourceUrl);
        List<String> resourcesList = new ArrayList<>();

        try (InputStream is = resourceUrl.openStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String resourceInLine;
            while ((resourceInLine = br.readLine()) != null) {
                resourcesList.add(resourceInLine);
            }
        } catch (IOException e) {
            throw new SpiException(e);
        }
        return resourcesList;
    }

}
