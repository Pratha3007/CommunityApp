package src.model;

public class Post {
    private int postId;
    private final Community belongsToCommunity;
    private final User postedByUser;
    private final String title;
    private final String content;
    private final int upvotes;
    private final int commentCount;

    public Post(int postId,User postedByUser,Community belongsToCommunity, String title, String content,int upvotes,int commentCount) {
        this.postId = postId;
        this.postedByUser = postedByUser;
        this.belongsToCommunity = belongsToCommunity;
        this.title = title;
        this.content = content;
        this.upvotes = upvotes;
        this.commentCount = commentCount;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public User getPostedByUser() {
        return postedByUser;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public Community getBelongsToCommunity() {
        return belongsToCommunity;
    }

    public int getPostId() {
        return postId;
    }

}
