package src.cli.pages;
import src.cli.PageManager;
import src.model.User;
import java.util.ArrayList;

class EditProfilePage extends BasePage{
    User user;
    protected EditProfilePage(PageManager pageManager){

        super(pageManager);
        user=pageManager.getUser();
    }
    public void display(){
       printInABox(addCentringSpaces("Edit Profile", width-10)+"\n"+
       "Name: "+user.getName()+"\n"+
       "Bio: "+user.getBio());
    }
    public void refresh(){
        pageManager.abortPage();
        pageManager.addNewPage(new EditProfilePage(pageManager));

    }
    @Override
    public ArrayList<String> input() {
        ArrayList<String> inputCommand;
        while(true) { 
            inputCommand = super.input();
            if(inputCommand==null)
                return null;
            switch (inputCommand.getFirst()) {
                case "/name":
                    System.out.print("Enter new name:");
                    String newName=clearOnCancelInput().getFirst();
                    if(newName == null)//a command was executed
                        return null;
                    boolean isAvailable = pageManager.getUserController().updateUsername(user.getUid(), newName);
                    if(!isAvailable) {
                        System.out.println("Username not available");
                    }
                    pageManager.refresh();

                    break;
                case "/bio":
                    System.out.print("Enter new bio:");
                    String newBio=clearOnCancelInput().getFirst();
                    if(newBio==null)//a command was executed
                        return null;
                    pageManager.getUserController().updateBio(user.getUid(), newBio);
                    refresh();
                    break;
                case "/done":
                    pageManager.abortPage();
                    pageManager.loadNext();
                    break;
                default:
                System.out.println(justifyAlign("Try : "+help(),width/2));
                continue;
            }
            break;
        }
        return null;
    }
    @Override
    protected String help(){
        return super.help() + ", /name, /bio, /done";
    }
}
