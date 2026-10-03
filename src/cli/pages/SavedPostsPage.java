package src.cli.pages;
import src.cli.PageManager;
import src.model.Post;
import java.util.ArrayList;

class SavedPostsPage extends PostListPage {
    protected SavedPostsPage(PageManager pageManager) {
        super(pageManager);
        scrolldown(5);
    }

    @Override
    protected ArrayList<Post> getPostList(int count) {
        return pageManager.getPostController().getSavedPosts(pageManager.getUser(),count,postIndexMapping.size());
    }

    @Override
    public void display() {
        printInABox(addCentringSpaces("\nSaved Posts", width-4));
        if(onScreenPosts.isEmpty())
            System.out.println("No saved posts :(");
        else
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
    @Override
    public void refresh() {
        pageManager.abortPage();
        pageManager.addPageReplacement(new SavedPostsPage(pageManager));
    }

}