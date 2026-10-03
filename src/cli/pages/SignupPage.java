package src.cli.pages;
import src.cli.PageManager;
import src.model.User;
import java.util.ArrayList;

class SignupPage extends PreLoginPage {
    String name="";
    String email="";
    String password ="";
    String bio ="";

    protected SignupPage(PageManager pageManager){
        super(pageManager);
    }

    @Override
    public void display() {
        printInABox(addCentringSpaces("SIGN UP PAGE", width-10)+"\n".repeat(2)+"Enter your details to sign up"+"\n"+"Name :- "+this.name+"\n"+"Email :- "+this.email+"\n"+"Password :- "+"*".repeat(this.password.length())+"\n"+"Bio :- "+this.bio);
    }

    @Override
    protected String help(){
        return "/name, /email, /password, /bio, /confirm";
    }

    @Override
    public ArrayList<String> input(){
        ArrayList<String> inputCommand = null;
        boolean hasLoadedNew = false;
        while(!hasLoadedNew) { 
            inputCommand = super.input();
            if(inputCommand==null)
                return null;
            switch (inputCommand.getFirst()) {
                case "/name":
                    System.out.print("Enter name :-");
                    String name;
                    try{
                    name=clearOnCancelInput().getFirst();
                    }catch(NullPointerException e){
                        return null;
                    }
                    if(validateName(name)){
                        this.name=name;
                        pageManager.redisplay();
                    }
                    break;
                case "/email":
                    System.out.print("Enter email :-");
                    String email;
                    try{
                    email=clearOnCancelInput().getFirst();//inputCommand.get(1);    
                    }catch(NullPointerException e){
                        return null;
                    }
                    if(validateEmail(email)){
                        this.email=email;
                        pageManager.redisplay();
                    }
                    break;

                case "/password":
                    System.out.println("Enter password :-");
                    String password;
                    try{
                    password=clearOnCancelInput().getFirst();//inputCommand.get(1);
                    }catch(NullPointerException e){
                        return null;
                    }
                    if(validatepassword(password)){
                        this.password=password;
                        pageManager.redisplay();
                    }
                    break;

                case "/bio":
                    pageManager.redisplay();
                    System.out.println("Enter bio :-");
                    String bio;
                    try{
                    bio=clearOnCancelInput().getFirst();//inputCommand.get(1);
                    }catch(NullPointerException e){
                        return null;
                    }
                    this.bio=bio;
                    pageManager.redisplay();
                    break;
                case "/confirm":
                    if(validateName(this.name) && validateEmail(this.email) && validatepassword(this.password)){
                        User suspect = new User(this.name,this.email,this.password,this.bio);
                        pageManager.getUserController().addUser(suspect);
                        loggedIn = pageManager.getUserController().getUserByName(suspect.getName());
                        synchronized (loggedInLock) {
                            loggedInLock.notifyAll(); // wake up all waiting threads
                        }
                        hasLoadedNew = true;
                    }
                    break;
                default:
                    System.out.println("try : "+help());
                    break;
            }
        }
        return null;
    }
    @Override
    public void refresh() {
        pageManager.abortPage();
        pageManager.addPageReplacement(new SignupPage(pageManager));
    }
     private boolean validatepassword(String password){
        if (password == null || password.trim().isEmpty()) {
            System.out.println("Password cannot be empty.");
            return false;
        }
        password = password.trim();
        if (password.length() < 8) {
            System.out.println("Password is too short. Must be at least 8 characters.");
            return false;
        }
        if (password.length() > 20) {
            System.out.println("Password is too long. Must be less than 20 characters.");
            return false;
        }
        return true;
    }
    private boolean validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println(" Name cannot be empty.");
            return false;
        }
        name = name.trim();
        if (name.length() < 2) {
            System.out.println(" Name is too short. Must be at least 2 characters.");
            return false;
        }
        if (name.length() > 30) {
            System.out.println(" Name is too long. Must be less than 50 characters.");
            return false;
        }
        for (int i = 0; i < name.length(); i++){
            char ch = name.charAt(i);
            boolean isLetter = false;
            for (char c = 'A'; c <= 'Z'; c++) {
                if (ch == c || ch == (char)(c + 32)) { // also check lowercase
                    isLetter = true;
                    break;
                }
            }
            if (!isLetter && ch != '_' && ch != '.') {
                System.out.println(" Invalid character found: '" + ch + "'. Only letters, underscores, and dots are allowed.");
                return false;
            }
        }
        if(pageManager.getUserController().checkUserNameTaken(name)){
            System.out.println("Name already taken");
            return false;
        }
        return true;
    }
    private boolean validateEmail(String email){
            if (email == null) {
                System.out.println("Invalid email: cannot be empty");
                return false;
            }
            email = email.trim();
            int atIndex = -1;
            for (int i = 0; i < email.length(); i++) {
                if (email.charAt(i) == '@') {
                    atIndex = i;
                    break;
                }
            }
            String domain1="@gmail.com";
            String domain2="@yahoo.com";
            String domain3="@outlook.com";
            String domain4="@hotmail.com";
            String domain5="@example.com";
            if(!email.endsWith(domain1) && !email.endsWith(domain2) && !email.endsWith(domain3) && !email.endsWith(domain4) && !email.endsWith(domain5)){
                System.out.println(" Invalid email: @gmail.com,@yahoo.com,@outlook.com,@hotmail.com or @example.com is missing ");  
                return false;
            }
            if (atIndex < 1) {
                System.out.println("Invalid email: username is too short");
                return false;
            }
            for (int i = 0; i < atIndex; i++) {
                char ch = email.charAt(i);
                boolean isValidChar = false;
                for (char c = 'a'; c <= 'z'; c++) {
                    if (ch == c || ch == (char)(c - 32)) {
                        isValidChar = true;
                        break;
                    }
                }
                for (char d = '0'; d <= '9'; d++) {
                    if (ch == d) {
                        isValidChar = true;
                        break;
                    }
                }
                if (!isValidChar && ch != '_' && ch != '-' && ch != '.') {
                    System.out.println("Invalid email: invalid character in username - " + ch);
                    return false;
                }
            }
            if(pageManager.getUserController().checkEmailRegistered(email)){
                System.out.println("Email already registered");
                return false;
            }
    
            return true;
    }

}
