package com.github.tristest.spi;

import com.github.tristest.spi.annotation.ServiceProviderInterface;

@ServiceProviderInterface
public interface EventListener {
     void onEvent();
}
