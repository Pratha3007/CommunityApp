package src.cli.pages;

import java.util.ArrayList;

public interface Page {
    public void display();
    public ArrayList<String> input();
    public void refresh();
}
