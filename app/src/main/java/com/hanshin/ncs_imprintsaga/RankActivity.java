package com.hanshin.ncs_imprintsaga;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RankActivity extends Activity {
    //구글로그인 회원정보
    static String loginName ="";
    static String loginEmail = "";
    //파이어베이스 선언 변수
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ListView rankListview;
    RankAdapter adapter;

    ArrayList<String> enrollEmail = new ArrayList<String>();
    ArrayList<String> enrolllevel = new ArrayList<String>();
    ArrayList<String> enrollPoint = new ArrayList<String>();
    ArrayList<String> enrollStage = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank);

        rankListview = findViewById(R.id.rankListview);

        //실행할때 데이타 초기화.
        enrollEmail.clear();
        enrolllevel.clear();
        enrollPoint.clear();
        enrollStage.clear();

        //로그인한 회원정보를 가져오는 변수
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
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
                //수집된 이메일을 바탕으로 데이타 조사하기
                emailSearch();
            }
        });


    }

    private void emailSearch() {
        //어플에 등록된 계정에 기록을 찾아보기
        enrolllevel.clear();
        enrollPoint.clear();
        enrollStage.clear();

        final BackgroundThreads thread = new BackgroundThreads();
        for(int i=0; i<enrollEmail.size(); i++){
            db.collection(enrollEmail.get(i)).document("item").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    MyPage_Item item = document.toObject(MyPage_Item.class);
                    String level = item.getLevel();
                    String point = item.getPoint();
                    String stage = item.getStage();


                    enrolllevel.add(level);
                    enrollPoint.add(point);
                    enrollStage.add(stage);

                    //어댑터에 데이타 추가하기
                    addAdapter();

                }
            });
            thread.run();
        }
    }

    private void addAdapter() {

        adapter = new RankAdapter(this, enrollEmail, enrolllevel, enrollPoint, enrollStage);
        rankListview.setAdapter(adapter);


    }
    //대기 시간을 할 수 있도록 하는 클래스
    class BackgroundThreads extends  Thread{
        public  void run(){
            try{
                Thread.sleep(300);
            }catch (Exception e){
            }
        }
    }

}
