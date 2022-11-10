package com.hanshin.ncs_imprintsaga;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPageActivity extends AppCompatActivity {
    //보유 아이템
    ArrayList<String>  haveItem= new ArrayList<String>();
    //아이템 장착 여부
    ArrayList<String> checkItem = new ArrayList<String>();


    ImageView mypage_charIv;
    ImageButton mypage_closeBtn;
    TextView mypage_levelTv;
    TextView mypage_expTv;
    TextView mypage_pointTv;
    TextView mypage_stageTv;
    TextView mypage_hpTv;
    TextView mypage_atkTv;
    TextView mypage_dfdTv;
    TextView mypage_skillTv ;
    GridView mypage_gridview;
    ImageButton medal1, medal2, medal3, medal4, medal5;
    ProgressBar mypage_achievementPb, mypage_answerratePb;

    MypageViewAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MyPage_Item item;
    Mypage_checkitem check;
    Medal medalInfo;
    //현재 갖고있는 아이템을 보여줌


    //구글로그인 회원정보
    String loginName ="";
    String loginEmail = "";
    //스테이지 위치
    String stageInfo="0";
    //각 스테이지 정답률
    ArrayList<Integer>  answerRate = new ArrayList<Integer>();
    //정답률 계산하기 위해 사용되는 변수이다.
    int sum=0;
    int a=0;
    int k;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);


        //위젯 연결
        mypage_charIv = findViewById(R.id.mypage_charIv);  // 캐릭터 이미지
        mypage_closeBtn = findViewById(R.id.mypage_closeBtn); // 창닫기 버튼
        //데이터 위젯
        mypage_levelTv = findViewById(R.id.mypage_levelTv); // 레벨
        mypage_expTv = findViewById(R.id.mypage_expTv);
        mypage_pointTv = findViewById(R.id.mypage_pointTv); // 포인트
        mypage_stageTv = findViewById(R.id.mypage_stageTv); // 스테이지
        mypage_atkTv = findViewById(R.id.mypage_atkTv); //공격력
        mypage_dfdTv = findViewById(R.id.mypage_dfdTv); //방어력
        mypage_skillTv = findViewById(R.id.mypage_skillTv); //능력

        mypage_gridview = findViewById(R.id.mypage_gridview); //그리드뷰
        medal1 = findViewById(R.id.medal1); medal2 = findViewById(R.id.medal2); //메달
        medal3 = findViewById(R.id.medal3); medal4 = findViewById(R.id.medal4);
        medal5 = findViewById(R.id.medal5);
        mypage_achievementPb = findViewById(R.id.mypage_achievementPb); //달성도
        mypage_answerratePb = findViewById(R.id.mypage_answerratePb);//정답률

        //그리드뷰 이미지 타이틀
        String[] shopListTitle=  {
                "cap", "newspaper", "sneakers",
                "coffee ", "book", "magnifier",
                "hambuger", "phone", "sunglass"
        };
        //그리드뷰 이미지 저장위치
        final Integer[] shopListImage ={
                R.drawable.item1, R.drawable.item2, R.drawable.item3,
                R.drawable.item4, R.drawable.item5, R.drawable.item6,
                R.drawable.item7, R.drawable.item8, R.drawable.item9
        };

        //그리드뷰 이미지 가격
        Integer[] shopListPrice ={
                200,  300,  500,
                800, 1000, 1500,
                1500, 2000, 3000
        };


        //그리드뷰 대화상자 아이템능력
        final String[] shopListAbility = {
                //기본 HP = 100, 기본 공격력 = 10, 기본 방어력 = 0, 능력 = x
                "방어 10 증가", "공격 10 증가 ", "힌트 1회 제공",
                "방어 20 증가", "공격 20 증가", "힌트 2회 제공",
                "방어 30 증가", "공격 30 증가",  "힌트 3회 제공"
        };




        //로그인한 회원정보를 가져오는 변수
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount != null){
            //회원정보 이름
            loginName = signInAccount.getDisplayName();
            //회원정보 이메일
            loginEmail = signInAccount.getEmail();
            //Toast.makeText(MyPageActivity.this, loginName+" "+loginEmail, Toast.LENGTH_SHORT).show();
        }


        //개인페이지 그리드뷰 및 어댑터 설정
          adapter = new MypageViewAdapter(this, haveItem);

        //현재 갖고 있는 아이템 리스트 정보 가져오기.
        db.collection(loginEmail).document("itemlist"). get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Mypage_HavingItem havingItem = document.toObject(Mypage_HavingItem.class);

                //현재 갖고 있는 아이템의 유무를 리스트에 등록시킨다.
                haveItem.add(havingItem.getItem1());
                haveItem.add(havingItem.getItem2());
                haveItem.add(havingItem.getItem3());
                haveItem.add(havingItem.getItem4());
                haveItem.add(havingItem.getItem5());
                haveItem.add(havingItem.getItem6());
                haveItem.add(havingItem.getItem7());
                haveItem.add(havingItem.getItem8());
                haveItem.add(havingItem.getItem9());
                adapter.notifyDataSetChanged();
            }
        });
        //현재 장착한 아이템 리스트 정보 가져오기.
        db.collection(loginEmail).document("itemcheck").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Mypage_checkitem check = document.toObject(Mypage_checkitem.class);

                //현재 갖고 있는 아이템의 체크 유무를 리스트에 등록시킨다.
                checkItem.add(check.getItem1());
                checkItem.add(check.getItem2());
                checkItem.add(check.getItem3());
                checkItem.add(check.getItem4());
                checkItem.add(check.getItem5());
                checkItem.add(check.getItem6());
                checkItem.add(check.getItem7());
                checkItem.add(check.getItem8());
                checkItem.add(check.getItem9());

                String mountItem = checkItem.get(0)+checkItem.get(1)+checkItem.get(2);
                changeImage(mountItem);
            }
        });

        mypage_gridview.setAdapter(( MypageViewAdapter) adapter);

        //그리드뷰에서 아이템을 클릭할 때, 이벤트 작성
        mypage_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final View  dialogView =(LinearLayout)  View.inflate(com.hanshin.ncs_imprintsaga.MyPageActivity.this,  R.layout.mypage_dialog_list, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(com.hanshin.ncs_imprintsaga.MyPageActivity.this);
                final int pos = position;
                ImageView image =dialogView.findViewById(R.id.mypage_dialogImage);
                image.setImageResource(shopListImage[position]);
                TextView text1 = dialogView.findViewById(R.id.mypage_dialogAbilty);
                text1.setText(shopListAbility[position]);
                TextView text2 = dialogView.findViewById(R.id.mypage_dialogText);

                //4번아이템부터 9번아이템 흑백으로 표시
                if(position ==0 || position==1 || position==2){
                }else{
                    ColorMatrix matrix = new ColorMatrix();
                    matrix.setSaturation(0);

                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                    image.setColorFilter(filter);
                }

                if(haveItem.get(position).equals("0")){
                    text2.setText("아이템 보유 x");
                }else{
                    text2.setText("아이템 보유 O");
                }

                dlg.setTitle("아이템을 장착하시겠습니까?");
                dlg.setView(dialogView);
                dlg.setPositiveButton("장착", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //아이템이 없을 경우 실행
                        if(haveItem.get(pos).equals("0")){
                            Toast.makeText(getApplicationContext(),"아이템을 보유하지 않아 장착할 수 없습니다.",Toast.LENGTH_SHORT).show();
                        }else{
                           switch (pos){
                               case 0:
                                   if(checkItem.get(position).equals("0") && checkItem.get(position+3).equals("0")  && checkItem.get(position+6).equals("0")  ){
                                       item.setDfd(String.valueOf(Integer.parseInt(item.getDfd())+10));
                                       checkItem.set(position, "1");
                                       mypage_dfdTv.setText(item.getDfd());
                                       Map<String, Object> data = new HashMap<>();
                                       data.put("dfd" , item.getDfd());
                                       db.collection(loginEmail).document("item").update(data);
                                       Map<String, Object> data2 = new HashMap<>();
                                       data2.put("item1", checkItem.get(position));
                                       //체크리스트 업데이트
                                       db.collection(loginEmail).document("itemcheck").update(data2);
                                       //이미지 업데이트
                                       checkItem.set(position,"1");
                                       changeImage(checkItem.get(0)+checkItem.get(1)+checkItem.get(2));

                                    }
                                   break;
                               case 1:
                                   if(checkItem.get(position).equals("0") && checkItem.get(position+3).equals("0")  && checkItem.get(position+6).equals("0") ){
                                       item.setAtk(String.valueOf(Integer.parseInt(item.getAtk())+10));
                                       checkItem.set(position, "1");
                                       mypage_atkTv.setText(item.getAtk());
                                       Map<String, Object> data = new HashMap<>();
                                       data.put("atk" , item.getAtk());
                                       db.collection(loginEmail).document("item").update(data);
                                       Map<String, Object> data2 = new HashMap<>();
                                       data2.put("item2", checkItem.get(position));
                                       db.collection(loginEmail).document("itemcheck").update(data2);
                                       //이미지 업데이트
                                       checkItem.set(position,"1");
                                       changeImage(checkItem.get(0)+checkItem.get(1)+checkItem.get(2));
                                   }
                                   break;
                               case 2:
                                   if(checkItem.get(position).equals("0")&& checkItem.get(position+3).equals("0")  && checkItem.get(position+6).equals("0") ){
                                       item.setSkill(shopListAbility[pos]);
                                       checkItem.set(position, "1");
                                       mypage_skillTv.setText(item.getSkill());
                                       Map<String, Object> data = new HashMap<>();
                                       data.put("skill" , item.getSkill());
                                       db.collection(loginEmail).document("item").update(data);
                                       Map<String, Object> data2 = new HashMap<>();
                                       data2.put("item3", checkItem.get(position));
                                       db.collection(loginEmail).document("itemcheck").update(data2);
                                       //이미지 업데이트
                                       checkItem.set(position,"1");
                                       changeImage(checkItem.get(0)+checkItem.get(1)+checkItem.get(2));
                                   }
                                   break;
                               case 3:
                                   if(checkItem.get(position).equals("0")&& checkItem.get(position-3).equals("0")&& checkItem.get(position+3).equals("0")){

                                       item.setDfd(String.valueOf(Integer.parseInt(item.getDfd())+20));
                                       checkItem.set(position, "1");
                                       mypage_dfdTv.setText(item.getDfd());
                                       Map<String, Object> data = new HashMap<>();
                                       data.put("dfd" , item.getDfd());
                                       db.collection(loginEmail).document("item").update(data);
                                       Map<String, Object> data2 = new HashMap<>();
                                       data2.put("item4", checkItem.get(position));
                                       db.collection(loginEmail).document("itemcheck").update(data2);
                                   }

                                   break;
                               case 4:
                                   if(checkItem.get(position).equals("0")&& checkItem.get(position-3).equals("0")&& checkItem.get(position+3).equals("0")){
                                       item.setAtk(String.valueOf(Integer.parseInt(item.getAtk())+20));
                                       checkItem.set(position, "1");
                                       mypage_atkTv.setText(item.getAtk());
                                       Map<String, Object> data = new HashMap<>();
                                       data.put("atk" , item.getAtk());
                                       db.collection(loginEmail).document("item").update(data);
                                       Map<String, Object> data2 = new HashMap<>();
                                       data2.put("item5", checkItem.get(position));
                                       db.collection(loginEmail).document("itemcheck").update(data2);
                                   }

                                   break;
                               case 5:
                                   if(checkItem.get(position).equals("0")&& checkItem.get(position-3).equals("0")&& checkItem.get(position+3).equals("0")){
                                       item.setSkill(shopListAbility[pos]);
                                       checkItem.set(position, "1");
                                       mypage_skillTv.setText(item.getSkill());
                                       Map<String, Object> data = new HashMap<>();
                                       data.put("skill" , item.getSkill());
                                       db.collection(loginEmail).document("item").update(data);
                                       Map<String, Object> data2 = new HashMap<>();
                                       data2.put("item6", checkItem.get(position));
                                       db.collection(loginEmail).document("itemcheck").update(data2);
                                   }

                               case 6:
                                   if(checkItem.get(position).equals("0")&& checkItem.get(position-6).equals("0")&& checkItem.get(position-3).equals("0")){


                                       item.setDfd(String.valueOf(Integer.parseInt(item.getDfd())+30));
                                       checkItem.set(position, "1");
                                       mypage_dfdTv.setText(item.getDfd());
                                       Map<String, Object> data = new HashMap<>();
                                       data.put("dfd" , item.getDfd());
                                       db.collection(loginEmail).document("item").update(data);
                                       Map<String, Object> data2 = new HashMap<>();
                                       data2.put("item7", checkItem.get(position));
                                       db.collection(loginEmail).document("itemcheck").update(data2);
                                   }
                                   break;
                               case 7:
                                   if(checkItem.get(position).equals("0")&& checkItem.get(position-6).equals("0")&& checkItem.get(position-3).equals("0")){


                                       item.setAtk(String.valueOf(Integer.parseInt(item.getAtk())+30));
                                       checkItem.set(position, "1");
                                       mypage_atkTv.setText(item.getAtk());
                                       Map<String, Object> data = new HashMap<>();
                                       data.put("atk" , item.getAtk());
                                       db.collection(loginEmail).document("item").update(data);
                                       Map<String, Object> data2 = new HashMap<>();
                                       data2.put("item8", checkItem.get(position));
                                       db.collection(loginEmail).document("itemcheck").update(data2);
                                       break;
                                   }

                                   break;
                               case 8:
                                   if(checkItem.get(position).equals("0")&& checkItem.get(position-6).equals("0")&& checkItem.get(position-3).equals("0")){
                                       item.setSkill(shopListAbility[pos]);
                                       checkItem.set(position, "1");
                                       mypage_skillTv.setText(item.getSkill());
                                       Map<String, Object> data = new HashMap<>();
                                       data.put("skill" , item.getSkill());
                                       db.collection(loginEmail).document("item").update(data);
                                       Map<String, Object> data2 = new HashMap<>();
                                       data2.put("item9", checkItem.get(position));
                                       db.collection(loginEmail).document("itemcheck").update(data2);
                                   }
                                   break;
                           }
                        }
                    }
                });
                dlg.setNegativeButton("장착해제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(haveItem.get(pos).equals("0")){
                            Toast.makeText(getApplicationContext(),"아이템을 보유하지 않아 장착해제 할 수 없습니다.",Toast.LENGTH_SHORT).show();
                        }else{
                            switch (pos){
                                case 0:
                                    if(checkItem.get(position).equals("1")){
                                        item.setDfd(String.valueOf(Integer.parseInt(item.getDfd())-10));
                                        checkItem.set(position, "0");
                                    }
                                    mypage_dfdTv.setText(item.getDfd());
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("dfd" , item.getDfd());
                                    db.collection(loginEmail).document("item").update(data);
                                    Map<String, Object> data11 = new HashMap<>();
                                    data11.put("item1", checkItem.get(position));
                                    db.collection(loginEmail).document("itemcheck").update(data11);

                                    //이미지 업데이트
                                    checkItem.set(position,"0");
                                    changeImage(checkItem.get(0)+checkItem.get(1)+checkItem.get(2));
                                    break;
                                case 1:
                                    if(checkItem.get(position).equals("1")){
                                        item.setAtk(String.valueOf(Integer.parseInt(item.getAtk())-10));
                                        checkItem.set(position, "0");
                                    }
                                    mypage_atkTv.setText(item.getAtk());
                                    Map<String, Object> data2 = new HashMap<>();
                                    data2.put("atk" , item.getAtk());
                                    db.collection(loginEmail).document("item").update(data2);
                                    Map<String, Object> data22 = new HashMap<>();
                                    data22.put("item2", checkItem.get(position));
                                    db.collection(loginEmail).document("itemcheck").update(data22);

                                    //이미지 업데이트
                                    checkItem.set(position,"0");
                                    changeImage(checkItem.get(0)+checkItem.get(1)+checkItem.get(2));
                                    break;

                                case 2:
                                    if(checkItem.get(position).equals("1")){
                                        item.setSkill("-");
                                        checkItem.set(position, "0");
                                    }
                                    mypage_skillTv.setText(item.getSkill());
                                    Map<String, Object> data3 = new HashMap<>();
                                    data3.put("skill" , item.getSkill());
                                    db.collection(loginEmail).document("item").update(data3);
                                    Map<String, Object> data33 = new HashMap<>();
                                    data33.put("item3", checkItem.get(position));
                                    db.collection(loginEmail).document("itemcheck").update(data33);

                                    //이미지 업데이트
                                    checkItem.set(position,"0");
                                    changeImage(checkItem.get(0)+checkItem.get(1)+checkItem.get(2));
                                    break;

                                case 3:
                                    if(checkItem.get(position).equals("1")){
                                        item.setDfd(String.valueOf(Integer.parseInt(item.getDfd())-20));
                                        checkItem.set(position, "0");
                                    }
                                    mypage_dfdTv.setText(item.getDfd());
                                    Map<String, Object> data4 = new HashMap<>();
                                    data4.put("dfd" , item.getDfd());
                                    db.collection(loginEmail).document("item").update(data4);
                                    Map<String, Object> data44 = new HashMap<>();
                                    data44.put("item4", checkItem.get(position));
                                    db.collection(loginEmail).document("itemcheck").update(data44);
                                    break;

                                case 4:
                                    if(checkItem.get(position).equals("1")){
                                        item.setAtk(String.valueOf(Integer.parseInt(item.getAtk())-20));
                                        checkItem.set(position, "0");
                                    }
                                    mypage_atkTv.setText(item.getAtk());
                                    Map<String, Object> data5 = new HashMap<>();
                                    data5.put("atk" , item.getAtk());
                                    db.collection(loginEmail).document("item").update(data5);
                                    Map<String, Object> data55 = new HashMap<>();
                                    data55.put("item5", checkItem.get(position));
                                    db.collection(loginEmail).document("itemcheck").update(data55);
                                    break;
                                case 5:
                                    if(checkItem.get(position).equals("1")){
                                        item.setSkill("-");
                                        checkItem.set(position, "0");
                                    }
                                    mypage_skillTv.setText(item.getSkill());
                                    Map<String, Object> data6 = new HashMap<>();
                                    data6.put("skill" , item.getSkill());
                                    db.collection(loginEmail).document("item").update(data6);
                                    Map<String, Object> data66 = new HashMap<>();
                                    data66.put("item6", checkItem.get(position));
                                    db.collection(loginEmail).document("itemcheck").update(data66);
                                    break;

                                case 6:
                                    if(checkItem.get(position).equals("1")){
                                        item.setDfd(String.valueOf(Integer.parseInt(item.getDfd())-30));
                                        checkItem.set(position, "0");
                                    }
                                    mypage_dfdTv.setText(item.getDfd());
                                    Map<String, Object> data7 = new HashMap<>();
                                    data7.put("dfd" , item.getDfd());
                                    db.collection(loginEmail).document("item").update(data7);
                                    Map<String, Object> data77 = new HashMap<>();
                                    data77.put("item7", checkItem.get(position));
                                    db.collection(loginEmail).document("itemcheck").update(data77);
                                    break;

                                case 7:
                                    if(checkItem.get(position).equals("1")){
                                        item.setAtk(String.valueOf(Integer.parseInt(item.getAtk())-30));
                                        checkItem.set(position, "0");
                                    }
                                    mypage_atkTv.setText(item.getAtk());
                                    Map<String, Object> data8 = new HashMap<>();
                                    data8.put("atk" , item.getAtk());
                                    db.collection(loginEmail).document("item").update(data8);
                                    Map<String, Object> data88 = new HashMap<>();
                                    data88.put("item8", checkItem.get(position));
                                    db.collection(loginEmail).document("itemcheck").update(data88);
                                    break;

                                case 8:
                                    if(checkItem.get(position).equals("1")){
                                        item.setSkill("-");
                                        checkItem.set(position, "0");
                                    }
                                    mypage_skillTv.setText(item.getSkill());
                                    Map<String, Object> data9 = new HashMap<>();
                                    data9.put("skill" , item.getSkill());
                                    db.collection(loginEmail).document("item").update(data9);
                                    Map<String, Object> data99 = new HashMap<>();
                                    data99.put("item9", checkItem.get(position));
                                    db.collection(loginEmail).document("itemcheck").update(data99);
                                    break;
                            }
                        }
                    }
                });
                dlg.setNeutralButton("취소",null);

                dlg.show();

            }
        });

        //파이어베이스 데이터 정보가져오기
         db.collection(loginEmail).document("item"). get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                 DocumentSnapshot document = task.getResult();
                 //객체(MYPage_Item)에 뿌려주기
                 item = document.toObject(MyPage_Item.class);
                 //파이어베이스에서 데이터 가져와서, 각 위젯에 데이터 설정해주기.
                //클래스 객체 필드와 파이어베이스 필드명 같아야함 (틀리면 값을 못가져온다)
                 mypage_levelTv.setText(item.getLevel());
                 mypage_expTv.setText(item.getExp());
                 mypage_pointTv.setText(item.getPoint());
                 mypage_stageTv.setText(item.getStage());
                 mypage_atkTv.setText(item.getAtk());
                 mypage_dfdTv.setText(item.getDfd());
                 mypage_skillTv.setText(item.getSkill());

                 //변수에 따로 데이터를 저장
                 stageInfo = item.getStage();
                 int achieveResult = (int)(((double)Integer.parseInt(stageInfo) / 9) * 100);
                 // 달성도 프로그레스바 설정
                 mypage_achievementPb.setProgress(achieveResult);

             }
         });


        //창닫기 버튼 클릭시 메인페이지로 이동하기
        mypage_closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(getApplicationContext(),StageActivity.class);
                startActivity(intent);
                finish();
            }
        });

         //메달 정보 보여주기
        db.collection(loginEmail).document("medal").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                medalInfo = document.toObject(Medal.class);
                String m1 = medalInfo.getMedal1();
                String m2 = medalInfo.getMedal2();
                String m3 = medalInfo.getMedal3();
                String m4 = medalInfo.getMedal4();
                String m5 = medalInfo.getMedal5();

                if(m1.equals("0")){
                    medal1.setVisibility(View.GONE);
                }else{
                    medal1.setVisibility(View.VISIBLE);
                }
                if(m2.equals("0")){
                    medal2.setVisibility(View.GONE);
                }else{
                    medal2.setVisibility(View.VISIBLE);
                }
                if(m3.equals("0")){
                    medal3.setVisibility(View.GONE);
                }else{
                    medal3.setVisibility(View.VISIBLE);
                }
                if(m4.equals("0")){
                    medal4.setVisibility(View.GONE);
                }else{
                    medal4.setVisibility(View.VISIBLE);
                }
                if(m5.equals("0")){
                    medal5.setVisibility(View.GONE);
                }else{
                    medal5.setVisibility(View.VISIBLE);
                }
            }
        });




        //총 정답률 정보 가져오기
        for(int i=0; i<9; i++){
            k=i;
            db.collection(loginEmail).document("stage"+String.valueOf(i+1)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                 DocumentSnapshot document = task.getResult();
                 StageResult stageResult = document.toObject(StageResult.class);
                 if(!stageResult.getAnswerRate().equals("0")){
                     answerRate.add(Integer.parseInt(stageResult.getAnswerRate()));
                     calAnswerRate(k);
                 }
                }
            });
        }




    }

    private void calAnswerRate(int k) {

        sum +=answerRate.get(a);
        int totalAnswerRate;
        if(answerRate.size()==0){
            totalAnswerRate = sum / (answerRate.size()+1);
        }else{
            totalAnswerRate = sum / answerRate.size();
        }

        mypage_answerratePb.setProgress(totalAnswerRate);
        a++;

    }
    //현재 장착한 아이템에 따라서 이미지를 변화시킨다.
    private void changeImage(String mountItem) {
        switch (mountItem){
            case "000":
                mypage_charIv.setImageResource(R.drawable.item000);
                break;
            case "001":
                mypage_charIv.setImageResource(R.drawable.item001);
                break;
            case "010":
                mypage_charIv.setImageResource(R.drawable.item010);
                break;
            case "100":
                mypage_charIv.setImageResource(R.drawable.item100);
                break;
            case "101":
                mypage_charIv.setImageResource(R.drawable.item101);
                break;
            case "011":
                mypage_charIv.setImageResource(R.drawable.item011);
                break;
            case "110":
                mypage_charIv.setImageResource(R.drawable.item110);
                break;
            case "111":
                mypage_charIv.setImageResource(R.drawable.item111);
                break;
        }
    }






}
