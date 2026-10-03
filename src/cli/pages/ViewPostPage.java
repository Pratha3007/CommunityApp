package src.cli.pages;

import src.cli.PageManager;
import src.model.Comment;
import src.model.Post;

import java.util.ArrayList;
import java.util.HashMap;

class ViewPostPage extends BasePage {
    protected Post post;
    protected ArrayList<Comment> rootComments;
    protected HashMap<String,Comment.ReplyNode> indexedComments;

    protected ViewPostPage(PageManager pageManager, Post post) {
        super(pageManager);
        this.post = post;
        rootComments = pageManager.getPostController().getRootComments(post);
        indexedComments = new HashMap<>();
    }

    @Override
    public void display() {
        printInABox(getFormattedContent());
        printLineByLine(getNestedCommentsString());
    }
    @Override
    protected String help(){
        return super.help() + ", /upvote, /downvote, /reply, /reply <index>";
    }


    @Override
    public ArrayList<String> input() {
        ArrayList<String> inputCommand = null;
        boolean hasLoadedNew = false;
        while(!hasLoadedNew) {
            inputCommand = super.input();
            if(inputCommand==null)
                return null;
            switch (inputCommand.getFirst()) {
                case "/upvote":
                    boolean isUpvoted = pageManager.getPostController().upvotePost(post,pageManager.getUser());
                    if(isUpvoted)
                        System.out.println("Post upvoted successfully.");
                    else
                        System.out.println("Post upvote revoked.");
                    break;
                case "/downvote":
                    boolean isDownvoted = pageManager.getPostController().downvotePost(post,pageManager.getUser());
                    if(isDownvoted)
                        System.out.println("Post downvoted successfully.");
                    else
                        System.out.println("Post downvote revoked.");
                    break;

                case "/comment" :
                case "/reply":
                    boolean isRootReply = inputCommand.size()<2;
                    String index = null;
                    if(!isRootReply) {
                        index = inputCommand.get(1);
                        if (!indexedComments.containsKey(index)) {
                            System.out.println("Enter valid index.");
                            System.out.println("/reply <index>");
                            continue;
                        }
                    }
                    System.out.print("Enter reply: ");
                    String content = clearOnCancelInput().getFirst();
                    if(content==null)
                        return null;//a new page loaded

                    if(isRootReply){ // root comment
                        pageManager.getPostController().addRootComment(pageManager.getUser(),post,content);
                        refresh();
                        hasLoadedNew = true;
                    }else{
                        Comment.ReplyNode replyTo = indexedComments.get(index);
                        pageManager.getPostController().addComment(pageManager.getUser(),replyTo,content);
                        refresh();
                        hasLoadedNew = true;
                    } 
                    break;
                default:
                    System.out.println("try : "+help());
            }
        }
        return null;
    }

    @Override
    public void refresh() {
        pageManager.abortPage();
        pageManager.addNewPage(new ViewPostPage(pageManager, pageManager.getPostController().getPost(post)));
    }

    private String getFormattedContent(){
        int innerWidth = width-4;
        StringBuffer sb = new StringBuffer();
        sb.append(justifyAlign(post.getTitle(),innerWidth)).append('\n');
        sb.append(addIndentation(justifyAlign(post.getContent(),innerWidth-4/*indentation*/))).append('\n');
        sb.append(getPostInfoString(post));
        return sb.toString();
    }
    protected String getNestedCommentsString() {
        StringBuffer sb = new StringBuffer();

        int index = 1;
        for (Comment comment : rootComments) {
            String prefix = String.valueOf(index);
            sb.append(formatComment(comment.getRootReply(), prefix, 0));
            index++;
        }

        return sb.toString();
    }

    private String formatComment(Comment.ReplyNode node, String prefix, int indentLevel) {
        if (node == null) return "";

        indexedComments.put(prefix,node);
        StringBuilder sb = new StringBuilder();
        sb.append(prefix)
                .append("-- ")
                .append(node.getReplier().getName())
                .append(": ")
                .append(node.getContent())
                .append('\n');

        for(int i = 0; i < indentLevel; i++)
            sb = new StringBuilder(addIndentation(sb.toString()));

        int childIndex = 1;
        for (Comment.ReplyNode child : node.getChildReplies()) {
            String childPrefix = prefix + "." + childIndex;
            sb.append(formatComment(child, childPrefix, indentLevel + 1));
            childIndex++;
        }
        return sb.toString();
    }
    public String getCommentString(Comment.ReplyNode replyNode,String index, int commentWidth){
        StringBuffer commentString = new StringBuffer();
        int leftLength = commentString.append(index).append(" ").append(replyNode.getReplier().getName()).append(": ").length();
        commentString.append(addIndentation(justifyAlign(replyNode.getContent(),commentWidth-leftLength),leftLength).trim());
        return  commentString.toString();
    }

}
