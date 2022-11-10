package com.hanshin.ncs_imprintsaga;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends Activity {
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = SettingActivity.class.getSimpleName();

    TextView name, mail;
    Button logout, revoke, goHome;

    //구글로그인 회원정보
    static String loginName ="";
    static String loginEmail = "";
    //파이어베이스 선언 변수
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //어플에 등록된 이메일 계정
     ArrayList<String> enrollEmail = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        logout = findViewById(R.id.logout);
        revoke = findViewById(R.id.revoke);
        goHome = findViewById(R.id.goHome);
        name = findViewById(R.id.name);
        mail = findViewById(R.id.mail);

        mAuth = FirebaseAuth.getInstance();

        //로그인한 회원정보를 가져오는 변수
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            //회원정보 이름
            name.setText(signInAccount.getDisplayName());
            //회원정보 이메일
            mail.setText(signInAccount.getEmail());

            //회원정보 이름
            loginName = signInAccount.getDisplayName();
            //회원정보 이메일
            loginEmail = signInAccount.getEmail();
        }

        //계정정보 찾아보기
        db.collection("member").document("account").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                AccountItem account = document.toObject(AccountItem.class);

                enrollEmail = account.getEmail();

                if(account.getEmail().contains(loginEmail)) {
                    //Toast.makeText(SettingActivity.this, "이 계정은 현재 등록되어 있는 상태입니다", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SettingActivity.this, "ImprintSaga 어플을 방문해주셔서 감사합니다!", Toast.LENGTH_SHORT).show();
                    //새로운 계정일 경우, 데이터베이스 테이블을 생성하는 함수를 호출한다.
                    dataEnroll();
                }
            }
        });


        //로그아웃 하기
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show();

                MainActivity.mPlayer.stop();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });
        //탈퇴하기
        revoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //경고창 보여주기
                AlertDialog.Builder alt_bld = new AlertDialog.Builder(SettingActivity.this);
                alt_bld.setMessage("회원탈퇴 하시겠습니까?").setCancelable(false).setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 네 클릭식 탈퇴하기
                        Toast.makeText(getApplicationContext(), "회원탈퇴 성공, 모든 테이타를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                        mAuth.getCurrentUser().delete();

                        //기존에 있던 데이타베이스 테이블 전부 삭제한다.
                        db.collection(loginEmail).document("item").delete();
                        db.collection(loginEmail).document("itemlist").delete();
                        db.collection(loginEmail).document("itemcheck").delete();
                        db.collection(loginEmail).document("medal").delete();
                        for(int i=1; i<10; i++){
                            db.collection(loginEmail).document("stage"+String.valueOf(i)).delete();
                        }
                        //어플에 등록된 계정중에 이메일명을 삭제한다.
                        int arrayWhich =enrollEmail.indexOf(loginEmail);
                        enrollEmail.remove(arrayWhich);
                        Map<String, Object> mail= new HashMap<>();
                        mail.put("email", enrollEmail);
                       db.collection("member").document("account").update(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               //삭제 작업을 모두 완료했으면 로그인 페이지로 이동한다.

                               MainActivity.mPlayer.stop();
                               Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                               startActivity(intent);
                           }
                       });

                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 아니오 클릭. dialog 닫기.

                        dialog.cancel();
                    }
                });
                AlertDialog alert = alt_bld.create();
                alert.setTitle("회원탈퇴");
                alert.setIcon(R.drawable.ic_baseline_warning_24);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(255, 62, 79, 92)));
                alert.show();


            }
        });
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StageActivity.class);
                startActivity(intent);
            }
        });
    }

    //처음으로 어플을 이용하게 되는 경우, 새로운 계정 데이터베이스 테이블 생성
    private void dataEnroll() {
        //아이템 테이블 생성
        Map<String, Object> item = new HashMap<>();
        item.put("atk", "10");
        item.put("dfd", "0");
        item.put("exp", "0");
        item.put("level", "1");
        item.put("point", "1000");
        item.put("skill", "-");
        item.put("stage", "0");
        db.collection(loginEmail).document("item").set(item);
        //아이템 보유 ,체크 테이블  생성
        Map<String, Object> itemC = new HashMap<>();
        itemC.put("item1", "0"); itemC.put("item2", "0"); itemC.put("item3", "0");
        itemC.put("item4", "0"); itemC.put("item5", "0"); itemC.put("item6", "0");
        itemC.put("item7", "0"); itemC.put("item8", "0"); itemC.put("item9", "0");
        db.collection(loginEmail).document("itemlist").set(itemC);
        db.collection(loginEmail).document("itemcheck").set(itemC);
        //메달 테이블 생성
        Map<String, Object> medal= new HashMap<>();
        medal.put("medal1","0"); medal.put("medal2","0"); medal.put("medal3","0");
        medal.put("medal4","0"); medal.put("medal5","0");
        db.collection(loginEmail).document("medal").set(medal);
        //스테이지 테이블 생성
        Map<String, Object> stage= new HashMap<>();
        stage.put("answerRate", "0"); stage.put("correctNum", "0"); stage.put("rank", "0");
        stage.put("result", "LOSE"); stage.put("totalNum", "0");
        for(int i=1; i<10; i++){
            db.collection(loginEmail).document("stage"+String.valueOf(i)).set(stage);
        }
        //계정 테이블 등록
        enrollEmail.add(loginEmail);
        Map<String, Object> mail= new HashMap<>();
        mail.put("email", enrollEmail);
        db.collection("member").document("account").set(mail);

    }
}
