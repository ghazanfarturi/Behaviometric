package com.dell.research.continuousauthentication.demotransmitter;

public interface DemoTransmittable {
    String getVisualizationJSON();

    public static enum MessageType {
        GESTURE;

        private MessageType() {
        }
    }
}
