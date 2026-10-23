package ptit.e23cnpm01.nguyenbahung.math_dash.entity;

/**
 * Cấu hình game trên máy: âm thanh, rung, thời gian trả lời mỗi câu (chế độ offline).
 */
public class GameConfig {
    private boolean soundEnabled = true;
    private boolean vibrationEnabled = true;
    private int secondsPerQuestion = 6;

    public GameConfig() {
    }

    public GameConfig(boolean soundEnabled, boolean vibrationEnabled, int secondsPerQuestion) {
        this.soundEnabled = soundEnabled;
        this.vibrationEnabled = vibrationEnabled;
        this.secondsPerQuestion = secondsPerQuestion;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public boolean isVibrationEnabled() {
        return vibrationEnabled;
    }

    public void setVibrationEnabled(boolean vibrationEnabled) {
        this.vibrationEnabled = vibrationEnabled;
    }

    public int getSecondsPerQuestion() {
        return secondsPerQuestion;
    }

    public void setSecondsPerQuestion(int secondsPerQuestion) {
        this.secondsPerQuestion = secondsPerQuestion;
    }
}
