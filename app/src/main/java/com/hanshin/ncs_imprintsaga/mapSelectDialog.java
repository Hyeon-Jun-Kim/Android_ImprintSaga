package com.hanshin.ncs_imprintsaga;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class mapSelectDialog {
    private Context context;

    //구글로그인 회원정보
    String loginName ="";
    String loginEmail = "";
    //파이어베이스
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //파이어베이스로부터 받을 정보데이타 변수클래스
    StageResult stageData = new StageResult();
    //현재 맵 위치
    int num;


    public mapSelectDialog(Context context) {
        this.context = context;
    }

    public void callFunction(String stageNum) {

        loginName = StageActivity.loginName;
        loginEmail =StageActivity.loginEmail;

        num = Integer.parseInt(stageNum);

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.stage_start_info);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final TextView stageTv = (TextView) dlg.findViewById(R.id.stageTv);
        final RatingBar rankRb = (RatingBar) dlg.findViewById(R.id.rankRb);
        final TextView levelTv = (TextView) dlg.findViewById(R.id.levelTb);
        final TextView rateTv = (TextView) dlg.findViewById(R.id.rateTv);
        final Button selectBtn = (Button) dlg.findViewById(R.id.selectBtn);

        db.collection(loginEmail).document("stage"+stageNum).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                stageData = document.toObject(StageResult.class);
                int rank = Integer.parseInt(stageData.rank);
                String total = stageData.totalNum;
                String correct = stageData.correctNum;

                rankRb.setRating(rank);
                rateTv.setText("정답률 : "+ correct+" / "+ total );
            }
        });

        final String stage = stageTv.getText()+stageNum;
        stageTv.setText(stage);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(num==1){
                    //첫번째 스테이지는 이전맵 결과를 확인하지 않고 실행시킨다.
                    //이전맵을 클리어했을경우

                    Intent intent = new Intent(context.getApplicationContext(), BattleActivity.class);
                    intent.putExtra("stageNum",stage);
                    context.startActivity(intent);
                    dlg.cancel();
                } else if(StageActivity.preStage.get(num-2).equals("LOSE")){
                    //이전맵이 클리어한 상태가 아닐 경우 실행을 못하게 한다.
                    dlg.cancel();
                    Toast.makeText(context, "이전맵을 먼저 클리어해주세요!", Toast.LENGTH_SHORT).show();
                }else{
                    //이전맵을 클리어했을경우
                    Intent intent = new Intent(context.getApplicationContext(), BattleActivity.class);
                    intent.putExtra("stageNum",stage);
                    context.startActivity(intent);
                    dlg.cancel();
                }

            }
        });
    }
}
