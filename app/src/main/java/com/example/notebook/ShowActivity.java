package com.example.notebook;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ShowActivity extends AppCompatActivity {
    private Button btnpic;
    private ImageView imageView;
    private Button btnSave;
    private Button btnCancel;
    private TextView showTime;
    private EditText showContent;
    private EditText showTitle;
    private EditText showAuthor;
    private Values value;
    DBService myDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        init();
    }

    public void init() {
        myDb = new DBService(this);
        btnCancel = findViewById(R.id.show_cancel);
        btnSave = findViewById(R.id.show_save);
        showTime = findViewById(R.id.show_time);
        showTitle = findViewById(R.id.show_title);
        showAuthor = findViewById(R.id.show_author);
        showContent = findViewById(R.id.show_content);
        btnpic=findViewById(R.id.btn_chopic);
        imageView=findViewById(R.id.btn_img);
        Intent intent = this.getIntent();
        if (intent != null) {
            value = new Values();

            value.setTime(intent.getStringExtra(DBService.TIME));
            value.setTitle(intent.getStringExtra(DBService.TITLE));
            value.setAuthor(intent.getStringExtra(DBService.AUTHOR));
            value.setContent(intent.getStringExtra(DBService.CONTENT));
            value.setId(Integer.valueOf(intent.getStringExtra(DBService.ID)));

            showTime.setText(value.getTime());
            showTitle.setText(value.getTitle());
            showAuthor.setText(value.getAuthor());

            showContent.setText(value.getContent());
            ShowPic(value.getId(),imageView);

        }
           btnpic.setOnClickListener(new View.OnClickListener() {//设置点击事件
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                startActivityForResult(intent, 0x1);
            }
        });
        //按钮点击事件
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myDb.getWritableDatabase();
                ContentValues values = new ContentValues();
                String content = showContent.getText().toString();
                String title = showTitle.getText().toString();
                String author= showAuthor.getText().toString();
                values.put(DBService.TIME, getTime());
                values.put(DBService.TITLE,title);
                values.put(DBService.AUTHOR,author);
                values.put(DBService.CONTENT,content);

                db.update(DBService.TABLE,values,DBService.ID+"=?",new String[]{value.getId().toString()});
                SavePic(value.getId(),imageView);
                Toast.makeText(ShowActivity.this,"修改成功",Toast.LENGTH_LONG).show();
                db.close();
                Intent intent = new Intent(ShowActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = showContent.getText().toString();
                final String title = showTitle.getText().toString();
                final String author = showAuthor.getText().toString();
                new AlertDialog.Builder(ShowActivity.this)
                        .setTitle("提示框")
                        .setMessage("是否保存当前内容?")
                        .setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SQLiteDatabase db = myDb.getWritableDatabase();
                                        ContentValues values = new ContentValues();
                                        values.put(DBService.TIME, getTime());
                                        values.put(DBService.TITLE,title);
                                        values.put(DBService.AUTHOR,author);
                                        values.put(DBService.CONTENT,content);
                                        db.update(DBService.TABLE,values,DBService.ID+"=?",new String[]{value.getId().toString()});
                                        SavePic(value.getId(),imageView);
                                        Toast.makeText(ShowActivity.this,"修改成功",Toast.LENGTH_LONG).show();
                                        db.close();
                                        Intent intent = new Intent(ShowActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                })
                        .setNegativeButton("no",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(ShowActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                }).show();
            }
        });
    }

    //获取当前时间
    private String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());

        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String str = sdf.format(date);
        return str;
    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == 0x1 && resultCode == RESULT_OK) {
            if (data != null) {
                imageView.setImageURI(data.getData());

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    protected void SavePic(int id,ImageView imageView){
        SharedPreferences sharedPreferences = getSharedPreferences("image_file",MODE_PRIVATE);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ((BitmapDrawable)imageView.getDrawable()).getBitmap()
                .compress(Bitmap.CompressFormat.JPEG,50,stream);
        String imageBase64 = new String(Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SaveImage"+id,imageBase64);
        editor.commit();
    }
    protected void ShowPic(int id,ImageView imageView){
        SharedPreferences sharedPreferences = getSharedPreferences("image_file",MODE_PRIVATE);

        String imageBase64 = sharedPreferences.getString("SaveImage"+id,"");
        byte[] base64byte = Base64.decode(imageBase64,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(base64byte,0,base64byte.length);
        imageView.setImageBitmap(bitmap);

    }
    private int lastid( SQLiteDatabase db){
        String sql = "select last_insert_rowid() from " + DBService.TABLE;
        Cursor cursor = db.rawQuery(sql, null);
        int a = -1;
        if(cursor.moveToFirst()){
            a = cursor.getInt(0);
        }
        return a;

    }
}