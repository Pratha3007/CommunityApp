package src.cli.pages;

import src.cli.PageManager;
import src.model.Community;
import src.model.Post;

import java.util.ArrayList;

class CommunityPage extends PostListPage {
    Community community;
    protected CommunityPage(PageManager pageManager, Community community) {

        super(pageManager);
        this.community = community;
        scrolldown(5);
    }
    
    @Override
    protected String help() {
        return super.help() + ", /createpost, /join, /unjoin";
    }

    @Override
    protected ArrayList<Post> getPostList(int count) {
        return pageManager.getPostController().getNewCommunityPosts(community,count,postIndexMapping.size());
    }

    @Override
    public void display() {
        printInABox(getFormattedDescription());
        super.display();
    }

    @Override
    public ArrayList<String> input() {
        ArrayList<String> inputCommand = null;
        boolean hasLoadedNew = false;
        while (!hasLoadedNew) {
            inputCommand = super.input();
            if (inputCommand == null)
                return null;
            switch (inputCommand.getFirst()) {
                case "/createpost":
                    pageManager.addNewPage(new NewPostPage(pageManager,community));
                    hasLoadedNew = true;
                    break;
                case "/join":
                    boolean alreadyJoined = !(pageManager.getCommunityController().joinCommunity(pageManager.getUser(), community));
                    if (alreadyJoined) {
                        System.out.println("You have already joined this community");
                    }else{
                        System.out.println("You have joined this community");
                    }
                    break;
                case "/unjoin":
                    boolean alreadyUnjoined = !(pageManager.getCommunityController().unJoinCommunity(pageManager.getUser(), community));
                    if (alreadyUnjoined) {
                        System.out.println("You haven't joint this community");
                    }else{
                        System.out.println("You have unjoined this community");
                    }
                    break;
                default:
                    System.out.println((justifyAlign("Try : "+help(),width)));
            }
        }
        return null;
    }

    private String getFormattedDescription(){
        int innerWidth = width-4;
        StringBuffer sb = new StringBuffer();
        sb.append(" ".repeat((innerWidth - community.getName().length()) / 2 - 1)).append("c/").append(community.getName()).append('\n');
        sb.append(justifyAlign(community.getDescription(),innerWidth)).append('\n');
        String noOfMembers = "Members: "+ pageManager.getCommunityController().getNoOfMembers(community);
        sb.append(" ".repeat((innerWidth- noOfMembers.length()))).append(noOfMembers);
        return sb.toString();
    }

    @Override
    public void refresh() {
        pageManager.abortPage();
        Community refreshedCom = pageManager.getCommunityController().reloadCommunity(community);
        pageManager.addPageReplacement(new CommunityPage(pageManager,refreshedCom));
    }
}
