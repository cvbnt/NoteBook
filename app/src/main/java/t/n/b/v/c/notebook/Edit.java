package t.n.b.v.c.notebook;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Edit extends AppCompatActivity implements View.OnClickListener{
    private TextView mItemDate;
    private EditText mEditTextContent;
    private Button ok;
    private Button cancel;
    private MyDataBaseHelper DBhelper;
    public int enter_state = 0;  //用来区分是新建一个note还是更改原来的note
    public String last_content;  //用来获取edittext内容
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);
        load();
    }

    private void load() {
        mItemDate=(TextView)findViewById(R.id.itemDate);
        mEditTextContent=(EditText)findViewById(R.id.editContent);
        ok=(Button)findViewById(R.id.btn_ok);
        cancel=(Button)findViewById(R.id.btn_cancel);
        DBhelper=new MyDataBaseHelper(this);
        Date date=new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = sdf.format(date);
        mItemDate.setText(dateString);
        Bundle myBundle = this.getIntent().getExtras();
        last_content = myBundle.getString("info");
        enter_state = myBundle.getInt("enter_state");
        mEditTextContent.setText(last_content);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_ok:
                SQLiteDatabase db=DBhelper.getReadableDatabase();
                String content=mEditTextContent.getText().toString();
                if (enter_state == 0) {
                    if (!content.equals("")) {
                        //获取此时时刻时间
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String dateString = sdf.format(date);
                        //向数据库添加信息
                        ContentValues values = new ContentValues();
                        values.put("content", content);
                        values.put("date", dateString);
                        db.insert("note", null, values);
                        finish();
                    } else {
                        Toast.makeText(Edit.this, "请输入你的内容！", Toast.LENGTH_SHORT).show();
                    }
                }
                // 查看并修改一个已有的日志
                else {
                    ContentValues values = new ContentValues();
                    values.put("content", content);
                    db.update("note", values, "content = ?", new String[]{last_content});
                    finish();
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }
}
