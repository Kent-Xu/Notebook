package com.example.notebook;


import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class EditActivity extends AppCompatActivity {

    DBService myDb;
    private Button btnCancel;
    private Button btnSave;
    private EditText titleEditText;
    private EditText authorEditText;
    private EditText contentEditText;
    private TextView timeTextView;
    private ImageView imageView;
    private Button btnpic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_editor);

        init();
        if(timeTextView.getText().length()==0)
            timeTextView.setText(getTime());
    }

    private void init() {
        myDb = new DBService(this);
        SQLiteDatabase db = myDb.getReadableDatabase();
        titleEditText = findViewById(R.id.et_title);
        authorEditText = findViewById(R.id.et_author);
        contentEditText = findViewById(R.id.et_content);
        timeTextView = findViewById(R.id.edit_time);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        btnpic=findViewById(R.id.btn_chopic);
        imageView=findViewById(R.id.img);
        Intent intent=getIntent();
        System.out.println(intent.getStringExtra("username")+"!!");
        authorEditText.setText(intent.getStringExtra("username"));


        btnpic.setOnClickListener(new View.OnClickListener() {//设置点击事件
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                startActivityForResult(intent, 0x1);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQLiteDatabase db = myDb.getWritableDatabase();
                ContentValues values = new ContentValues();

                String title= titleEditText.getText().toString();
                String author= authorEditText.getText().toString();
                String content=contentEditText.getText().toString();
                String time= timeTextView.getText().toString();

                if("".equals(titleEditText.getText().toString())){
                    Toast.makeText(EditActivity.this,"标题不能为空",Toast.LENGTH_LONG).show();
                    return;
                }

                if("".equals(contentEditText.getText().toString())) {
                    Toast.makeText(EditActivity.this,"内容不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                values.put(DBService.TITLE,title);
                values.put(DBService.AUTHOR,author);
                values.put(DBService.CONTENT,content);
                values.put(DBService.TIME,time);
                db.insert(DBService.TABLE,null,values);
                SavePic(lastid(db),imageView);
                Toast.makeText(EditActivity.this,"保存成功",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EditActivity.this,MainActivity.class);
                startActivity(intent);
                db.close();
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
    Bitmap bitmap=BitmapFactory.decodeByteArray(base64byte,0,base64byte.length);
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

