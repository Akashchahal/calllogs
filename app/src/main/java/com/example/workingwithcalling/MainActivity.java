package com.example.workingwithcalling;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CallLogAdapter.OnItemLongClickListener {

    private static final int REQUEST_CODE_READ_CALL_LOG = 1;
    private static final int REQUEST_CODE_READ_WRITE_CALL_LOG = 1;

    private RecyclerView recyclerView;
    private CallLogAdapter adapter;
    private List<CallLogEntry> callLogEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        callLogEntries = new ArrayList<>();
        adapter = new CallLogAdapter(callLogEntries, this);
        recyclerView.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG}, REQUEST_CODE_READ_WRITE_CALL_LOG);
        } else {
            readCallLogs();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_WRITE_CALL_LOG) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                readCallLogs();
            } else {
                // Permission denied, show a message to the user
            }
        }
    }

    private void readCallLogs() {
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");

        if (cursor != null) {
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);

            while (cursor.moveToNext()) {
                String number = cursor.getString(numberIndex);
                int type = cursor.getInt(typeIndex);
                long date = cursor.getLong(dateIndex);
                long duration = cursor.getLong(durationIndex);

                String typeString = "";
                switch (type) {
                    case CallLog.Calls.INCOMING_TYPE:
                        typeString = "Incoming";
                        break;
                    case CallLog.Calls.OUTGOING_TYPE:
                        typeString = "Outgoing";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        typeString = "Missed";
                        break;
                    case CallLog.Calls.VOICEMAIL_TYPE:
                        typeString = "Voicemail";
                        break;
                    case CallLog.Calls.REJECTED_TYPE:
                        typeString = "Rejected";
                        break;
                    case CallLog.Calls.BLOCKED_TYPE:
                        typeString = "Blocked";
                        break;
                    case CallLog.Calls.ANSWERED_EXTERNALLY_TYPE:
                        typeString = "Answered Externally";
                        break;
                    default:
                        typeString = "Unknown";
                        break;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                String dateString = sdf.format(new Date(date));
                String durationString = duration + " seconds";

                CallLogEntry entry = new CallLogEntry(number, typeString, dateString, durationString);
                callLogEntries.add(entry);
            }
            cursor.close();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemLongClick(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Call Log")
                .setMessage("Do you want to delete this call log?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCallLog(position);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteCallLog(int position) {
        CallLogEntry entry = callLogEntries.get(position);
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                long date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                String dateString = sdf.format(new Date(date));

                if (entry.getNumber().equals(number) && entry.getDate().equals(dateString)) {
                    long id = cursor.getLong(cursor.getColumnIndex(CallLog.Calls._ID));
                    getContentResolver().delete(Uri.withAppendedPath(CallLog.Calls.CONTENT_URI, String.valueOf(id)), null, null);
                    callLogEntries.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(MainActivity.this, "Call log deleted", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            cursor.close();
        }
    }

}
