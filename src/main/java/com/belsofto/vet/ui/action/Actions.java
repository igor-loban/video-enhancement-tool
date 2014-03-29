package com.belsofto.vet.ui.action;

import javax.swing.Action;

public enum Actions implements ActionSource {
    FILE(new FileAction()),
    OPEN(new OpenAction()),
    EXIT(new ExitAction()),

    ANALYZE(new AnalyzeAction()),
    RUN_MOTION_DETECTION(new RunMotionDetectionAction()),
    MOTION_DETECTION_OPTIONS(new MotionDetectionOptionsAction()),
    RUN_SOUND_DETECTION(new RunSoundDetectionAction()),
    SOUND_DETECTION_OPTIONS(new SoundDetectionOptionsAction()),

    REPORT(new ReportAction()),
    VIEW_FRAMES_CAPTURED(new ViewFramesCapturedAction()),
    GENERATE_REPORT(new GenerateReportAction()),
    REPORT_OPTIONS(new ReportOptionsAction()),
    SHOW_LOG(new ShowLogAction()),

    HELP(new HelpAction()),
    ABOUT(new AboutAction());

    private final Action action;

    private Actions(Action action) {
        this.action = action;
    }

    public Action get() {
        return action;
    }
}
