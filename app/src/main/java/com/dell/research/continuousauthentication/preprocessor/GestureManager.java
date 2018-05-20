package com.dell.research.continuousauthentication.preprocessor;

import com.dell.research.continuousauthentication.nativedevice.NativeDeviceData;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GestureManager {
    private static final int TERMINATING_TRACKING_ID = -1;
    private final String TAG = this.getClass().getName();
    private Integer currentSlot = 0;
    private Map<Integer, ConstructedGesture> slotTimelineMapping = null;
    private int displaySizeX = -1;
    private int displaySizeY = -1;

    public GestureManager(int displaySizeX, int displaySizeY) {
        this.slotTimelineMapping = new TreeMap();
        this.displaySizeX = displaySizeX;
        this.displaySizeY = displaySizeY;
    }

    public List<ConstructedGesture> process(List<NativeDeviceData> dataBuffer, int displayedOrientation, String appName) {
        double time = -1.0D;
        int positionX = -1;
        int positionY = -1;
        int touchMajor = -1;
        boolean clearData = false;
        boolean emptySynReport = true;
        double tabletTime = time;
        List<ConstructedGesture> finishedGestures = new LinkedList();
        ConstructedGesture finished = null;
        Iterator i$ = dataBuffer.iterator();

        while(i$.hasNext()) {
            NativeDeviceData d = (NativeDeviceData)i$.next();
            if (d.isValid()) {
                switch(d.getType()) {
                    case EV_ABS:
                        time = (double)d.getTime() + (double)d.getTimeMicro() * 1.0E-6D;
                        switch(d.getAbsCode()) {
                            case ABS_MT_TOOL_X:
                            case ABS_MT_TOOL_Y:
                            case ABS_MT_WIDTH_MAJOR:
                            case ABS_MT_WIDTH_MINOR:
                            default:
                                break;
                            case ABS_MT_POSITION_X:
                                positionX = d.getValue();
                                break;
                            case ABS_MT_POSITION_Y:
                                positionY = d.getValue();
                                break;
                            case ABS_MT_TOUCH_MAJOR:
                                touchMajor = d.getValue();
                                break;
                            case ABS_MT_TOUCH_MINOR:
                                if (touchMajor < 0) {
                                    touchMajor = d.getValue();
                                }
                                break;
                            case ABS_MT_SLOT:
                                this.writeToActiveTimeline(time, positionX, positionY, touchMajor, displayedOrientation, appName);
                                this.currentSlot = d.getValue();
                                clearData = true;
                                break;
                            case ABS_MT_TRACKING_ID:
                                if (d.getValue() == -1) {
                                    finished = this.finishCurrentSlot(time);
                                    if (finished != null && finished.hasPoints()) {
                                        finishedGestures.add(finished);
                                    }

                                    clearData = true;
                                }
                        }

                        emptySynReport = false;
                        break;
                    case EV_SYN:
                        switch(d.getSynCode()) {
                            case SYN_CONFIG:
                            default:
                                break;
                            case SYN_REPORT:
                                this.writeToActiveTimeline(time, positionX, positionY, touchMajor, displayedOrientation, appName);
                                clearData = true;
                                break;
                            case SYN_MT_REPORT:
                                tabletTime = (double)d.getTime() + (double)d.getTimeMicro() * 1.0E-6D;
                        }
                }

                if (clearData) {
                    time = -1.0D;
                    positionX = -1;
                    positionY = -1;
                    touchMajor = -1;
                    clearData = false;
                }

                ConstructedGesture activeGesture = this.getActiveGesture();
                activeGesture.addNative(d);
            }
        }

        if (emptySynReport && tabletTime > 0.0D) {
            i$ = this.slotTimelineMapping.keySet().iterator();

            while(i$.hasNext()) {
                Integer slot = (Integer)i$.next();
                this.currentSlot = slot;
                finished = this.finishCurrentSlot(tabletTime);
                if (finished != null && finished.hasPoints()) {
                    finishedGestures.add(finished);
                }
            }
        }

        return finishedGestures;
    }

    private ConstructedGesture getActiveGesture() {
        if (this.slotTimelineMapping.containsKey(this.currentSlot)) {
            return (ConstructedGesture)this.slotTimelineMapping.get(this.currentSlot);
        } else {
            ConstructedGesture activeTimeline = new ConstructedGesture(this.displaySizeX, this.displaySizeY);
            this.slotTimelineMapping.put(this.currentSlot, activeTimeline);
            return activeTimeline;
        }
    }

    private void writeToActiveTimeline(double time, int positionX, int positionY, int touchMajor, int displayedOrientation, String appName) {
        ConstructedGesture activeGesture = this.getActiveGesture();
        GestureDataPoint previous = activeGesture.getLastDataPoint();
        boolean hasMovement = true;
        if (previous != null) {
            if (positionX < 0) {
                positionX = previous.getNativePositionX();
            }

            if (positionY < 0) {
                positionY = previous.getNativePositionY();
            }

            hasMovement = positionX != previous.getNativePositionX() || positionY != previous.getNativePositionY();
            if (touchMajor < 0) {
                touchMajor = previous.getTouchMajor();
            }
        }

        boolean validTouchscreenValues = time > 0.0D && positionX > 0 && positionY > 0 && touchMajor > 0;
        if (validTouchscreenValues && hasMovement) {
            int[] rotatedPosition = this.rotate(positionX, positionY, displayedOrientation);
            int rotatedPositionX = rotatedPosition[0];
            int rotatedPositionY = rotatedPosition[1];
            GestureDataPoint dataPoint = new GestureDataPoint(time, rotatedPositionX, rotatedPositionY, positionX, positionY, touchMajor, displayedOrientation);
            activeGesture.add(dataPoint);
            activeGesture.setActiveApp(appName);
        }

    }

    private ConstructedGesture finishCurrentSlot(double time) {
        ConstructedGesture completedGesture = (ConstructedGesture)this.slotTimelineMapping.remove(this.currentSlot);
        if (completedGesture != null) {
            completedGesture.finish(time);
        }

        return completedGesture;
    }

    public void reset() {
        this.currentSlot = 0;
        this.slotTimelineMapping.clear();
    }

    private int[] rotate(int x, int y, int displayedOrientation) {
        int[] rotated = new int[]{x, y};
        int xToGo = this.displaySizeX - x;
        int yToGo = this.displaySizeY - y;
        switch(displayedOrientation) {
            case 0:
            default:
                break;
            case 1:
                rotated[0] = y;
                rotated[1] = xToGo;
                break;
            case 2:
                rotated[0] = xToGo;
                rotated[1] = yToGo;
                break;
            case 3:
                rotated[0] = yToGo;
                rotated[1] = x;
        }

        return rotated;
    }
}
