package screens;

import java.util.ArrayList;
import java.util.TreeMap;

public class Dialogue {

    /**
     * The text that the dialogue displays
     */
    private String text;

    /**
     * If the player has dialogue choices, they can make them here
     */
    private TreeMap<String, String> options;

    /**
     * Output following a response (or will be length 1 if !response)
     */
    private TreeMap<String, Dialogue> branches;

    /**
     * True if the dialogue expects a user response
     */
    private boolean response;

    /**
     * Current input for response
     */
    private String selection;

    /**
     * Letters referring to the index in the responses
     */
    private final String LETTERS = "abcdefghijklmnopqrstuvwxyz";

    public Dialogue(String text, boolean response, String[] options, Dialogue[] branches) {
        this.text = text;
        this.response = response;
        this.options = indexResponses(options);
        this.branches = indexBranches(options, branches);
        selection = "";
    }

    public Dialogue(String text, Dialogue branch) {
        this.text = text;
        this.response = false;
        this.options = null;
        this.branches = new TreeMap<String, Dialogue>() {
            {
                put("", branch);
            }
        };
        selection = "";
    }

    public TreeMap<String, String> indexResponses(String[] options) {

        TreeMap<String, String> map = new TreeMap<>();

        for(int i = 0; i < options.length; i++) {
            int index = i % LETTERS.length();
            int times = Math.floorDiv(i, LETTERS.length()) + 1;
            StringBuilder s = new StringBuilder();

            do {
                if(times % 2 == 0) {
                    s.append(Character.toUpperCase(LETTERS.charAt(index)));
                    times -= 2;
                }
                else {
                    s.append(LETTERS.charAt(index));
                    times -= 1;
                }

            } while (times > 0);

            map.put(s.toString(), options[i]);
        }

        return map;
    }

    public String intToIndex(int it) {

        int index = it % LETTERS.length();
        int times = Math.floorDiv(it, LETTERS.length()) + 1;
        StringBuilder s = new StringBuilder();

        do {
            if(times % 2 == 0) {
                s.append(Character.toUpperCase(LETTERS.charAt(index)));
                times -= 2;
            }
            else {
                s.append(LETTERS.charAt(index));
                times -= 1;
            }

        } while (times > 0);

        return s.toString();
    }

    public TreeMap<String, Dialogue> indexBranches(String[] options, Dialogue[] branches) {
        TreeMap<String, Dialogue> map = new TreeMap<>();

        for(int i = 0; i < options.length; i++) {
            if(i >= branches.length) break;
            map.put(options[i], branches[i]);
        }

        return map;
    }

    public boolean responseFound() {
        //If the index is more than one character long and starts with the selection, keep selection input
        boolean superSet = false;
        for(String s : options.keySet()) {
            //If the selection exists, return true
            if(s.equals(selection))
                return true;

            if(selection.length() > s.length())
                continue;

            if(s.substring(0, selection.length()).equals(selection))
                superSet = true;
        }

        if(!superSet)
            selection = "";

        return superSet;
    }

    //<editor-fold desc="Getters and Setters">
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TreeMap<String, String> getOptions() {
        return options;
    }

    public void setOptions(TreeMap<String, String> options) {
        this.options = options;
    }

    public String getResponseAt(String s) {
        return options.get(s);
    }

    public String getResponseAtSelection() {
        return options.get(selection);
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public TreeMap<String, Dialogue> getBranches() {
        return branches;
    }

    public void setBranches(TreeMap<String, Dialogue> branches) {
        this.branches = branches;
    }

    public void setBranches(Dialogue[] branches) {
        TreeMap<String, Dialogue> map = new TreeMap<>();

        for(int i = 0; i < branches.length; i++) {
            String s = getResponseAt(intToIndex(i));
            map.put(s, branches[i]);
        }

        this.branches = map;
    }

    public Dialogue getBranchAt(String s) {
        if(!response)
            return branches.get("");
        else
            return branches.get(s);
    }

    public void acceptInput(char c) {
        selection += c;
    }

    //</editor-fold>
}
