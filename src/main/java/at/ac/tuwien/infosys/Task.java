package at.ac.tuwien.infosys;


import java.io.Serializable;

public class Task implements Serializable {

    private Double cpu;
    private Double duration;
    private Double timeLeft;
    private Double actualCPU;
    private String UUID;
    private String id;
    private Boolean dataSimulation;
    private Integer dataSize;

    public Task(Double cpu, Double duration, Integer dataSize) {
        this.cpu = cpu;
        this.duration = duration;
        this.dataSize = dataSize;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getDataSimulation() {
        return dataSimulation;
    }

    public void setDataSimulation(Boolean dataSimulation) {
        this.dataSimulation = dataSimulation;
    }

    public Integer getDataSize() {
        return dataSize;
    }

    public void setDataSize(Integer dataSize) {
        this.dataSize = dataSize;
    }

}
