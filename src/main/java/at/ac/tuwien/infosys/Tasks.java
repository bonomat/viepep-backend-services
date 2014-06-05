package at.ac.tuwien.infosys;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tasks {

    private Map<Integer, Task> tasks = new HashMap<Integer, Task>();


    public Tasks() {
        tasks.put(1, new Task(10.0, 60.0));
        tasks.put(2, new Task(15.0, 40.0));
        tasks.put(3, new Task(55.0, 50.0));

    }

    public Task getTask(Integer taskId) {
        return tasks.get(taskId);
    }
}
