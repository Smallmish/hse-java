package hse.java.lectures.lecture6.tasks.queue;

public class BoundedBlockingQueue<T> {

    private final Object[] buffer;
    private final int capacity;
    private int head, tail, size;

    public BoundedBlockingQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;
        this.buffer = new Object[capacity];
    }

    public synchronized void put(T item) throws InterruptedException {
        if (item == null) throw new NullPointerException("null items are not allowed");
        while (size == capacity) wait();
        buffer[tail] = item;
        tail = (tail + 1) % capacity;
        size++;
        notifyAll();
    }

    @SuppressWarnings("unchecked")
    public synchronized T take() throws InterruptedException {
        while (size == 0) wait();
        T item = (T) buffer[head];
        buffer[head] = null;
        head = (head + 1) % capacity;
        size--;
        notifyAll();
        return item;
    }

    public synchronized int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }
}
