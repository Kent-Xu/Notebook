package com.example.notebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity{

    private SharedPreferences pref;//定义一个SharedPreferences对象
    private SharedPreferences.Editor editor;//调用SharedPreferences对象的edit()方法来获取一个SharedPreferences.Editor对象，用以添加要保存的数据
    private Button login;//登录按钮
    private Button register;//登录按钮
    private EditText adminEdit;//用户名输入框
    private EditText passwordEdit;//密码输入框

    private CheckBox showPassword;//显示或隐藏密码复选框
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将背景图与状态栏融合到一起，只支持Android5.0以上的版本
        if(Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            //布局充满整个屏幕
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //设置状态栏为透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_login);
        //获取各组件或对象的实例
        pref= getSharedPreferences("data",MODE_PRIVATE);
        login=findViewById(R.id.login_button);
        adminEdit=findViewById(R.id.admin);
        adminEdit.setText("admin");
        passwordEdit=findViewById(R.id.password);
        passwordEdit.setText("123456");
        register=findViewById(R.id.register_button);
        showPassword=findViewById(R.id.show_password);
        //获取当前“是否保存密码”的状态

        //用户点击登录时的处理事件
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String account=adminEdit.getText().toString();
                String password=passwordEdit.getText().toString();

                if (password.equals(pref.getString("password"+account,null))&&account.equals(pref.getString("username"+account,""))){

                    Intent intent = new Intent();
                    intent.putExtra("username", account);
                    intent.setClass(Login.this, MainActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "用户名或者密码错误", Toast.LENGTH_SHORT).show();
                }


            }
        });

        //用户点击注册时的处理事件
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //读出用户名和密码并判断是否正确
                String account=adminEdit.getText().toString();
                String password=passwordEdit.getText().toString();
               if(account.equals("")||password.equals("")){
                   Toast.makeText(Login.this, "用户名或者密码未填写", Toast.LENGTH_SHORT).show();
               } else if (pref.getString("username"+account, "").equals("")) {
                   SharedPreferences.Editor editor = pref.edit();
                   editor.putString("username"+account, account);
                   editor.putString("password"+account, password);
                   editor.commit();
                   Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
               }else {
                   Toast.makeText(getApplicationContext(), "用户名已经存在", Toast.LENGTH_SHORT).show();
               }

            }
        });
        //用户点击'显示密码'复选框
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showPassword.isChecked()){
                    showOrHide(passwordEdit,true);
                }else{
                    showOrHide(passwordEdit,false);
                }
            }
        });

    }


    //显示或隐藏密码
    private void showOrHide(EditText passwordEdit,boolean isShow){

        //记住光标开始的位置
        int pos = passwordEdit.getSelectionStart();
        if(isShow){
            passwordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }else{
            passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        passwordEdit.setSelection(pos);
    }

}