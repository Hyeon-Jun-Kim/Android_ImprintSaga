package com.hanshin.ncs_imprintsaga;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Battle_FinishDialog {
    private Context context;

    //구글로그인 회원정보
    String loginName ="";
    String loginEmail = "";
    //파이어베이스
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //결과 데이타에 사용되는 변수
    int correct;
    int total;
    int rank;
    int answerRate;
    //현재 스테이지 위치
    String stageNum;
    //리워드 데이타에 사용되는 변수
    int exp;
    int point;
    int level;
    String medal1;
    String medal2;
    String medal3;
    String medal4;
    String medal5;
    Medal m  =new Medal();;


    public Battle_FinishDialog(Context context) {
        this.context = context;
    }

    public void callFunction(boolean b , int correctNum, int totalNum) {

        loginName = BattleActivity.loginName;
        loginEmail =BattleActivity.loginEmail;

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.stage_end_info);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
         correct = correctNum;
         total = totalNum;
         answerRate = (int)(((double)correct/total)*100); // 정답률

        if(answerRate<40)
            rank = 1;
        else if(answerRate<80)
            rank = 2;
        else
            rank = 3;

        final TextView resultTv = (TextView) dlg.findViewById(R.id.resultTv_onBattle);
        if(b)
            resultTv.setText("WIN");
        else
            resultTv.setText("LOSE");

        final RatingBar rankRb = (RatingBar) dlg.findViewById(R.id.rankRb_onBattle);
        rankRb.setRating(rank);

        final TextView rateTv = (TextView) dlg.findViewById(R.id.rateTv_onBattle);
        rateTv.setText(rateTv.getText() + String.valueOf(correctNum) + "/" + String.valueOf(totalNum));

        //파이어베이스 데이터 업데이트
        Map<String, Object> data = new HashMap<>();
        data.put("correctNum", String.valueOf(correct));
        data.put("totalNum", String.valueOf(total));
        data.put("answerRate", String.valueOf(answerRate));
        data.put("rank", String.valueOf(rank));
        data.put("result", resultTv.getText());

        stageNum = BattleActivity.num;

        db.collection(loginEmail).document("stage"+stageNum).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //업데이트 완료
                Log.d("TAG", stageNum + "|" +correct + "|" + total + "|"
                        + answerRate + "|" + rank + "|" + rateTv.getText() +"\n");
            }
        });



        final Button endBtn = (Button) dlg.findViewById(R.id.endBtn_onBattle);
        final BattleActivity Ba = (BattleActivity)BattleActivity.BattlePageActivity;
        final StageActivity  Sa = (StageActivity) StageActivity.StagePageActivity;
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //게임을 승리할 시 리워드 지급
                if(resultTv.getText().equals("WIN")){
                    final View  dialogView =(LinearLayout)  View.inflate(context,  R.layout.stage_end_rewards, null);
                    AlertDialog.Builder dlg2 = new AlertDialog.Builder(context);
                    TextView medalTv_onBattle = dialogView.findViewById(R.id.medalTv_onBattle);

                    //현재 갖고있는 포인트, 경험치, 레벨 확인하기
                    db.collection(loginEmail).document("item").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            final DocumentSnapshot document = task.getResult();
                            MyPage_Item ask = document.toObject(MyPage_Item.class);
                            point = Integer.parseInt(ask.getPoint());
                            exp = Integer.parseInt(ask.getExp());
                            level = Integer.parseInt(ask.getLevel());
                            calculateReward();
                        }
                    });


                    //현재 갖고 있는 메달 확인
                    db.collection(loginEmail).document("medal").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            final DocumentSnapshot document = task.getResult();
                            Medal medalCheck = document.toObject(Medal.class);

                            medal1 = medalCheck.getMedal1();
                            medal2 = medalCheck.getMedal2();
                            medal3 = medalCheck.getMedal3();
                            medal4 = medalCheck.getMedal4();
                            medal5 = medalCheck.getMedal5();


                            m.setMedal1(medal1);
                            m.setMedal2(medal2);
                            m.setMedal3(medal3);
                            m.setMedal4(medal4);
                            m.setMedal5(medal5);

                            TextView medalTv_onBattle = dialogView.findViewById(R.id.medalTv_onBattle);
                            if(stageNum.equals("1")){
                                medalTv_onBattle.setVisibility(View.VISIBLE);
                                medalTv_onBattle.setText("스타 메달 획득");
                                m.setMedal1("1");
                            }else if(stageNum.equals("3")){
                                medalTv_onBattle.setVisibility(View.VISIBLE);
                                medalTv_onBattle.setText("파이어 메달 획득");
                                m.setMedal2("1");
                            }else if(stageNum.equals("5")){
                                medalTv_onBattle.setVisibility(View.VISIBLE);
                                medalTv_onBattle.setText("하트 메달 획득");
                                m.setMedal3("1");
                            }else if(stageNum.equals("7")){
                                medalTv_onBattle.setVisibility(View.VISIBLE);
                                medalTv_onBattle.setText("토이 메달 획득");
                                m.setMedal4("1");
                            }else if(stageNum.equals("9")){
                                medalTv_onBattle.setVisibility(View.VISIBLE);
                                medalTv_onBattle.setText("뮤직 메달 획득");
                                m.setMedal5("1");
                            }
                            calculateReward2();
                        }
                    });

                    dlg2.setView(dialogView);
                    dlg2.setNegativeButton("닫기", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //페이지 닫기
                            dlg.dismiss();
                            Intent intent=new Intent();
                            intent.setClass(Sa, Sa.getClass());
                            Sa.startActivity(intent);
                            Ba.finish();

                        }
                    });
                    dlg2.show();


                }
                else{
                    Ba.finish();
                }

            }
        });

        final Button retryBtn = (Button) dlg.findViewById(R.id.retryBtn_onBattle);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Ba.finish();
                Intent intent=new Intent();
                intent.setClass(Ba, Ba.getClass());
                intent.putExtra("stageNum", "Stage"+stageNum);
                Ba.startActivity(intent);
                //대화상자 창닫기
                dlg.dismiss();
            }
        });
    }



    //리워드 데이타 계산1
    private void calculateReward() {
        point = point+300;
        exp = exp + 40;
        if(exp>99){
            //경험치 100초과시 레벨업
            level = level+1;
            exp = exp-100;
        }
        MyPage_Item item = new MyPage_Item();
        item.setExp(String.valueOf(exp));
        item.setPoint(String.valueOf(point));
        item.setLevel(String.valueOf(level));
        item.setStage(stageNum);
        //리워드 업데이트
        Map<String, Object> data = new HashMap<>();
        data.put("exp", item.getExp());
        data.put("point", item.getPoint());
        data.put("level", item.getLevel());
        data.put("stage", item.getStage());
        db.collection(loginEmail).document("item").update(data);
    }
    //리워드 데이타 계산2
    private void calculateReward2() {
        //메달 업데이트
        Map<String, Object> data2 = new HashMap<>();
        data2.put("medal1", m.getMedal1());
        data2.put("medal2", m.getMedal2());
        data2.put("medal3", m.getMedal3());
        data2.put("medal4", m.getMedal4());
        data2.put("medal5", m.getMedal5());
        db.collection(loginEmail).document("medal").update(data2);
    }
}
