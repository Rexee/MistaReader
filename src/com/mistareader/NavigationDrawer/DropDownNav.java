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
import com.mistareader.Section;
import com.mistareader.R.string;

public class DropDownNav {

    public ArrayList<String> shortSectionsNamesList;

    
    public void reBuildSubmenu(Activity mainActivity, String selectedForumName, int selectedSectionPosition) {

        final ActionBar actionBar = mainActivity.getActionBar();

        if (selectedForumName.isEmpty()) {
            setDefaultActionBarMode(actionBar);
            actionBar.setTitle(R.string.sAllSections);
            return;
        }
        else if (selectedForumName.equals(NavDrawer_Main.MENU_SUBSCRIPTIONS)) {
            setDefaultActionBarMode(actionBar);
            actionBar.setTitle(R.string.sSubscriptions);
            return;
        }

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        ArrayList<String> dropdownItems = new ArrayList<String>();

        if (selectedForumName.equals(API.TOPICS_WITH_ME)) {

            dropdownItems.add(mainActivity.getString(R.string.sMyTopics2));
            dropdownItems.add(mainActivity.getString(R.string.sMyTopics));

        }
        else
        {
            final Resources locRes = mainActivity.getResources();

            shortSectionsNamesList = new ArrayList<String>();

            dropdownItems.add(selectedForumName + " (" + locRes.getText(string.sNavDrawerAll) + ")");
            shortSectionsNamesList.add(selectedForumName);

            Forum forum = Forum.getInstance();

            for (int i = 0; i < forum.sections.size(); i++) {

                Section sec = forum.sections.get(i);
                if (sec.forumName.equals(selectedForumName)) {
                    dropdownItems.add(sec.sectionFullName);
                    shortSectionsNamesList.add(sec.sectionShortName);
                }

            }
        }

        ArrayAdapter<String> aAdpt = new ArrayAdapter<String>(mainActivity, R.layout.actionbar_dropdowntext, android.R.id.text1, dropdownItems);
        aAdpt.setDropDownViewResource(android.R.layout.simple_list_item_1);

        actionBar.setListNavigationCallbacks(aAdpt, (OnNavigationListener) mainActivity);

        actionBar.setSelectedNavigationItem(selectedSectionPosition);

    }


    private void setDefaultActionBarMode(final ActionBar actionBar) {
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

}
