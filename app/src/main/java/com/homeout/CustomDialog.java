package com.homeout;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.homeout.SQL.SQLiteHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class CustomDialog extends AppCompatActivity {

    private Context context;
    public String strDate, strContext;
    public Calendar calendar;
    public outApplication outApplication;

    SQLiteHelper sqLiteHelper;

    public CustomDialog(Context context) {
        this.context =context;
    }

    public void runDialog(final ArrayList<Memo> arrayList){

        outApplication = (outApplication) getApplication();
        final Dialog dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.custom_dialog);
        dlg.show();

        sqLiteHelper = new SQLiteHelper(context);

        final DatePicker datePicker = (DatePicker) dlg.findViewById(R.id.datepicker);
        final EditText etMemo = (EditText) dlg.findViewById(R.id.etContext);
        final Button okButton = (Button) dlg.findViewById(R.id.btnOk);
        final Button cancelButton = (Button) dlg.findViewById(R.id.btnCan);

        calendar = Calendar.getInstance();
        int year = calendar.get(calendar.YEAR);
        int month = calendar.get(calendar.MONTH); // 1월은 0부터
        int dayOfMonth = calendar.get(calendar.DAY_OF_MONTH);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "작성이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                strDate = Integer.toString(datePicker.getYear()) + "/" +  Integer.toString(datePicker.getMonth())+ "/" +  Integer.toString(datePicker.getDayOfMonth());
                strContext = etMemo.getText().toString();
                Memo memo = new Memo(strContext,strDate,"미완");
                arrayList.add(memo);
                sqLiteHelper.insertMemo(memo);
                dlg.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "작성이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                dlg.dismiss();
            }
        });

    }
}
