package src.cli.pages;

import src.DS.PostArrayDeque;
import src.DS.PostStack;
import src.cli.PageManager;
import src.model.Post;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class PostListPage extends BasePage {
    protected PostArrayDeque onScreenPosts;
    protected PostStack upStack;
    protected PostStack downStack;
    protected PostIndexMapping postIndexMapping;
    protected PostListPage(PageManager pageManager) {
        super(pageManager);
        onScreenPosts = new PostArrayDeque(5);
        upStack = new PostStack(5);
        downStack = new PostStack(5);
        postIndexMapping = new PostIndexMapping();

    }

    protected abstract ArrayList<Post> getPostList(int count);

    public void display() {
        if (onScreenPosts.isEmpty())
            System.out.println("No more posts :(");
        else
            for (Post post : onScreenPosts.toArrayTopToBottom())
                displayPostPreview(post);
    }

    @Override
    protected String help() {
        return super.help() + ", /scrollup <count> , /scrolldown <count> , /scroll, /open <index_number>, /save <index_number>, /unsave <index_number>, /back";
    }

    public ArrayList<String> input() {
        ArrayList<String> inputCommand = null;
        boolean hasLoadedNew = false;
        while (!hasLoadedNew) {
            inputCommand = super.input();
            if (inputCommand == null)
                return null;
            switch (inputCommand.getFirst()) {
                case "/scrollup":
                    int count = 4;
                    if (inputCommand.size() == 1) {
                        count = 4;
                    } else {
                        try {
                            count = Integer.parseInt(inputCommand.get(1));
                        } catch (NumberFormatException numberFormatException) {
                            System.out.println("/scrollup <count>");
                            continue;
                        }
                    }
                    boolean hasScrolledUp = scrollup(count);
                    if (!hasScrolledUp) {
                        System.out.println("nothing to scroll up");
                    }else{
                        pageManager.redisplay();
                    }
                    break;
                case "/scroll":
                case "/scrolldown":
                    count = 4;
                    if (inputCommand.size() == 1) {
                        count = 4;
                    } else {
                        try {
                            count = Integer.parseInt(inputCommand.get(1));
                        } catch (NumberFormatException numberFormatException) {
                            System.out.println("/scrolldown <count>");
                            continue;
                        }
                    }
                    boolean hasScrolledDown = scrolldown(count);
                    if (!hasScrolledDown) {
                        System.out.println("End of the Page.\n" +
                                "You have seen it all!!!");
                    }else{
                        pageManager.redisplay();
                    }
                    break;
                case "/open":
                    try {
                        int index = Integer.parseInt(inputCommand.get(1));
                        Post post = postIndexMapping.getPost(index);

                        if(post==null){
                            System.out.println("Enter valid index.");
                            continue;
                        }
                        pageManager.addNewPage(new ViewPostPage(pageManager, post));

                        hasLoadedNew = true;
                    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                        System.out.println("Enter valid index.");
                    } catch (NumberFormatException numberFormatException) {
                        System.out.println("Enter valid command");
                        System.out.println("/open <index_number>");
                    }
                    break;
                case "/save":
                    try {
                        if (inputCommand.size() == 1) {
                            System.out.println("Enter valid command");
                            System.out.println("/save <index_number>");
                            continue;
                        }

                        int index = Integer.parseInt(inputCommand.get(1));
                        Post post = postIndexMapping.getPost(index);
                        if(post==null){
                            System.out.println("Enter valid index.");
                            continue;
                        }
                        boolean yetToBeSaved = pageManager.getPostController().savePost(post, pageManager.getUser());
                        if (yetToBeSaved)
                            System.out.println("Post saved successfully.");
                        else
                            System.out.println("Post already saved.");
                    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                        System.out.println("Enter valid index.");
                    } catch (NumberFormatException numberFormatException) {
                        System.out.println("Enter valid command");
                        System.out.println("/save <index_number>");
                    }
                    break;
                case "/unsave":
                    try {
                        if (inputCommand.size() == 1) {
                            System.out.println("Enter valid command");
                            System.out.println("/unsave <index_number>");
                            continue;
                        }

                        int index = Integer.parseInt(inputCommand.get(1));
                        Post post = postIndexMapping.getPost(index);
                        if(post==null){
                            System.out.println("Enter valid index.");
                            continue;
                        }
                        boolean isUnsaved = pageManager.getPostController().unsavePost(post, pageManager.getUser());
                        if (isUnsaved)
                            System.out.println("Post unsaved successfully.");
                        else
                            System.out.println("Post not saved.");
                    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                        System.out.println("Enter valid index.");
                    } catch (NumberFormatException numberFormatException) {
                        System.out.println("Enter valid command");
                        System.out.println("/unsave <index_number>");
                    }
                    break;
                default:
                    return inputCommand;
            }
        }
        return null;
    }

    public void refreshSamePosts() {
        postIndexMapping.clear();

        PostStack postStack = new PostStack(5);

        for (Post post : onScreenPosts.toArrayTopToBottom()) //deque extract
            upStack.push(post); //pushing it in upStack, and re-getting it so we dont get reverse

        onScreenPosts.clear();

        while (!upStack.isEmpty())          //upSatck extract
            postStack.push(upStack.pop());

        while (!postStack.isEmpty()){
            Post reloadedPost = pageManager.getPostController().getPost(postStack.pop());
            onScreenPosts.enqueueAtBottom(reloadedPost);
            postIndexMapping.addPost(reloadedPost);
        }

        while (!downStack.isEmpty())      //downStack extract
            postStack.push(downStack.pop());
        
        while (!postStack.isEmpty()){
            Post reloadedPost = pageManager.getPostController().getPost(postStack.pop());
            downStack.push(reloadedPost);
            postIndexMapping.addPost(reloadedPost);

        }
    }

    void displayPostPreview(Post post) {
        int index = postIndexMapping.getIndex(post);

        StringBuffer previewString = new StringBuffer();
        previewString.append(index).append(". ").append(justifyAlign(post.getTitle(), width - (index + ". ").length())).append('\n');

        String content = post.getContent();
        String contentPreview = content.substring(0, Math.min(content.length(), 40));
        previewString.append(" ".repeat(10)).append(contentPreview).append("...").append('\n');

        previewString.append(getPostInfoString(post)).append('\n');
        previewString.append("-".repeat(width)).append('\n');
        System.out.println(previewString.toString());
    }

    boolean scrolldown(int count) {// returns if we have posts
        boolean hasScrolled = false;
        while (!downStack.isEmpty() && count > 0) {
            Post post = downStack.pop();
            Post returned = onScreenPosts.enqueueAtBottom(post);
            if (returned != null) {
                upStack.push(returned);
            }
            count--;
            hasScrolled = true;
        }
        for (Post post : getPostList(count)) {
            postIndexMapping.addPost(post);
            Post returned = onScreenPosts.enqueueAtBottom(post);

            if (returned != null) {
                upStack.push(returned);
            } 
            hasScrolled = true;
        }
        return hasScrolled;
    }

    protected boolean scrollup(int count) {

        boolean hasScrolled = false;
        while (!upStack.isEmpty() && count > 0) {
            Post post = upStack.pop();
            Post returned = onScreenPosts.enqueueAtTop(post);
            if (returned != null) {
                downStack.push(returned);
            }
            count--;
            hasScrolled = true;
        }   
        return hasScrolled;
    }
    class PostIndexMapping{
        private int index;
        private Post post;
        private HashMap<Post,Integer> postIndexed;
        private HashMap<Integer,Post> indexedPosts;

        PostIndexMapping(){
            postIndexed = new HashMap<>();
            indexedPosts = new HashMap<>();
        }
        void addPost(Post post){
            index = postIndexed.size()+1;
            postIndexed.put(post,index);
            indexedPosts.put(index,post);
        }
        void removePost(Post post){
            index = postIndexed.get(post);
            postIndexed.remove(post);
            indexedPosts.remove(index);
        }
        int getIndex(Post post){
            return postIndexed.get(post);
        }
        Post getPost(int index){
            return indexedPosts.get(index);
        }
        void clear(){
            postIndexed.clear();
            indexedPosts.clear();
        }
        int size(){
            return postIndexed.size();
        }
    }
}
