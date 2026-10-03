package src.cli;

import src.DS.PageStack;
import src.cli.pages.PreLoginPage;
import src.cli.pages.HomePage;
import src.cli.pages.Page;
import src.cli.pages.PostListPage;
import src.controller.CommunityController;
import src.controller.ControllerManager;
import src.controller.PostController;
import src.controller.UserController;
import src.model.User;
import src.session.Session;


public class PageManager {
    private ControllerManager controllers;
    private final Session session;
    private User user;


    public CommunityController getCommunityController() {
        return controllers.getCommunityController();
    }

    public PostController getPostController() {
        return controllers.getPostController();
    }

    public UserController getUserController() {
        return controllers.getUserController();
    }

    public User getUser() {
        return user;

    }

    private final PageStack forStack;
    private final PageStack backStack;

    public PageManager(ControllerManager controllers, Session session) {
        this.session=session;
        this.controllers = controllers;
        forStack = new PageStack();
        backStack = new PageStack();
    }
    public void addNewPage(Page page){
        forStack.clear();
        backStack.push(page);
        loadNext();
    }
    public void addPageReplacement(Page page){    //used for refresh(after aborts)
        backStack.push(page);
        loadNext();
    }
    
    public boolean forward(){
        if(!forStack.isEmpty()){
            backStack.push(forStack.pop());
            if(backStack.peek() instanceof PostListPage){
                ((PostListPage) backStack.peek()).refreshSamePosts();//to keep  last changes
                loadNext();
            }
            else
                backStack.peek().refresh(); // to erase last changes
            return true;
        }
        else{
            System.out.println("Nothing to forward to.");
            return false;
        }
    }
    public boolean back(){
        backStack.peek();
        Page lastPage = backStack.pop();
        if(!backStack.isEmpty()) {
            forStack.push(lastPage);
            if(backStack.peek() instanceof PostListPage){
                ((PostListPage) backStack.peek()).refreshSamePosts();//to keep  last changes
                loadNext();
            }
            else
                backStack.peek().refresh(); // to erase last changes

            return true;
        }
        else{
            backStack.push(lastPage);//push back
            System.out.println("Nothing to go back.");
            return false;
        }
    }
    public void loadNext(){
        PageLoaderThread pageLoaderThread = new PageLoaderThread(backStack.peek());
        pageLoaderThread.start();
    }
    public void clearInputCanvas(){
        loadNext();
    }

    public void redisplay(){
        clearScreen();
        backStack.peek().display();
    }

    public void refreshSession(){
        user = getUserController().getUser(user.getUid());
        session.reloadUser();
    }

    private void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void abortPage() {
        backStack.pop();
    }

    private void clearStack(){
        forStack.clear();
        backStack.clear();
    }

    public void refresh(){
        refreshSession();
        backStack.peek().refresh();
    }
    public User getLogin() {
        user = null;
        clearStack();
        this.user = PreLoginPage.getLoggedIn(this);
        return user;
    }
    public void startWithHome(){
        clearStack();
        addNewPage(new HomePage(this));
    }
    public User getDummyLogin(){
        user = getUserController().getUser(190);
        return user;
    }

    public void logout() {
        clearStack();
        PreLoginPage.loggedIn = null;
        session.logOut();
    }

    private class PageLoaderThread extends Thread{
        private final Page page;

        public PageLoaderThread(Page page) {
            this.page = page;
        }
        public void run(){
            clearScreen();
            page.display();
            page.input();
        }
    }
}
