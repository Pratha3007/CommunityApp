package src.cli.pages;

import src.cli.PageManager;
import src.model.Community;
import src.model.Post;
import java.util.ArrayList;

class NewPostPage extends BasePage {
    private final Community community;
    private final Post demoPost;
    private String title ;
    private String content ;

    protected NewPostPage(PageManager pageManager, Community community) {
        super(pageManager);
        this.community = community;
        demoPost = new Post(1,pageManager.getUser(),community,null,null,0,0);

        title = demoPost.getTitle();
        content = demoPost.getContent();
    }
    private NewPostPage(Post demoPost,PageManager pageManager, Community community){
        super(pageManager);
        this.demoPost = demoPost;
        this.community = community;
        title = demoPost.getTitle();
        content = demoPost.getContent();
    }

    @Override
    public void refresh() {
        pageManager.abortPage();
        Post newDemoPost = new Post(1,pageManager.getUser(),community,title,content,0,0);
        pageManager.addPageReplacement(new NewPostPage(newDemoPost,pageManager, community));
    }

    @Override
    public void display() {
        printInABox(getFormattedContent());
    }
    @Override
    public String help(){
        return super.help() + ", /title, /content, /post, /cancel";
    }

    @Override
    public ArrayList<String> input() {
        ArrayList<String> inputCommand = null;
        while(true) {
            inputCommand = super.abortOnCancelInput();

            if (inputCommand == null)
                return null;
            switch (inputCommand.getFirst()) {
                case "/title":
                    System.out.println("Enter Title");
                    System.out.println("-------------");
                    title = super.clearOnCancelInput().getFirst();
                    if(title==null)
                        return null;
                    refresh();
                    break;
                case "/content":
                    System.out.println("Enter Content");
                    System.out.println("-------------");
                    content = super.clearOnCancelInput().getFirst();
                    if(content==null)
                        return null;

                    refresh();
                    break;
                case "/confirm":
                case "/post":
                    if(title==null || content==null) {
                        System.out.println("Post Incomplete.");
                        continue;
                    }
                    pageManager.getPostController().addPost(demoPost);
                    System.out.println("Successfully posted.");
                    printRedirecting();

                    pageManager.abortPage();
                    pageManager.refresh();
                    break;
                default:
                    System.out.println(justifyAlign("Try : "+help(),width/2));
                    continue;
            }
            break;
        }
        return null;
    }
    private String getFormattedContent(){
        int innerWidth = width-4;
        StringBuffer sb = new StringBuffer();

        String title = demoPost.getTitle();
        sb.append(justifyAlign((title==null)? "<Title>" : title,innerWidth)).append('\n');

        String content = demoPost.getContent();
        sb.append(addIndentation(justifyAlign((content==null)?"<Content>":content,innerWidth-4/*indentation*/))).append('\n');

        sb.append(getPostInfoString(demoPost));
        return sb.toString();
    }
    public void printRedirecting() {
        System.out.print("Redirecting");
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.print(" .");
        }
        System.out.println();
    }
}
