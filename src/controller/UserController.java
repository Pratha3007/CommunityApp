
package src.controller;
import src.dao.DaoManager;
import src.model.Community;
import src.model.User;

import java.util.ArrayList;
public class UserController {
    private final DaoManager dao;

    public UserController(DaoManager dao) {
        this.dao = dao;
    }
    public Community getMyCommunity(int userId) {
        Community community = dao.getCommunityDAO().getCommunityByUserId(userId);
        if (community == null) {
            System.out.println("No community found for user ID: " + userId);
            return null;
        }
        else {
            return community;
        }
    }
    public boolean checkEmailRegistered(String email) {
        return dao.getUserDAO().checkEmailRegistered(email);
    }
    public boolean updateUsername(int uid, String name){
        return dao.getUserDAO().updateUsername(uid, name);
    }
    public boolean updateBio(int uid, String bio){
        return dao.getUserDAO().updateBio(uid, bio);
    }
    public boolean checkUserNameTaken(String name){
        return dao.getUserDAO().checkUserNameTaken(name);
    }

    public void addUser(User user){
        dao.getUserDAO().addUser(user);
    }
   
    public User getUserByEmail(String email){
        return dao.getUserDAO().getUserByEmail(email);
    }
    public User getUser(int uid) {
        return dao.getUserDAO().getUserByUserId(uid);
    }
    public ArrayList<Community> getJoinedCommunities(User user) {
        ArrayList<Community> communities = dao.getCommunityDAO().getJoinedCommunitiesByUserId(user.getUid());
        if (communities == null || communities.isEmpty()) {
            System.out.println("No communities found for this user.");
            return null;
        } else {
            return communities;
        }
    }
    public ArrayList<Community> getMyCommunities(User user) {
        ArrayList<Community> communities = dao.getCommunityDAO().getCreatedCommunitiesByUserId(user.getUid());
        if (communities == null || communities.isEmpty()) {
            return null;
        } else {
            return communities;
        }
    }


    public User getUserByName(String username) {
        return dao.getUserDAO().getUserByUserName(username);
    }


}

