package src.cli.pages;

import src.cli.PageManager;
import src.model.User;
import src.model.Post;
import java.util.ArrayList;

class UserProfilePage extends PostListPage {
    User user;
    protected UserProfilePage(PageManager pageManager, User user) {

        super(pageManager);
        this.user = user;
        scrolldown(5);
    }

    @Override
    protected ArrayList<Post> getPostList(int count) {
        return pageManager.getPostController().getNewUserPosts(user,count,postIndexMapping.size());
    }

    @Override
    public void display() {
        printInABox(getFormattedDescription());
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

    private String getFormattedDescription(){
        int innerWidth = width-4;
        StringBuffer sb = new StringBuffer();
        sb.append(user.getName()).append('\n');
        sb.append(addIndentation(justifyAlign(user.getBio(),innerWidth-4/*indentation*/)));
        return sb.toString();
    }

    @Override
    public void refresh() {
        pageManager.abortPage();
        pageManager.addPageReplacement(new UserProfilePage(pageManager, pageManager.getUserController().getUser(user.getUid())));
    }
    @Override
    public void refreshSamePosts(){
        super.refreshSamePosts();
        user = pageManager.getUserController().getUser(user.getUid());
    }



}
