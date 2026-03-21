package hse.java.lectures.lecture6.tasks.synchronizer;

public class StreamingMonitor {

  private final int writerCount;
  private final int ticksPerWriter;
  private int currentId = 1;
  private final int[] ticks;
  private boolean done = false;

  public StreamingMonitor(int writerCount, int ticksPerWriter) {
    this.writerCount = writerCount;
    this.ticksPerWriter = ticksPerWriter;
    this.ticks = new int[writerCount + 1];
  }

  public synchronized void awaitTurn(int id) throws InterruptedException {
    while (currentId != id && !done)
      wait();
    if (done)
      throw new InterruptedException("done");
  }

  public synchronized void completeTick(int id) {
    ticks[id]++;
    int next = (currentId % writerCount) + 1;
    while (ticks[next] >= ticksPerWriter) {
      next = (next % writerCount) + 1;
      if (next == id) {
        done = true;
        break;
      }
    }
    boolean allDone = true;
    for (int i = 1; i <= writerCount; i++) {
      if (ticks[i] < ticksPerWriter) {
        allDone = false;
        break;
      }
    }
    if (allDone)
      done = true;
    currentId = next;
    notifyAll();
  }

  public synchronized boolean isDone() { return done; }

  public synchronized void waitUntilDone() throws InterruptedException {
    while (!done)
      wait();
  }
}
