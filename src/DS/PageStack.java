package src.DS;
import src.cli.pages.*;

public class PageStack {
    private int top;
    private Page[] stack;
    private int maxSize;

    public PageStack(int maxSize) {
        this.maxSize = maxSize;
        stack = new Page[maxSize];
        for (int i = 0; i < maxSize; i++) {
            stack[i] = null;
        }
        top = -1;
    }
    public PageStack() {
        this(10);
    }


    public void push(Page page) {
        if (top == maxSize - 1) {
            increaseArrayCapacity();
        } else {
            top++;
            stack[top] = page;
        }
    }

    public Page pop() {
        if (top == -1) {
            System.out.println("Stack is empty");
            return null;
        } else {
            Page
     page = stack[top];
            top--;
            return page;
        }
    }

    public Page peek() {
        if (top == -1) {
            System.out.println("Stack is empty");
            return null;
        } else {
            return stack[top];
        }
    }

    public boolean isEmpty() {
        return (top == -1);
    }

    public boolean isFull() {
        return (top == maxSize - 1);
    }
    private void increaseArrayCapacity() {
        int newSize = maxSize * 2;
        Page
[] newStack = new Page
[newSize];
        for (int i = 0; i < maxSize; i++) {
            newStack[i] = stack[i];
        }
        stack = newStack;
        maxSize = newSize;
    }
    public void clear() {
        top = -1;
    }

}
