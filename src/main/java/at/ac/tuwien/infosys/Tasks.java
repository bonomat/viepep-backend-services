package at.ac.tuwien.infosys;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tasks {

    private Map<Integer, Task> tasks = new HashMap<Integer, Task>();


    public Tasks() {
        tasks.put(1, new Task(5.0, 30.0));
        tasks.put(2, new Task(10.0, 80.0));
        tasks.put(3, new Task(20.0, 300.0));
        tasks.put(4, new Task(25.0, 100.0));
        tasks.put(5, new Task(50.0, 10.0));
        tasks.put(6, new Task(100.0, 20.0));
        tasks.put(7, new Task(120.0, 40.0));
        tasks.put(8, new Task(150.0, 20.0));
        tasks.put(9, new Task(200.0, 60.0));
        tasks.put(10, new Task(250.0, 30.0));

    }

    public Task getTask(Integer taskId) {
        return tasks.get(taskId);
    }
}
