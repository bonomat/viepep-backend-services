package at.ac.tuwien.infosys;


public class Task {

    private Double cpu;
    private Double duration;
    private Double timeLeft;
    private Double actualCPU;
    private String UUID;

    public Task(Double cpu, Double duration) {
        this.cpu = cpu;
        this.duration = duration;
    }

    public Double getCpu() {
        return cpu;
    }

    public void setCpu(Double cpu) {
        this.cpu = cpu;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Double getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(Double timeLeft) {
        this.timeLeft = timeLeft;
    }

    public Double getActualCPU() {
        return actualCPU;
    }

    public void setActualCPU(Double actualCPU) {
        this.actualCPU = actualCPU;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
}
