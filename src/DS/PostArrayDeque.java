package src.DS;

import src.model.Post;

public class PostArrayDeque {
    private final Post[] deque;
    private final int capacity;
    private int top;     // front index
    private int bottom;  // rear index
    private int size;

    public PostArrayDeque(int capacity) {
        this.capacity = capacity;
        this.deque = new Post[capacity];
        this.top = -1;
        this.bottom = -1;
        this.size = 0;
    }

    // Default capacity = 5
    public PostArrayDeque() {
        this(5);
    }

    // Enqueue at top (front)
    public Post enqueueAtTop(Post post) {
        Post displaced = null;
        if (isFull()) {
            displaced = dequeueBottom(); // remove other end
        }

        if (isEmpty()) {
            top = bottom = 0;
        } else {
            top = (top - 1 + capacity) % capacity;
        }

        deque[top] = post;
        if (size < capacity) size++;
        return displaced;
    }

    // Enqueue at bottom (rear)
    public Post enqueueAtBottom(Post post) {
        Post displaced = null;
        if (isFull()) {
            displaced = dequeueTop(); // remove other end
        }

        if (isEmpty()) {
            top = bottom = 0;
        } else {
            bottom = (bottom + 1) % capacity;
        }

        deque[bottom] = post;
        if (size < capacity) size++;
        return displaced;
    }


    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;//(rear+1)%capacity == front

    }

    public int size() {
        return size;
    }

    public void clear() {
        for (int i = 0; i < capacity; i++) {
            deque[i] = null;
        }
        top = bottom = -1;
        size = 0;
    }

    // PRIVATE bcuz only used internally by enqueue
    private Post dequeueTop() {
        if (isEmpty()) return null;

        Post removed = deque[top];
        deque[top] = null;

        if (top == bottom) { // single element
            top = bottom = -1;
        } else {
            top = (top + 1) % capacity;
        }
        size--;
        return removed;
    }

    // PRIVATE — only used internally by enqueue
    private Post dequeueBottom() {
        if (isEmpty()) return null;

        Post removed = deque[bottom];
        deque[bottom] = null;

        if (top == bottom) { // single element
            top = bottom = -1;
        } else {
            bottom = (bottom - 1 + capacity) % capacity;
        }
        size--;
        return removed;
    }
    //returns top to bottom array
    public Post[] toArrayTopToBottom() {
        Post[] result = new Post[size];
        if (isEmpty()) return result;

        int index = top;
        for (int i = 0; i < size; i++) {
            result[i] = deque[index];
            index = (index + 1) % capacity;
        }
        return result;
    }
}

