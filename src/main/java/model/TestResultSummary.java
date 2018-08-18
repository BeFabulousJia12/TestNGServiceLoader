package model;

import java.sql.Timestamp;

/**
 * @Author You Jia
 * @Date 7/25/2018 1:11 PM
 */
public class TestResultSummary {
    String testName;
    int passed;
    int failed;
    int ignored;
    long duration;
    Timestamp timestamp;

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public int getFailed() {
        return failed;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getIgnored() {
        return ignored;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getPassed() {
        return passed;
    }

    public void setIgnored(int ignored) {
        this.ignored = ignored;
    }

    public long getDuration() {
        return duration;
    }

    public void setPassed(int passed) {
        this.passed = passed;
    }

}
