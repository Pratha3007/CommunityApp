package src.dao;

public class DaoManager {

    final PostDAO postDao;
    final UserDAO userDAO;
    final CommunityDAO communityDAO;
    final CommentDAO commentDAO;

    public DaoManager(){
        postDao = new PostDAO(this);
        userDAO = new UserDAO(this);
        communityDAO = new CommunityDAO(this);
        commentDAO = new CommentDAO(this);
    }


    public PostDAO getPostDao() {
        return postDao;
    }


    public UserDAO getUserDAO() {
        return userDAO;
    }


    public CommunityDAO getCommunityDAO() {
        return communityDAO;
    }

    public CommentDAO getCommentDAO() {
        return commentDAO;
    }

}
