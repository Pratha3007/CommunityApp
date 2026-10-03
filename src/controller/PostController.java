package src.controller;

import src.dao.DaoManager;
import src.model.Comment;
import src.model.Community;
import src.model.Post;
import src.model.User;

import java.util.ArrayList;

public class PostController {
    private final DaoManager dao;

    public PostController(DaoManager dao) {
        this.dao = dao;
    }

    public ArrayList<Post> getRecommendedPosts(User user,int targetSize) {

        ArrayList<Post> result = new ArrayList<>();
        while (result.size() < targetSize) {
            Post post;
            if (Math.random() > 0.5)
                post = dao.getPostDao().getJoinedCommunityPostByUserId(user.getUid());
            else
                post = dao.getPostDao().getRandomRecommendingPostByUserId(user.getUid());

            if (post == null) {
                post = dao.getPostDao().getRandomRecommendingPostByUserId(user.getUid());
                if (post == null) {
                    post = dao.getPostDao().getJoinedCommunityPostByUserId(user.getUid());
                    if(post == null)//if none of them returns
                        return result;
                }
            }

            if (post != null && !result.contains(post))
                result.add(post);
            else if (post == null)
                break;
        }
        return result;
    }
    public boolean savePost(Post post,User user){
        return dao.getPostDao().savePost(post.getPostId(),user.getUid());
    }
    public boolean unsavePost(Post post,User user){
        return dao.getPostDao().unsavePost(post.getPostId(),user.getUid());
    }
    public boolean upvotePost(Post post,User user){
        return dao.getPostDao().upvotePost(post.getPostId(),user.getUid());
    }
    public boolean downvotePost(Post post,User user){
        return dao.getPostDao().downvotePost(post.getPostId(),user.getUid());
    }
    public ArrayList<Post> getNewCommunityPosts(Community community,int count,int offset) {
        return (ArrayList<Post>) dao.getPostDao().getNewPostsByCommunityId(community.getCommunityId(),count,offset);
    }

    public ArrayList<Post> getNewUserPosts(User user,int count,int offset) {
        return (ArrayList<Post>) dao.getPostDao().getNewPostsByUserId(user.getUid(),count,offset);
    }

    
    public ArrayList<Post> getSavedPosts(User user, int offset, int count){
        return (ArrayList<Post>) dao.getPostDao().getSavedPostsByUserId(user.getUid(),offset,count);
    }

    public ArrayList<Comment> getRootComments(Post post) {
        return dao.getCommentDAO().getCommentsByPostId(post);
    }

    public Post getPost(Post post) {
        return dao.getPostDao().getPostByPostId(post.getPostId());
    }

    public void addPost(Post demoPost) {
        dao.getPostDao().addPost(
                demoPost.getPostedByUser().getUid(),
                demoPost.getBelongsToCommunity().getCommunityId(),
                demoPost.getTitle(),
                demoPost.getContent());
    }

    public void addRootComment(User user,Post post,String content) {
        dao.getCommentDAO().addRootComment(user.getUid(), post.getPostId(),content );
    }

    public void addComment(User user, Comment.ReplyNode replyTo,String content) {
        dao.getCommentDAO().addComment(
                user.getUid(),
                replyTo.getRepliedOnPost().getPostId(),
                replyTo.getCommentId(),
                content);
    }
}
