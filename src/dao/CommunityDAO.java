package src.dao;

import src.model.Community;
import java.sql.*;
import java.util.ArrayList;

public class CommunityDAO {
    private final DaoManager dao;

    protected CommunityDAO(DaoManager dao) {

        this.dao = dao;
    }

    public void addCommunity(Community community) {

        int userId = community.getCreatedByUser().getUid();
        String name = community.getName();
        String desc = community.getDescription();
        
        String sql = "INSERT INTO communities (created_by, name, description) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, name);
            stmt.setString(3, desc);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
    public boolean joinCommunity(int userId, int communityId)  {
        String checkSql = "SELECT 1 FROM join_community WHERE user_id = ? AND community_id = ?";
        String insertSql = "INSERT INTO join_community (user_id, community_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, communityId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; // already joined
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, communityId);
            insertStmt.executeUpdate();
            return true;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    public boolean unjoinCommunity(int userId, int communityId) {
        String deleteSql = "DELETE FROM join_community WHERE user_id = ? AND community_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            deleteStmt.setInt(1, userId);
            deleteStmt.setInt(2, communityId);
            int rows = deleteStmt.executeUpdate();
            return rows > 0; // true if actually removed
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    public Community getCommunityById(int communityId)  {
        String sql = "SELECT * FROM communities WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, communityId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return  resultSetToCommunity(rs);
            }
            else {
                return null;
            }
        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }
    }

    public Community getCommunityByName(String name)  {
        String sql = "SELECT * FROM communities WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return  resultSetToCommunity(rs);
            }
            else {
                return null;
            }
        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }
    }
    public Community getCommunityByUserId(int userId)  {
        String sql = "SELECT * FROM communities WHERE created_by = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return  resultSetToCommunity(rs);
            }
        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }
        return null;
    }
    

    public ArrayList<Community> getCreatedCommunitiesByUserId(int userId) {
        ArrayList<Community> communities = new ArrayList<>();

        String sql = "SELECT * FROM communities WHERE created_by = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                communities.add(resultSetToCommunity(rs));
            }

        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }

        return communities;
    }

    public ArrayList<Community> getJoinedCommunitiesByUserId(int userId) {
        ArrayList<Community> communities = new ArrayList<>();

        String sql = "SELECT c.* FROM communities c JOIN join_community jc ON c.id = jc.community_id WHERE jc.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                communities.add(resultSetToCommunity(rs));
            }

        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }

        return communities;
    }

    public int getNoOfMembers(int communityId) {
        String sql = "SELECT members_count FROM communities WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, communityId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("members_count");
            } else {
                return 0;
            }

        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }
    }
    private Community resultSetToCommunity(ResultSet rs) {
        try {
            return new Community(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    dao.userDAO.getUserByUserId(rs.getInt("created_by"))
            );
        } catch (SQLException e) {
            return new Community(-1, "Unknown", "Error loading community", null);
        }
    }
}
