package src.cli.pages;

import src.cli.PageManager;
import src.model.Post;

import java.util.ArrayList;

public class HomePage extends PostListPage {
    public HomePage(PageManager pageManager) {
        super(pageManager);
        scrolldown(5);
    }

    @Override
    public ArrayList<Post> getPostList(int count) {
        return pageManager.getPostController().getRecommendedPosts(pageManager.getUser(),count);
    }

    @Override
    public void refresh() {
        pageManager.abortPage();
        pageManager.addPageReplacement(new HomePage(pageManager));
    }
    @Override
    public void display() {
        printInABox("\n "+addCentringSpaces("HOME"+"\n", width-4));

        super.display();
    }

    @Override
    public ArrayList<String> input() {
        ArrayList<String> inputCommand = null;
        while(true) {
            inputCommand = super.input();
            if(inputCommand==null)
                return null;
            else
                System.out.println((justifyAlign("Try : "+help(),width)));
        }
    }
}
