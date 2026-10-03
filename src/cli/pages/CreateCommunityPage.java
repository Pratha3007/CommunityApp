package src.cli.pages;

import src.cli.PageManager;
import src.model.Community;
import java.util.ArrayList;

class CreateCommunityPage extends BasePage {
    private String name = "";
    private String description = "";
    private String Msg="";

    protected CreateCommunityPage(PageManager pageManager) {

        super(pageManager);
    }
    
    @Override
    public void display() {
        if(!(name.isBlank() || description.isBlank())){
            Msg="Please Enter /create to create community.";
        }

        printInABox("\n"+addCentringSpaces("CREATE COMMUNITY"+"\n", width-4)+"\n"+

                
                "  Enter community details:             \n" +
                
                "  name: " + name +"\n" +
                "  description: " + description + "\n\n" +
                " " + Msg + "\n");
        Msg="";
    }
    @Override
    public String help(){
        return super.help()+"/name, /description, /create, or /cancel.";
    }

    
    @Override
    public ArrayList<String> input() {
        ArrayList<String> inputCommand = null;
        while(true) { 
            inputCommand = abortOnCancelInput();
            if(inputCommand==null)
                return null;
            switch (inputCommand.getFirst()) {
            case "/name":
                    System.out.print("Enter name :-");
                    String inputName;
                    try{
                        inputName=clearOnCancelInput().getFirst();//inputCommand.get(1);
                    }catch(NullPointerException e){
                        return null;
                    }
                    
                    if(cheakName(inputName)){
                        this.name=inputName;
                        pageManager.redisplay();
                        break;
                    }
                pageManager.redisplay();
                continue;
            case "/description":
                System.out.print("Enter description :-");
                String inputDescription;
                try{
                    inputDescription=clearOnCancelInput().getFirst();//inputCommand.get(1);
                }catch(NullPointerException e){
                    return null;
                }
                this.description=inputDescription;//inputCommand.get(1);
                pageManager.redisplay();
                continue;
            case "/create":
                if(createCommunity()){
                    System.out.println("Your community c/" + this.name +"has been created successfully.");
                    pageManager.abortPage();
                    pageManager.loadNext();
                    return null;
                }
                pageManager.redisplay();
                continue;
            default:
                System.out.println(justifyAlign("Try : "+help(),width/2));
            }
        }
       // return null;
    }
    
    @Override
    public void refresh() {
        pageManager.abortPage();
        pageManager.addPageReplacement(new CreateCommunityPage(pageManager));
    }
    private boolean cheakName(String name){
        boolean valid=false;

        if(name==null){
            Msg="Error: Community name is required.";
        }else if(name.contains(" ") ){
            Msg="Error: Community name must not contain spaces.";
        }else if(name.length()<3){
            Msg="Error: Community name must be at least 3 characters long.";
        }else if(name.length()>20){
            Msg="Error: Community name must be at most 20 characters long.";    
        }else if(pageManager.getCommunityController().checkCommunityNameTaken(name)){
            Msg="Error: Community name is already taken.";
        }else if(name.startsWith("/")){
            Msg="Error: Community name must not start with /.";
        }else{
            valid=true;
        }
        return valid;
    }
    private boolean createCommunity() {
        if (name == null || name.trim().isEmpty()) {
            Msg = "Error: Community name is required.";
            return false;
        }
        if (description == null || description.trim().isEmpty()) {
            Msg = "Error: Community description is required.";
            return false;
        }
            Community community = new Community(name, description,pageManager.getUser());
            pageManager.getCommunityController().createCommunity(community);
        return true;
    }
}
