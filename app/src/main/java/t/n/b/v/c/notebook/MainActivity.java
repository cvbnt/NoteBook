package t.n.b.v.c.notebook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private ListView mListView;
    private SimpleAdapter simple_adapter;      //可显示图片和文字的适配器，不能后期加工的适配器，单纯用来显示，必须用Map类
    private List<Map<String, Object>> dataList;//用于收集获取到的本地数据库的数据
    private Button addNoteButton;
    private TextView mItemContent;
    private MyDataBaseHelper DbHelper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        find();
        load();
    }

    private void find() {                //绑定ID全写在一个类里
        mListView=(ListView)findViewById(R.id.listview);
        addNoteButton=(Button)findViewById(R.id.addNote);
        mItemContent=(TextView)findViewById(R.id.itemContent);
    }

    @Override
    protected void onStart() {            //activity显示时更新ListView
        super.onStart();
        refresh();
    }

    private void load() {
        dataList=new ArrayList<Map<String, Object>>();
        DbHelper =new MyDataBaseHelper(this);
        db=DbHelper.getReadableDatabase();          //数据库基本操作，读取
        mListView.setOnItemClickListener(this);     //ListView条目短按
        mListView.setOnItemLongClickListener(this); //ListView条目长按
        addNoteButton.setOnClickListener(new View.OnClickListener() {  //跳转到Edit活动
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Edit.class);
                Bundle bundle=new Bundle();
                bundle.putString("info", "");
                bundle.putInt("enter_state", 0);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    public void refresh() {
        int size=dataList.size();                //每refresh一次清空datalist
        if (size>0){
            dataList.removeAll(dataList);
            simple_adapter.notifyDataSetChanged();
        }
        Cursor cursor = db.query("note", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            Map<String, Object> map = new HashMap<String, Object>();    //将查询到的内容和时间添加到map中
            map.put("itemContent", content);     //其中参数1为键，参数2为值
            map.put("itemDate", date);
            dataList.add(map);
        }
        simple_adapter=new SimpleAdapter(this,dataList,R.layout.item,new String[]{"itemContent","itemDate"},new int[]{R.id.itemContent,R.id.itemDate});
        //适配器参数1上下文，参数2条目的布局，参数3为键，参数4为键值对应条目上的组件ID，
        mListView.setAdapter(simple_adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //条目短按事件
        String content=mListView.getItemAtPosition(position)+"";
        //mListView.getItemAtPosition(position)产生出来的是map对象不是string对象，不能强制转换成string
        //加了“”调用了 Map的toString方法。Map的ToString方法其实是Map自己重写的，返回key-value的字符串。
        String content1 = content.substring(content.indexOf("=") + 1, content.indexOf(","));
        //indexOf()查找某个字符出现的位置，第一个字符位置为0，subString为截取两部分中间的字符
        //例如content值为{itemContent=你好, itemDate=2018-01-03 11:28},content1值为你好
        Intent myIntent=new Intent(MainActivity.this,Edit.class);
        Bundle bundle=new Bundle();
        bundle.putString("info",content1);
        bundle.putInt("enter_state", 1);
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this); //弹出对话框
        builder.setTitle("删除该日志")
                .setMessage("确认删除吗？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = mListView.getItemAtPosition(position) + "";
                        String content1 = content.substring(content.indexOf("=") + 1,
                                content.indexOf(","));
                        db.delete("note", "content = ?", new String[]{content1});
                        //数据库内找到该列的,删除
                        refresh();
                    }
                })
        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
                .create()
                .show();
        return true;
    }

}
