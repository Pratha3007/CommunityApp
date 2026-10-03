package src.controller;

import src.dao.DaoManager;
import src.model.Community;
import src.model.User;

public class CommunityController {
    private final DaoManager dao;

    public CommunityController(DaoManager dao) {
        this.dao = dao;
    }

    public int getNoOfMembers(Community community) {
        if (community == null) {
            System.out.println("Community is null. Cannot fetch member count.");
            return 0;
        }

        int count = dao.getCommunityDAO().getNoOfMembers(community.getCommunityId());

        if (count <= 0) {
            System.out.println("No members found for community: " + community.getName());
            return 0;
        } else {
            return count;
        }
    }
    public Community reloadCommunity(Community community){
        return dao.getCommunityDAO().getCommunityById(community.getCommunityId());
    }
    public Community getCommunity(String name) {
        return dao.getCommunityDAO().getCommunityByName(name);

    }
    public void createCommunity(Community community) {
        dao.getCommunityDAO().addCommunity(community);
    }
    public boolean checkCommunityNameTaken(String name) {
        return dao.getCommunityDAO().getCommunityByName(name) != null;
    }
    public boolean joinCommunity(User user, Community community){
        return dao.getCommunityDAO().joinCommunity(user.getUid(), community.getCommunityId());
    }public boolean unJoinCommunity(User user, Community community){
        return dao.getCommunityDAO().unjoinCommunity(user.getUid(), community.getCommunityId());
    }


}
