package com.mistareader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.mistareader.TextProcessors.JSONProcessor;


public class Section {

    public String sectionShortName;
    public String sectionFullName;
    public String forumName;
    public String sectionId;
    
    public static String getSectionID(String strValue) {
        return "15";//dummy until server side will return proper ID's
    }

   
    public static ArrayList<String> getUniqueForums(ArrayList<Section> sections) {

        if(sections == null || sections.isEmpty())
            return null;
        
        HashSet<String> hs = new HashSet<String>();
        
        for (Section section: sections) {
            hs.add(section.forumName);
        }
        
        ArrayList<String> forumsUnique = new ArrayList<String>();
        forumsUnique.addAll(hs);

        List<String> subList = forumsUnique.subList(0, forumsUnique.size());
        Collections.sort(subList);
//        forumsUnique.clear();
//        forumsUnique.addAll(subList);
        
        return forumsUnique;

    }
    
    public static ArrayList<String> getSectionsListForForum(ArrayList<Section> sections, ArrayList<String> forums, int forumIndex) {
        
        String selectedForumName = forums.get(forumIndex);
        ArrayList<String> resList = new ArrayList<String>();

        for (int i = 0; i < sections.size(); i++) {
            Section sec = sections.get(i);
            if (sec.forumName.equals(selectedForumName)) {
                resList.add(sec.sectionFullName);
            }

        }

        return resList;
    }


    public static String getSectionsAsString(ArrayList<Section> sections) {
                
        String result = "";
        
        result = JSONProcessor.arrayToString(sections);
        
        return result;
    }


    public static ArrayList<Section> getSectionsFromString(String sSections) {

        return JSONProcessor.stringToArray(sSections);
    }
    
}
