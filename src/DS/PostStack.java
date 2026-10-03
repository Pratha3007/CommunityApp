package src.DS;
import src.model.Post;


public class PostStack {
    private int top;
    private Post[] stack;
    private int maxSize;

    public PostStack(int maxSize) {
        this.maxSize = maxSize;
        stack = new Post[maxSize];
        top = -1;
    }
    public void push(Post post) {
        if (top == maxSize - 1) {
            increaseArrayCapacity();
        } else {
            top++;
            stack[top] = post;
        }
    }
    public Post pop() {
        if (top == -1) {
            return null;
        } else {
            Post post = stack[top];
            top--;
            return post;
        }
    }
    public Post peek() {
        if (top == -1) {
            return null;
        } else {
            return stack[top];
        }
    }
    public void clear() {
        top = -1;
    }

    public boolean isEmpty() {
        return (top == -1);
    }
    public boolean isFull() {
        return (top == maxSize - 1);
    }
    public int size() {
        return top + 1;
    }
    private void increaseArrayCapacity() {
        int newSize = maxSize * 2;
        Post[] newStack = new Post[newSize];
        for (int i = 0; i < maxSize; i++) {
            newStack[i] = stack[i];
        }
        stack = newStack;
        maxSize = newSize;
    }

}
