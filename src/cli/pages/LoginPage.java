package src.cli.pages;

import src.cli.PageManager;
import src.model.User;

import java.util.ArrayList;

class LoginPage extends PreLoginPage {
    String email = "";
    String password = "";
    User suspectedUser;
    protected LoginPage(PageManager pageManager){
        super(pageManager);
    }

    @Override
    public void refresh() {
        pageManager.abortPage();
        pageManager.addPageReplacement(new LoginPage(pageManager));
    }

    private boolean signin(String email) {
        if (pageManager.getUserController().checkEmailRegistered(email)) {
            suspectedUser = pageManager.getUserController().getUserByEmail(email);
            return true;
        } else {
            System.out.println("Email not registered");
            return false;
        }
    }

    @Override
    public void display() {
        String s = "SIGN IN PAGE";
        printInABox(addCentringSpaces(s, width - 10) + "\n".repeat(2) + ("Email :- " + this.email) + "\n" + ("Password :- " + "*".repeat(this.password.length())) + "\n");
    }
    @Override
    public String help(){
        return "/email , /password , /confirm";
    }


    @Override
    public ArrayList<String> input() {
        ArrayList<String> inputCommand = null;
        boolean hasLoadedNew = false;
        while (!hasLoadedNew) {
            inputCommand = super.input();
            if (inputCommand == null)
                return null;
            switch (inputCommand.getFirst()) {
                case "/email":
                    System.out.print("Enter email :-");
                    String inputEmail;
                    try{
                     inputEmail = clearOnCancelInput().getFirst();
                    }catch(NullPointerException e){
                        return null;
                    }
                    if (signin(inputEmail)) {
                        this.email = inputEmail;
                        pageManager.redisplay();
                    }
                    break;
                case "/password":
                    pageManager.redisplay();
                    System.out.println("enter password");
                    String inputPassword;
                    try{
                    inputPassword = clearOnCancelInput().getFirst();
                    }catch(NullPointerException e){
                        return null;
                    }
                    if (suspectedUser.getPassword().equals(inputPassword)) {
                        this.password = inputPassword;
                        pageManager.redisplay();
                    } else {
                        System.out.println("Incorrect password");
                    }
                    break;
                case "/confirm":
                    if(this.email.isBlank() || this.password.isBlank())
                        System.out.println("Please complete Authorization.");
                    else {
                        loggedIn = suspectedUser;
                        synchronized (loggedInLock) {
                            loggedInLock.notifyAll(); // wake up all waiting threads
                        }
                        hasLoadedNew = true;// done with login input loop
                    }
                    break;
                default:
                    System.out.println("try : "+help());
            }
        }
        return null;
    }
}