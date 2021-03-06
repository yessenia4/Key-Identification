package com.rodriguez.foundmatch.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rodriguez.foundmatch.DatabaseManagment.MyDBHandler;
import com.rodriguez.foundmatch.Keys.KeyInformation;
import com.rodriguez.foundmatch.R;

import java.util.ArrayList;

public class MyKeysActivity extends AppCompatActivity implements View.OnClickListener{
    ArrayList<KeyInformation> keylist;
    ListView lv;
    private static KeyListAdapter adapter;
    MyDBHandler currentDB;
    Button deleteBtn;
    Button updateBtn;

    private ArrayList<KeyInformation> GetlistKey(){
        currentDB = MyDBHandler.getDbInstance(getApplicationContext());
        keylist = currentDB.loadHandler();

        return keylist;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keys);
        lv = (ListView) findViewById(R.id.key_viewer);
        keylist = GetlistKey();
        adapter = new KeyListAdapter(keylist, getApplicationContext());

        lv.setAdapter(adapter);

        deleteBtn = (Button) findViewById(R.id.delete_button);
        updateBtn = (Button) findViewById(R.id.update_button);

        if(keylist.isEmpty()){
            TextView nothing = (TextView) findViewById(R.id.empty_db);
            nothing.setVisibility(View.VISIBLE);

            deleteBtn.setVisibility(View.GONE);
            updateBtn.setVisibility(View.GONE);
        }

        deleteBtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.delete_button:
                deleteBtn.setVisibility(View.GONE);
                updateBtn.setVisibility(View.GONE);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> a, View v, final int position, long id) {
                        //Toast.makeText(getApplicationContext(),"Item Selected: " + position, Toast.LENGTH_SHORT).show();
                        createDeleteKeyDialog(position).show();
                    }
                });

                break;

            case R.id.update_button:
                deleteBtn.setVisibility(View.GONE);
                updateBtn.setVisibility(View.GONE);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> a, View v, final int position, long id) {
                        //Toast.makeText(getApplicationContext(),"Item Selected: " + position, Toast.LENGTH_SHORT).show();
                        createEditKeyDialog(position).show();
                    }
                });
                break;
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        //begin again
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private Dialog createDeleteKeyDialog(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MyKeysActivity.this);

        //adb=new AlertDialog.Builder(MyKeysActivity.this);
        builder.setTitle("Delete?");
        builder.setMessage("Are you sure you want to delete this key?");
        //final int positionToRemove = position;
        builder.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteBtn.setVisibility(View.VISIBLE);
                updateBtn.setVisibility(View.VISIBLE);
            }});
        builder.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Delete and Check if change was made
                boolean deleted = currentDB.deleteHandler(keylist.get(position).getKeyID());
                if (deleted) {
                    Toast.makeText(MyKeysActivity.this, "DELETED", Toast.LENGTH_LONG).show();

                    //refresh view
                    Intent intent = new Intent(getApplicationContext(), MyKeysActivity.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(MyKeysActivity.this, "ERROR: COULD NOT DELETE", Toast.LENGTH_LONG).show();
            }});

        return builder.create();
    }

    private Dialog createEditKeyDialog(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MyKeysActivity.this);

        // Inflate using dialog themed context.
        final Context context = builder.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.edit_key, null, false);

        // Find widgets inside "view".
        final EditText updateName = (EditText) view.findViewById(R.id.edit_name);
        final EditText updateDesc = (EditText) view.findViewById(R.id.edit_description);

        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    dialog.cancel();
                    return;
                }

                String n_text = (String) updateName.getText().toString();
                String d_text = (String) updateDesc.getText().toString();

                KeyInformation editKey = keylist.get(position);
                if(n_text != null)
                    editKey.setKeyName(n_text);
                if(d_text != null)
                    editKey.setKeyDescription(d_text);

                //Edit and Check if change was made
                boolean isEdited = currentDB.updateHandler(editKey.getKeyID(), editKey);
                if(isEdited) {
                    Toast.makeText(MyKeysActivity.this, "CHANGED", Toast.LENGTH_LONG).show();

                    //refresh view
                    Intent intent = new Intent(getApplicationContext(), MyKeysActivity.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(MyKeysActivity.this, "ERROR: COULD NOT CHANGE", Toast.LENGTH_LONG).show();
            }
        };

        builder
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listener);
        return builder.create();
    }
}