package com.mistareader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

public class NewTopic_Activity extends Activity {

    private RelativeLayout RL_Vote;

    Spinner                spinForum;
    Spinner                spinSection;
    EditText               editSubject;
    EditText               editMessage;
    CheckBox               cb_Vote;
    EditText               editVote1;
    EditText               editVote2;
    EditText               editVote3;
    EditText               editVote4;
    EditText               editVote5;

    Forum                  forum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemesManager.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);

        setTitle(R.string.sNewTopic);

        forum = Forum.getInstance();

        RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.dialog_topic, null);

        spinForum = (Spinner) layout.findViewById(R.id.spinForum);
        spinSection = (Spinner) layout.findViewById(R.id.spinSection);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, forum.forums);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinForum.setAdapter(spinnerArrayAdapter);
        spinForum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                ArrayAdapter<String> spinnerSectArrayAdapter = new ArrayAdapter<String>(NewTopic_Activity.this, android.R.layout.simple_spinner_item, Section
                        .getSectionsListForForum(forum.sections, forum.forums, pos));
                spinnerSectArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinSection.setAdapter(spinnerSectArrayAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

        editSubject = ((EditText) layout.findViewById(R.id.editSubject));
        editMessage = ((EditText) layout.findViewById(R.id.editMessage));

        editVote1 = ((EditText) layout.findViewById(R.id.tvVote1));
        editVote2 = ((EditText) layout.findViewById(R.id.tvVote2));
        editVote3 = ((EditText) layout.findViewById(R.id.tvVote3));
        editVote4 = ((EditText) layout.findViewById(R.id.tvVote4));
        editVote5 = ((EditText) layout.findViewById(R.id.tvVote5));

        editSubject.requestFocus();
        editSubject.requestFocusFromTouch();

        RL_Vote = (RelativeLayout) layout.findViewById(R.id.RL_Votes);
        cb_Vote = (CheckBox) layout.findViewById(R.id.cb_Vote);
        Button btnCancel = (Button) layout.findViewById(R.id.dtb_Cancel);
        Button btnOk = (Button) layout.findViewById(R.id.dtb_OK);

        OnCheckedChangeListener cb_listener = new onCheckedChangeListener();
        cb_Vote.setOnCheckedChangeListener(cb_listener);

        OnClickListener btnOk_listener = new onOk();
        OnClickListener btnCancel_listener = new onCancel();
        btnOk.setOnClickListener(btnOk_listener);
        btnCancel.setOnClickListener(btnCancel_listener);

        final ScrollView sv = new ScrollView(this);

        sv.addView(layout);
        setContentView(sv);

    }

    class onOk implements OnClickListener {

        @Override
        public void onClick(View arg0) {

            createNewTopic();

        }
    }

    private void createNewTopic()
    {
        String subject = editSubject.getText().toString().trim();
        String message = editMessage.getText().toString().trim();

        if (subject.isEmpty()) {
            ErrorMessage.Show(R.string.sTopicError, NewTopic_Activity.this);
            return;
        }
        
        if (message.isEmpty()) {
            ErrorMessage.Show(R.string.sMessageError, NewTopic_Activity.this);
            return;
        }
        
        boolean isVoting = cb_Vote.isChecked();
        String select1 = "";
        String select2 = "";

        if (isVoting) {
            select1 = editVote1.getText().toString().trim();
            select2 = editVote2.getText().toString().trim();
            if (select1.isEmpty() || select2.isEmpty()) {
                
                ErrorMessage.Show(R.string.sVoteError, NewTopic_Activity.this);
                return;

            }
        }

        String forumName = spinForum.getSelectedItem().toString();
        String sectionName = spinSection.getSelectedItem().toString();

        int k = 0;
        String sectionIndex = "0";
        for (k = 0; k < forum.sections.size(); k++) {
            Section sect = forum.sections.get(k);
            if (sect.sectionFullName.equals(sectionName)) {
                sectionIndex = sect.sectionId;
                break;
            }
        }

        Intent intent = new Intent();
        intent.putExtra("commandName", Topics_Activity.COMMAND_CREATE_NEW_TOPIC);
        intent.putExtra("forumName", forumName);
        intent.putExtra("sectionIndex", sectionIndex);
        intent.putExtra("subject", subject);
        intent.putExtra("message", message);
        intent.putExtra("isVoting", isVoting);

        if (isVoting) {
            intent.putExtra("select1", select1);
            intent.putExtra("select2", select2);
            intent.putExtra("select3", editVote3.getText().toString().trim());
            intent.putExtra("select4", editVote4.getText().toString().trim());
            intent.putExtra("select5", editVote5.getText().toString().trim());
        }

        setResult(RESULT_OK, intent);

        forceCloseKeyboard();
        finish();      
    }
    
    class onCancel implements OnClickListener {

        @Override
        public void onClick(View arg0) {

            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);

            finish();

        }
    }

    private void forceCloseKeyboard() {

        InputMethodManager inputManager = (InputMethodManager) NewTopic_Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = NewTopic_Activity.this.getCurrentFocus();
        if (view == null)
            return;

        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    class onCheckedChangeListener implements OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (isChecked)
                RL_Vote.setVisibility(View.VISIBLE);
            else
                RL_Vote.setVisibility(View.GONE);

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuItem send = menu.add(R.string.sCreate);
        send.setIcon(ThemesManager.iconSend);
        send.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getTitle().equals(getString(R.string.sCreate))) {
            
            createNewTopic(); 
            return true;

        }
        
        return super.onOptionsItemSelected(item);
        
    }

}
