package com.github.tristest.spi;

public class ClickEventListener implements EventListener{
    @Override
    public void onEvent() {
        System.out.println("click event occurred.");
    }
}
