package src.cli.pages;

import src.cli.PageManager;
import src.model.Community;
import src.model.Post;
import src.model.User;

import java.util.*;

public abstract class BasePage implements Page {

    protected final PageManager pageManager;
    protected int width = 100;

    protected BasePage(PageManager pageManager) {
        if( !  (this instanceof PreLoginPage) ) {
            pageManager.refreshSession();

        }
        this.pageManager = pageManager;
    }

    public int getWidth() {
        return width;
    }
    Scanner sc = new Scanner(System.in);

    abstract public void refresh();

    abstract public void display();
    public ArrayList<String> input(){
        return inputLogic();
    }
    private ArrayList<String> inputLogic(){
        ArrayList<String> inputCommand = new ArrayList<>();
        boolean hasLoadedNew = false;
        while(!hasLoadedNew){         //loop input till new page has been loaded
            String input = sc.nextLine();
            if(input.isBlank())
                continue;
            boolean isCommand = input.charAt(0)=='/';
            if(isCommand)
                inputCommand = splitCommand(input.toLowerCase());
            else {
                inputCommand.add(input);
                return inputCommand;
            }

            if(inputCommand.isEmpty())
                continue;
            switch (inputCommand.getFirst()) {
                case "/back":
                        if(pageManager.back())
                            hasLoadedNew = true;
                    break;
                case "/forward":
                    if(pageManager.forward())
                        hasLoadedNew = true;
                    break;
                case "/help":
                    printInABox(help());
                    continue;
                case "/refresh":
                    refresh();
                    hasLoadedNew = true;
                    break;
                case "/exit":
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    if(this instanceof PreLoginPage){
                        return inputCommand;
                    }
            }
            if(hasLoadedNew)//terminate thread
                return null;
            switch (inputCommand.get(0)) {
                case "/home":
                    pageManager.addNewPage(new HomePage(pageManager));
                    hasLoadedNew = true;
                    break;
                case "/editprofile":
                    pageManager.addNewPage(new EditProfilePage(pageManager));
                    hasLoadedNew = true;
                    break;
                case "/saves":
                    pageManager.addNewPage(new SavedPostsPage(pageManager));
                    hasLoadedNew = true;
                    break;
                case "/search":
                    break;
                case "/createcommunity":
                    pageManager.addNewPage(new CreateCommunityPage(pageManager));
                    hasLoadedNew = true;
                    break;
                case "/profile":
                    break;
                case "/visit":
                    if (inputCommand.size() > 1) {
                        String arg = inputCommand.get(1);

                        if (arg.startsWith("c/")) {
                            String communityName = arg.substring(2);
                            Community c = pageManager.getCommunityController().getCommunity(communityName);
                            if (c != null) {
                                pageManager.addNewPage(new CommunityPage(pageManager, c));
                                return null;
                            }
                            else
                                System.out.println("Community not found.");
                        } else if (arg.startsWith("u/")) {
                            String username = arg.substring(2);
                            User u = pageManager.getUserController().getUserByName(username);
                            if (u != null) {
                                pageManager.addNewPage(new UserProfilePage(pageManager, u));
                                return null;
                            }
                            else
                                System.out.println("User not found.");
                        } else
                            System.out.println("Invalid argument. Use /visit c/<community> or /visit u/<user>");

                    } else {
                        ArrayList<Community> myJoinedCommunities = pageManager.getUserController().getJoinedCommunities(pageManager.getUser());
                        if (myJoinedCommunities == null || myJoinedCommunities.isEmpty()) {
                            System.out.println("You have not joined any communities.");
                            continue;
                        }

                        System.out.println("Your joined communities:");
                        System.out.println(communityList(myJoinedCommunities));

                        ArrayList<Community> myCreatedCommunities = pageManager.getUserController().getMyCommunities(pageManager.getUser());
                        if (myCreatedCommunities != null && !myCreatedCommunities.isEmpty()) {
                            System.out.println("Your created communities:");
                            System.out.println(communityList(myCreatedCommunities));
                        }

                        boolean valid = false;
                        while (!valid) {
                            System.out.print("Enter index to visit ( /j <index> for joined , /c <index> for created): ");
                            ArrayList<String> inputAgain = clearOnCancelInput();

                            if (inputAgain == null)
                                return null;// it has already run as command so terminate the method

                            try {
                                String listType = "/j";
                                String indexToken;
                                if (inputAgain.size() >= 2) {
                                    listType = inputAgain.get(0);
                                    indexToken = inputAgain.get(1);
                                } else {
                                    indexToken = inputAgain.getFirst();
                                }

                                int index = Integer.parseInt(indexToken) - 1;
                                Community chosen;
                                if (listType.equalsIgnoreCase("/c")) {
                                    if (myCreatedCommunities == null || myCreatedCommunities.isEmpty()) {
                                        System.out.println("You have not created any communities.");
                                        continue;
                                    }
                                    chosen = myCreatedCommunities.get(index);
                                } else {
                                    chosen = myJoinedCommunities.get(index);
                                }

                                pageManager.addNewPage(new CommunityPage(pageManager, chosen));
                                hasLoadedNew = true;
                                valid = true;
                            } catch (NumberFormatException nfe) {
                                System.out.println("Enter a valid number or a command.");
                            } catch (IndexOutOfBoundsException iob) {
                                System.out.println("Number out of range.");
                            }
                        }
                        
                    }
                    break;
                case "/logout":
                    pageManager.logout();
                    hasLoadedNew = true;
                    break;
                default:
                    return inputCommand;
                }
        }
        return null;//a command was executed
    }
    
    String help(){
        return "/back, /forward, /help, /refresh, /exit, /home, /editprofile, /saves,/createcommunity,/visit, /cancel";
    }

    ArrayList<String> splitCommand(String command){
        String[] arguments = command.split(" ");
        ArrayList<String> argsArrayList = new ArrayList<>(List.of(arguments));
        argsArrayList.removeIf(String::isEmpty);//predicate
        return argsArrayList;
    }
    protected ArrayList<String> clearOnCancelInput(){
        ArrayList<String> input = this.inputLogic();

        if(input==null)
            return null;
        if (input.getFirst().equals("/cancel")) {
            pageManager.clearInputCanvas();
            return null;
        }
        return input;
    }
    protected ArrayList<String> abortOnCancelInput(){
        ArrayList<String> input = this.inputLogic();
        if(input==null)
            return null;
        if (input.getFirst().equals("/cancel")) {
            pageManager.abortPage();
            pageManager.loadNext();
            return null;
        }
        return input;
    }

    protected void printLineByLine(String string){
        String[] lines = string.split("\n");
        for(String line : lines) {
            System.out.println(line);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    protected void printWordByWord(String string){

    }
    protected void printInABox(String s) {
        int innerWidth = width - 4;
        StringBuffer sb = new StringBuffer();

        sb.append("_".repeat(width)).append("\n");

        String[] paragraphs = s.split("\n");
        for (String paragraph : paragraphs) {
            int start = 0;
            while (start < paragraph.length()) {
                int maxEnd = Math.min(start + innerWidth, paragraph.length());
                int end = maxEnd;

                if (maxEnd < paragraph.length()) {
                    int lastSpace = paragraph.lastIndexOf(' ', maxEnd);
                    if (lastSpace > start)
                        end = lastSpace;
                }

                String line = paragraph.substring(start, end);//removed .trim() , may break!!
                if (line.length() < innerWidth)
                    line = line + " ".repeat(innerWidth - line.length());

                sb.append("| ").append(line).append(" |\n");

                if (end == start)
                    end = maxEnd;
                start = end;
                while (start < paragraph.length() && paragraph.charAt(start) == ' ')
                    start++;
            }
            if (paragraph.isEmpty()) {
                sb.append("| ").append(" ".repeat(innerWidth)).append(" |\n");
            }
        }

        sb.append("|").append("_".repeat(width - 2)).append("|");
        System.out.println(sb);
    }
    protected String StringInABox(String s) {
        int innerWidth = maxLength(s.split("\n"));
        int width = innerWidth+4;
        StringBuffer sb = new StringBuffer();

        sb.append("_".repeat(width)).append("\n");

        String[] paragraphs = s.split("\n");
        for (String paragraph : paragraphs) {
            int start = 0;
            while (start < paragraph.length()) {
                int maxEnd = Math.min(start + innerWidth, paragraph.length());
                int end = maxEnd;

                if (maxEnd < paragraph.length()) {
                    int lastSpace = paragraph.lastIndexOf(' ', maxEnd);
                    if (lastSpace > start)
                        end = lastSpace;
                }

                String line = paragraph.substring(start, end);//removed .trim() , may break!!
                if (line.length() < innerWidth)
                    line = line + " ".repeat(innerWidth - line.length());

                sb.append("| ").append(line).append(" |\n");

                if (end == start)
                    end = maxEnd;
                start = end;
                while (start < paragraph.length() && paragraph.charAt(start) == ' ')
                    start++;
            }
            if (paragraph.isEmpty()) {
                sb.append("| ").append(" ".repeat(innerWidth)).append(" |\n");
            }
        }

        sb.append("|").append("_".repeat(width - 2)).append("|");
        return sb.toString();
    }

    protected String justifyAlign(String s,int width){
        StringBuffer sb = new StringBuffer();

        String[] paragraphs = s.split("\n");
        for (String paragraph : paragraphs) {
            int start = 0;
            while (start < paragraph.length()) {
                int maxEnd = Math.min(start + width, paragraph.length());
                int end = maxEnd;

                if (maxEnd < paragraph.length()) {
                    int lastSpace = paragraph.lastIndexOf(' ', maxEnd);
                    if (lastSpace > start)
                        end = lastSpace;
                }

                String line = paragraph.substring(start, end).trim();
                if (line.length() < width)
                    line = line + " ".repeat(width - line.length());

                sb.append(line).append('\n');
                if (end == start)
                    end = maxEnd;
                start = end;
                while (start < paragraph.length() && paragraph.charAt(start) == ' ')
                    start++;
            }
        }
        return sb.toString();
    }
    protected String addIndentation(String s){
        String[] lines = s.split("\n");

        StringBuffer sb = new StringBuffer();
        for(String line : lines)
            sb.append(" ".repeat(4)).append(line).append('\n');

        return sb.toString();
    }
    protected String addIndentation(String s,int spaces){
        String lines[] = s.split("\n");

        StringBuffer sb = new StringBuffer();
        for(String line : lines)
            sb.append(" ".repeat(spaces)).append(line).append('\n');

        return sb.toString();
    }

    protected String addCentringSpaces(String s, int width){
    
        String lines[]= s.split("\n");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < lines.length; i++) {
            int stringWidth = lines[i].length();
            int spaces = (width-stringWidth)/2;
            sb.append(" ".repeat(spaces)).
                    append(lines[i]).
                    append((" ".repeat(spaces)));
            if(i!= lines.length-1)
                sb.append('\n');
        }
        return sb.toString();
    }

    private String communityList(ArrayList<Community> communities){
        StringBuffer sb = new StringBuffer();
        int index = 1;
        for(Community c : communities)
            sb.append(index).append(". c/").append(c.getName()).append('\n');
        return sb.toString();
    }
    
    private int maxLength(String[] lines) {
        int max = 0;
        for (String line : lines) {
            if (line.length() > max) {
                max = line.length();
            }
        }
        return max;
    }
    public String getPostInfoString(Post post){
        int width = this.getWidth();
        if(!(this instanceof PostListPage))
            width = width-4;

        StringBuffer postInfo = new StringBuffer();
        int leftLength = postInfo.append(" ( ").append(post.getUpvotes()).append((post.getUpvotes()>0) ? '^' : 'v').append(" ) (").append(post.getCommentCount()).append("Q )").length()%width;
        int rightLength = 0;

        if(this instanceof CommunityPage){
            rightLength = (2 + post.getPostedByUser().getName().length())%width;
            postInfo.append(" ".repeat(width-leftLength-rightLength));
            postInfo.append("u/").append(post.getPostedByUser().getName());
        }
        else if(this instanceof UserProfilePage) {
            rightLength = (2 + post.getBelongsToCommunity().getName().length())%width;
            postInfo.append(" ".repeat(width-leftLength-rightLength));
            postInfo.append("c/").append(post.getBelongsToCommunity().getName());
        }
        else{
            rightLength = (7 + post.getPostedByUser().getName().length() + post.getBelongsToCommunity().getName().length())%width;
            postInfo.append(" ".repeat(width-leftLength-rightLength));
            postInfo.append("u/").append(post.getPostedByUser().getName());
            postInfo.append(" | ");
            postInfo.append("c/").append(post.getBelongsToCommunity().getName());
        }
        return postInfo.toString();
    }
}
