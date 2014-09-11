package com.mistareader.NavigationDrawer;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.res.Resources;
import android.widget.ArrayAdapter;

import com.mistareader.API;
import com.mistareader.Forum;
import com.mistareader.R;
import com.mistareader.R.string;
import com.mistareader.Section;

public class DropDownNav {

    public ArrayList<String> shortNamesList;

    public void reBuildSubmenu(Activity mainActivity, String selectedForumName, int selectedSectionPosition) {

        final Resources locRes = mainActivity.getResources();
        final ActionBar actionBar = mainActivity.getActionBar();

        if (selectedForumName.isEmpty()) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(R.string.sAllSections);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            return;
        }

        switch (selectedForumName) {
            case API.MYTOPICS:
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                actionBar.setTitle(R.string.sMyTopics);
                return;
            case API.TOPICS_WITH_ME:
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                actionBar.setTitle(R.string.sMyTopics2);
                return;
            default:
                break;
        }

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        ArrayList<String> fullNamesList = new ArrayList<String>();
        shortNamesList = new ArrayList<String>();

        fullNamesList.add(selectedForumName + " (" + locRes.getText(string.sNavDrawerAll) + ")");
        shortNamesList.add(selectedForumName);

        Forum forum = Forum.getInstance();

        for (int i = 0; i < forum.sections.size(); i++) {

            Section sec = forum.sections.get(i);
            if (sec.forumName.equals(selectedForumName)) {
                fullNamesList.add(sec.sectionFullName);
                shortNamesList.add(sec.sectionShortName);
            }

        }

        ArrayAdapter<String> aAdpt = new ArrayAdapter<String>(mainActivity, R.layout.actionbar_dropdowntext, android.R.id.text1, fullNamesList);
        aAdpt.setDropDownViewResource(android.R.layout.simple_list_item_1);

        actionBar.setListNavigationCallbacks(aAdpt, (OnNavigationListener) mainActivity);

        actionBar.setSelectedNavigationItem(selectedSectionPosition);

    }
    

}
