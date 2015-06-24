package eu.trentorise.opendata.schemamatcher.services.experiment;

public class ExperimentResult implements IExperimentResult {

    private long timeSpent;
    private long memoryUsed;
    private double precision;
    private double precisionFive;
    private double recall;
    private double map;

    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public void setMemoryUsed(long memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public void setPrecisionFive(double precisionFive) {
        this.precisionFive = precisionFive;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

    public void setMap(double map) {
        this.map = map;
    }

    public long getTimeSpent() {
        return this.timeSpent;
    }

    public long getMemoryUsed() {
        return this.memoryUsed;
    }

    public double getPrecision() {
        return this.precision;
    }

    public double getRecall() {
        return this.recall;
    }

    public double getMeanAveragePrecision() {
        return this.map;
    }

    public double getPrecisionFive() {
        return this.precisionFive;
    }

}
