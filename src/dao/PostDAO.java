package src.dao;

import src.model.Post;
import src.model.User;
import src.model.Community;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {
    private final DaoManager dao;

    public PostDAO(DaoManager dao) {
        this.dao = dao;
    }

    public List<Post> getNewPostsByCommunityId(int communityId, int targetSize, int offset) {
        String sql = "SELECT * FROM posts WHERE community_id=? ORDER BY created_at DESC LIMIT ? OFFSET ? ";

        List<Post> posts = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, communityId);
            stmt.setInt(2,targetSize);
            stmt.setInt(3,offset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                posts.add(resultSetToPost(rs));
            }
        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }
        return posts;
    }

    public List<Post> getNewPostsByUserId(int userId,int targetSize,int offset)  {
        String sql = "SELECT * FROM posts WHERE user_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        List<Post> posts = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2,targetSize);
            stmt.setInt(3,offset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                posts.add(resultSetToPost(rs));
            }
        } catch (SQLException e) {
           throw new RuntimeException(e);
        }
        return posts;
    }

    public int getUpvotesByPostId(int postId) throws SQLException {
        String sql = "SELECT votes FROM posts WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("votes");
            } else {
                return 0;
            }
        }
    }

    public void addPost(int userId, int commId, String title, String content) {
        String sql = "INSERT INTO posts (title, content, user_id, community_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setInt(3, userId);
            stmt.setInt(4, commId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }
    }

    public boolean savePost(int postId, int userId) {
        // First check if the post is already saved
        if (isPostSavedByUserId(postId, userId)) {
            return false; // Already saved
        }
        
        String sql = "INSERT INTO saved_posts (user_id, post_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, postId);
            stmt.executeUpdate();
            return true; // Successfully saved
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean unsavePost(int postId, int userId) {
        // First check if the post is already unsaved
        if (!isPostSavedByUserId(postId, userId)) {
            return false; // Already unsaved
        }
        
        String sql = "DELETE FROM saved_posts WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            return true; // Successfully unsaved
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public boolean isPostSavedByUserId(int postId, int userId) {
        String sql = "SELECT COUNT(*) FROM saved_posts WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if count > 0
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    public List<Post> getSavedPostsByUserId(int userId,int count,int offset) {

        String sql = "SELECT p.* FROM posts p INNER JOIN saved_posts sp ON p.id = sp.post_id WHERE sp.user_id = ? LIMIT ? OFFSET ?";

        List<Post> posts = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, count);
            stmt.setInt(3, offset);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                posts.add(resultSetToPost(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return posts;
    }

    public boolean upvotePost(int postId, int userId) {
        String sql = "{ CALL handle_upvote(?, ?) }";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, postId);
            stmt.execute();

            // After procedure executes, check if the user has the upvote
            String checkSql = "SELECT COUNT(*) FROM post_upvotes WHERE post_id = ? AND user_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, postId);
                checkStmt.setInt(2, userId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0; // true if upvote exists, false if revoked
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false; // fallback
    }

    public boolean downvotePost(int postId, int userId) {
        String sql = "{ CALL handle_downvote(?, ?) }";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, postId);
            stmt.execute();

            // After procedure executes, check if the downvote exists
            String checkSql = "SELECT COUNT(*) FROM post_downvotes WHERE post_id = ? AND user_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, postId);
                checkStmt.setInt(2, userId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0; // true if downvote exists, false if revoked
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false; // fallback
    }
    
    public Post getPostByPostId(int postId) {
        String sql = "SELECT * FROM posts WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return resultSetToPost(rs);
            }
        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }
        return null;
    }

    public Post getRandomRecommendingPostByUserId(int userId) {
        String sql = " SELECT * FROM posts WHERE posts.id NOT IN (SELECT posts_id FROM recommended WHERE users_id = ?) ORDER BY RAND() LIMIT 1 ";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Post postToRecommend = resultSetToPost(rs);

                    try (PreparedStatement psInsert = conn.prepareStatement(
                            "INSERT INTO recommended(users_id, posts_id) VALUES(?, ?)"
                    )) {
                        psInsert.setInt(1, userId);
                        psInsert.setInt(2, postToRecommend.getPostId());
                        psInsert.executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return postToRecommend;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        public Post getJoinedCommunityPostByUserId(int userId) {
        String sql = """
         SELECT posts.* FROM posts JOIN join_community  ON posts.community_id = join_community.community_id 
         WHERE join_community.user_id = ? AND posts.id NOT IN (SELECT posts_id FROM recommended WHERE users_id = ?)
         ORDER BY posts.created_at DESC LIMIT 1  """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Post postToRecommend = resultSetToPost(rs);
                try (PreparedStatement psInsert = conn.prepareStatement(
                            "INSERT INTO recommended(users_id, posts_id) VALUES(?, ?)"
                    )) {
                        psInsert.setInt(1, userId);
                        psInsert.setInt(2, postToRecommend.getPostId());
                        psInsert.executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                return resultSetToPost(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    

    private Post resultSetToPost(ResultSet rs) {
        try {
            User postedByUser = dao.userDAO.getUserByUserId(rs.getInt("user_id"));
            Community belongsToCommunity = dao.communityDAO.getCommunityById(rs.getInt("community_id"));
            Post post = new Post(
                    rs.getInt("id"),
                    postedByUser,
                    belongsToCommunity,
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getInt("votes"),
                    rs.getInt("comments_count")
            );
            return post;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
