package src.dao;
import src.model.*;

import java.sql.*;
import java.util.ArrayList;

public class CommentDAO {
    DaoManager dao;
    public CommentDAO(DaoManager daoManager) {
        dao = daoManager;
    }

    public void addRootComment(int userId, int postId, String content) {
        String sql = "INSERT INTO comments (user_id,post_id, content) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2,postId);
            stmt.setString(3, content);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
    public void addComment(int userId,int postId, int parentId, String content) {
        String sql = "INSERT INTO comments (user_id,post_id, parent_id, content) VALUES (?, ? , ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2,postId);
            stmt.setInt(3, parentId);
            stmt.setString(4, content);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Comment> getCommentsByPostId(Post post)  {
        ArrayList<Comment> comments = new ArrayList<>();

        String sql = "SELECT * FROM comments " +
                "WHERE post_id = ? AND parent_id IS NULL " +
                "ORDER BY id " +
                "LIMIT 5";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, post.getPostId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Comment comment = new Comment(post);
                Comment.ReplyNode root = buildReplyTree(
                        rs.getInt("id"),
                        post,
                        dao.getUserDAO().getUserByUserId(rs.getInt("user_id")),
                        rs.getString("content")
                );
                comment.setRootReply(root);
                comments.add(comment);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return comments;
    }

    // Recursive helper to build tree of replies

    private Comment.ReplyNode buildReplyTree(int commentId, Post post,User replier, String content) throws SQLException {
        ArrayList<Comment.ReplyNode> children = new ArrayList<>();

        String sql = "SELECT * FROM comments WHERE parent_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Comment.ReplyNode child = buildReplyTree(
                        rs.getInt("id"),
                        post,
                        dao.getUserDAO().getUserByUserId(rs.getInt("user_id")), // ideally fetch actual replier from DB
                        rs.getString("content")
                );
                children.add(child);
            }
        }
        return new Comment(post).new ReplyNode(commentId,children, post, replier, content);
    }

}
