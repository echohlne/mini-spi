package com.github.tristest.spi;

import com.github.tristest.spi.core.ServiceProviderLoader;
import com.github.tristest.spi.core.ServiceProviderManager;

public class SPITest {
    public static void main(String[] args) {
        ServiceProviderLoader<EventListener> serviceProviderLoaders = ServiceProviderManager.load(EventListener.class);
        EventListener click = serviceProviderLoaders.getServiceProvider("click");
        click.onEvent();
    }
}
