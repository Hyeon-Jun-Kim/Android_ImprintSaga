package com.hanshin.ncs_imprintsaga;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopActivity extends Activity {

    ImageButton shop_closeBtn;
    ImageView shop_charIv;
    TextView shop_pointTv;
    GridView shop_GridView;
    ShopViewAdapter adapter;

    //구글로그인 회원정보
    String loginName ="";
    String loginEmail = "";

    //아이템 장착 여부
    ArrayList<String> checkItem = new ArrayList<String>();

    //그리드뷰 이미지 타이틀
    //그리드뷰 이미지 타이틀
    String[] shopListTitle=  {
            "cap", "newspaper", "sneakers",
            "coffee ", "book", "magnifier",
            "hambuger", "phone", "sunglass"
    };
    //그리드뷰 이미지 저장위치
    Integer[] shopListImage ={
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
    String[] shopListAbility = {
            //기본 HP = 100, 기본 공격력 = 10, 기본 방어력 = 0, 능력 = x
            "방어 10 증가", "공격 10 증가 ", "힌트 1회 제공",
            "방어 20 증가", "공격 20 증가", "힌트 2회 제공",
            "방어 30 증가", "공격 30 증가",  "힌트 3회 제공"
    };

    //파이어베이스 데이터 정보가져오기
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //구매할때 사용하는 변수
    int price =0;
    String s= "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop);

        shop_closeBtn = findViewById(R.id.shop_closeBtn);
        shop_charIv = findViewById(R.id.shop_charIv);
        shop_pointTv = findViewById(R.id.shop_pointTv);
        shop_GridView = findViewById(R.id.shop_GridView);


        //로그인한 회원정보를 가져오는 변수
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount != null){
            //회원정보 이름
            loginName = signInAccount.getDisplayName();
            //회원정보 이메일
            loginEmail = signInAccount.getEmail();
            Toast.makeText(ShopActivity.this, loginName+" "+loginEmail, Toast.LENGTH_SHORT).show();
        }


        // 파이어베이스 경로 ( mypage컬렉션 -> item문서의 경로 설정)
        db.collection(loginEmail).document("item").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                //객체(MYPage_Item)에 뿌려주기 (파이어베이스 문서 -> 객체 Item에 주입)
                MyPage_Item item = document.toObject(MyPage_Item.class);
                //파이어베이스에서 데이터 가져와서, 각 위젯에 데이터 설정해주기.
                //클래스 객체 필드와 파이어베이스 필드명 같아야함 (틀리면 값을 못가져온다)
               shop_pointTv.setText(item.getPoint());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"데이터를 가져오지 못했습니다", Toast.LENGTH_LONG).show();
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



        //shop페이지 그리드뷰 및 어댑터 설정
        adapter = new ShopViewAdapter(this);
        shop_GridView.setAdapter(adapter);

        //상단의 왼쪽 닫기 버튼을 클릭할때 이벤트 작성 (메인페이지로 돌아가게 설정하기.)
        shop_closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), StageActivity.class);
            startActivity(intent);
            }
        });
        //그리드뷰에 각각 리스트를 클릭할 때 이벤트 작성. (대화상자 띄우고 구매할지 물어보기 설정하기)
        shop_GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
                public void onItemClick(AdapterView parent, View view, final int position, long id) {
                    View dialogView = View.inflate(ShopActivity.this, R.layout.shop_list_dialog, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(com.hanshin.ncs_imprintsaga.ShopActivity.this);
                AlertDialog dialog = dlg.create();

                ImageView img =  dialogView.findViewById(R.id.shopListImage);
                TextView tv = dialogView.findViewById(R.id.shopListPrice);
                TextView ability = dialogView.findViewById(R.id.shopabilty);

                img.setImageResource(shopListImage[position]);
                tv.setText(shopListPrice[position].toString());
                dlg.setTitle(shopListTitle[position]);
                ability.setText(shopListAbility[position]);

                //4번아이템부터 9번아이템 흑백으로 표시
                if(position ==0 || position==1 || position==2){
                }else{
                    ColorMatrix matrix = new ColorMatrix();
                    matrix.setSaturation(0);
                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                    img.setColorFilter(filter);
                }

                dlg.setIcon(R.drawable.ic_baseline_shopping_basket_24);
                dlg.setView(dialogView);
                //리스트에 아이템 구매버튼을 클릭했을 때, 이벤트 작성.
                dlg.setPositiveButton("구매", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(position==0 || position==1 || position==2){
                            db.collection(loginEmail).document("item").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    final DocumentSnapshot document = task.getResult();
                                    MyPage_Item item = (MyPage_Item) document.toObject(MyPage_Item.class);
                                    price =Integer.parseInt(item.getPoint());
                                    //현재 갖고 있는 아이템 데이터베이스에 등록
                                    db.collection(loginEmail).document("itemlist").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            final DocumentSnapshot document = task.getResult();
                                            //파이어베이스에서 정보 가져오기
                                            Mypage_HavingItem have = document.toObject(Mypage_HavingItem.class);

                                            int which = position+1;
                                            //현재 아이템 정보 가져오기

                                            if(which ==1){
                                                s = have.getItem1();
                                            }
                                            if(which ==2){
                                                s = have.getItem2();
                                            }
                                            if(which ==3){
                                                s = have.getItem3();
                                            }
                                            if(which ==4){
                                                s = have.getItem4();
                                            }
                                            if(which ==5){
                                                s = have.getItem5();
                                            }
                                            if(which ==6){
                                                s = have.getItem6();
                                            }
                                            if(which ==7){
                                                s = have.getItem7();
                                            }
                                            if(which ==8){
                                                s = have.getItem8();
                                            }
                                            if(which ==9){
                                                s = have.getItem9();
                                            }


                                            //아이템을 갖고 있으면 실행 ( 0은 소유x , 1은 소유o)
                                            if (s.equals("1")) {
                                                Toast.makeText(getApplicationContext(), shopListTitle[position] + "가 이미 있으므로 구매할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                            //아이템을 갖고 있지 않으면 실행
                                            else {
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("item" + String.valueOf(position + 1), "1");

                                                db.collection(loginEmail).document("itemlist").update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(getApplicationContext(), shopListTitle[position] + "를 구매했습니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                //포인트 남은 금액 = sum
                                                int sum = price - shopListPrice[position];
                                                //현재 남은 포인트 최신화 시켜주기.

                                                Map<String, Object> data2 = new HashMap<>();
                                                data2.put("point", String.valueOf(sum));
                                                db.collection(loginEmail).document("item").update(data2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                    }
                                                });
                                                shop_pointTv.setText(String.valueOf(sum));

                                            }
                                        }
                                    });


                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(), "현재 구매할 수 없는 아이템입니다.", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("취소", null);
                dlg.show();
            }

        });
    }

    //현재 장착한 아이템에 따라서 이미지를 변화시킨다.
    private void changeImage(String mountItem) {
        switch (mountItem){
            case "000":
                shop_charIv.setImageResource(R.drawable.item000);
                break;
            case "001":
                shop_charIv.setImageResource(R.drawable.item001);
                break;
            case "010":
                shop_charIv.setImageResource(R.drawable.item010);
                break;
            case "100":
                shop_charIv.setImageResource(R.drawable.item100);
                break;
            case "101":
                shop_charIv.setImageResource(R.drawable.item101);
                break;
            case "011":
                shop_charIv.setImageResource(R.drawable.item011);
                break;
            case "110":
                shop_charIv.setImageResource(R.drawable.item110);
                break;
            case "111":
                shop_charIv.setImageResource(R.drawable.item111);
                break;
        }
    }


}