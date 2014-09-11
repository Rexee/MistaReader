package com.mistareader;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class Messages_Activity extends BaseActivity implements Forum.iOnPOSTRequestExecuted, Messages_Fragment.OnContextMenuListener {

    private Messages_Fragment messages_Fragment;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        Forum forum = Forum.getInstance();
        if (forum.sessionID == null || forum.sessionID.isEmpty()) {
            MenuItem mi = menu.findItem(R.id.menu_add);
            mi.setVisible(false);
        }

        return true;
    }

    @Override
     public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        
        if (v.getId()==R.id.lvMain) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.messages_dropdown, menu);
            menu.setHeaderTitle(R.string.sPopupMenyHeaderMessage);
        }
    }  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            messages_Fragment = new Messages_Fragment();

            Bundle args = getIntent().getExtras();

            String title = args.getString("section");
            if (title == null || title.isEmpty()) {
                String topicForum = args.getString("forum");
                if (topicForum == null || topicForum.isEmpty())
                    title = getString(R.string.sAllSections);
                else
                    title = topicForum.toUpperCase();
            }
            setTitle(title);

            messages_Fragment.setArguments(args);

            getFragmentManager().beginTransaction().add(android.R.id.content, messages_Fragment).commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_reload) {
            if (messages_Fragment != null) {
                messages_Fragment.reLoad();
            }
            return true;
        }
        else if (id == R.id.menu_add) {
            if (messages_Fragment != null) {

                long curTopicId = messages_Fragment.getCurrentTopicId();

                Forum forum = Forum.getInstance();
                forum.addNewMessage(curTopicId, Messages_Activity.this);
            }
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onPOSTRequestExecuted(String result) {

        messages_Fragment.reLoad();

    }

    public void onContextMenySelected(Message selectedMessage, long currentTopicId) {
       
        Forum forum = Forum.getInstance();
        forum.addNewMessage(currentTopicId, selectedMessage.n, Messages_Activity.this);
        
    }

}
