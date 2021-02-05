package com.github.tristest.spi;

public class CloseEventListener implements EventListener{
    @Override
    public void onEvent() {
        System.out.println("Close event occurred.");
    }
}
