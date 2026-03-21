package hse.java.lectures.lecture6.tasks.synchronizer;

import java.util.List;

public class Synchronizer {

  public static final int DEFAULT_TICKS_PER_WRITER = 10;
  private final List<StreamWriter> tasks;
  private final int ticksPerWriter;

  public Synchronizer(List<StreamWriter> tasks) {
    this(tasks, DEFAULT_TICKS_PER_WRITER);
  }

  public Synchronizer(List<StreamWriter> tasks, int ticksPerWriter) {
    this.tasks = tasks;
    this.ticksPerWriter = ticksPerWriter;
  }

  public void execute() {
    StreamingMonitor monitor =
        new StreamingMonitor(tasks.size(), ticksPerWriter);

    for (StreamWriter writer : tasks) {
      writer.attachMonitor(monitor);
    }

    for (StreamWriter writer : tasks) {
      Thread worker = new Thread(writer, "stream-writer-" + writer.getId());
      worker.setDaemon(true);
      worker.start();
    }

    try {
      monitor.waitUntilDone();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
