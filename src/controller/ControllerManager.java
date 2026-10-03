package src.controller;

import src.dao.DaoManager;

public class ControllerManager {
    private final DaoManager dao;

    private final CommunityController communityController ;
    private final PostController postController;
    private final UserController userController;


    public ControllerManager(DaoManager dao) {
        this.dao = dao;
        communityController = new CommunityController(dao);
        postController = new PostController(dao);
        userController = new UserController(dao);
    }

    public DaoManager getDao() {
        return dao;
    }

    public CommunityController getCommunityController() {
        return communityController;
    }

    public PostController getPostController() {
        return postController;
    }

    public UserController getUserController() {
        return userController;
    }
}
