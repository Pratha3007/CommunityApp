package src;

import src.cli.PageManager;
import src.controller.ControllerManager;
import src.dao.DaoManager;
import src.session.Session;

public class Run {
    public static void main(String[] args) {
        Session session = new Session();
        DaoManager dao = new DaoManager();
        ControllerManager controllerManager = new ControllerManager(dao);
        PageManager pageManager = new PageManager(controllerManager, session);
        session.setPageManager(pageManager);
        session.logIn();
    }
}
