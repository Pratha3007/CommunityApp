package src.session;

import src.cli.PageManager;
import src.model.User;

public class Session {
    private User user;
    private PageManager pageManager;
    public Session(){
        user = null;

    }
    public void setPageManager(PageManager pageManager){
        this.pageManager=pageManager;
    }
    public User getUser() {
        return user;
    }

    public void reloadUser(){
        user = pageManager.getUser();
    }

    public void logIn()  {
        
        user = pageManager.getLogin();
        // this.user = pageManager.getDummyLogin();//for instant login
        pageManager.startWithHome();
    }
    public void logOut(){
        user = null;
        logIn();
    }
}
