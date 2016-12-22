package com.itgao.bookshelf.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import com.itgao.bookshelf.R;
import com.itgao.bookshelf.adapter.NovelAdapter;
import com.itgao.bookshelf.db.NovelDB;
import com.itgao.bookshelf.model.Novel;

import java.util.List;

public class MainActivity extends Activity {

    private ImageButton create_frag_search;
    private ListView listView;
    private List<Novel> list;
    private NovelAdapter adapter;
    private NovelDB novelDB ;
    private static final int Request_Code = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        novelDB = NovelDB.getInstance(this);
        init_view();
        listen();
    }

    private void init_view(){
        create_frag_search = (ImageButton) findViewById(R.id.create_frag_search);
        listView = (ListView) findViewById(R.id.novel_list);
        list = novelDB.loadAllNovels();
        adapter = new NovelAdapter(this,R.layout.novel_list_item,list);
        listView.setAdapter(adapter);


    }
    public void listen(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Novel novel = list.get(i);
                /**
                 Intent intent = new Intent(MainActivity.this,);
                 intent.putExtra("novel",novel);
                 startActivityForResult(intent,0);
                 */
            }
        });
        create_frag_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivityForResult(intent,Request_Code);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0){
            List<Novel> ll = novelDB.loadAllNovels();
            list.clear();
            list.addAll(ll);
            adapter.notifyDataSetChanged();
        }
    }
}
