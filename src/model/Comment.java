package src.model;

import java.util.ArrayList;

public class Comment{
    private ReplyNode rootReply;
    private final Post commentOnPost;

    public Comment(Post commentOnPost) {
        this.commentOnPost = commentOnPost;
    }

    public void setRootReply(ReplyNode rootReply) {
        this.rootReply = rootReply;
    }

    public Post getCommentOnPost() {
        return commentOnPost;
    }

    public ReplyNode getRootReply() {
        return rootReply;
    }

    public class ReplyNode{
        private final int commentId;

        private final String content;

        private final User replier;
        private final Post repliedOnPost;
        private final ArrayList<ReplyNode> childReplies;
        public ReplyNode(int commentId, ArrayList<ReplyNode> childReplies,Post repliedOnPost, User replier, String content) {
            this.commentId = commentId;
            this.childReplies = childReplies;
            this.repliedOnPost = repliedOnPost;
            this.replier = replier;
            this.content = content;
        }

        public int getCommentId() { return commentId;}

        public String getContent() {
            return content;
        }

        public User getReplier() {
            return replier;
        }

        public Post getRepliedOnPost() {
            return repliedOnPost;
        }

        public ArrayList<ReplyNode> getChildReplies() {
            return childReplies;
        }
    }
}
