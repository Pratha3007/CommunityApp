package src.cli.pages;

import src.cli.PageManager;
import src.model.User;

import java.util.ArrayList;


public class PreLoginPage extends BasePage {
    public static User loggedIn;
    public static final Object loggedInLock = new Object();
    public static boolean supportInformed = false;


    PreLoginPage(PageManager pageManager) {
        super(pageManager);
    }

    public static User getLoggedIn(PageManager pageManager) {

        pageManager.addNewPage(new PreLoginPage(pageManager));

        while (loggedIn == null) {
            try {
                synchronized (loggedInLock) {
                    loggedInLock.wait(); // wait releases the lock
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        return loggedIn;
    }

    @Override
    public void display() {
        String ascii =
" _______ _                       _ _        \n" +
"|__   __| |                     | | |       \n" +
"   | |  | |__  _ __ ___  __ _ __| | | ___   \n" +
"   | |  | '_ \\| '__/ _ \\/ _` / _` | |/ _ \\  \n" +
"   | |  | | | | | |  __/ (_| | (_| | |  __/ \n" +
"   |_|  |_| |_|_|  \\___|\\__,_|\\__,_|_|\\___| \n";


        printInABox(addCentringSpaces(ascii, width - 4) + "\n".repeat(3)
                + addCentringSpaces(StringInABox(addCentringSpaces("\nSignUp", 20)) + "\n" + StringInABox(addCentringSpaces("\nSignin", 20)), width - 4)
                + "\n ".repeat(3));
                if(!supportInformed){
                    System.out.println("For support, Enter : /help");
                    supportInformed=true;
                }
    }

    @Override
    public ArrayList<String> input() {
        ArrayList<String> inputCommand = null;
        boolean hasLoadedNew = false;
        while (!hasLoadedNew) {
            inputCommand = super.input();
            if (inputCommand == null) {
                return null;
            }
            switch (inputCommand.getFirst()) {
                case "/signup":
                    pageManager.addNewPage(new SignupPage(pageManager));
                    hasLoadedNew = true;
                    break;
                case "/signin":
                    pageManager.addNewPage(new LoginPage(pageManager));
                    hasLoadedNew = true;
                    break;
                default:
                    if (!(this instanceof SignupPage || this instanceof LoginPage)) {
                        System.out.println(justifyAlign("Try : "+help(),width));
                    }
                    else
                        return inputCommand;

            }
        }
        return null;
    }

    @Override
    public void refresh() {
        pageManager.clearInputCanvas();
    }
    @Override
    protected String help(){
        return "/signup, /signin, /exit";
    }


}
