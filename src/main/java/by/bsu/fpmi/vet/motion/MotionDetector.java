package by.bsu.fpmi.vet.motion;

public class MotionDetector {
    private MotionDetectionOptions options = new MotionDetectionOptions(5, 30, 16, false);

    public MotionDetectionOptions getOptions() {
        return options;
    }

    public void setOptions(MotionDetectionOptions options) {
        if (options != null) {
            this.options = options;
        }
    }
}
