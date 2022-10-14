package kr.puze.easyfarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Android Compose 를 이용한 View 구성
        setContent {
            // 화면을 최대로, 배경색상을 #FFFFFF, #3399FF 의 그라데이션으로 설정
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(android.graphics.Color.parseColor("#3399FF")),
                                Color(android.graphics.Color.parseColor("#FFFFFF"))
                            )
                        )
                    )
                    .fillMaxSize()
            ){
                // Column 안의 View 를 Column 의 중심으로 설정
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ActionBar 구성
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(android.graphics.Color.parseColor("#3399FF")),)
                            .padding(16.dp)
                    ){
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "Easy Farm",
                            color = Color(android.graphics.Color.parseColor("#FFFFFF")),
                            fontSize = 36.sp
                        )
                    }

                    // 식물 관리 텍스트
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = "식물관리",
                        color = Color(android.graphics.Color.parseColor("#ffffff")),
                        fontSize = 28.sp
                    )
                    // 식물 관리에 관한 표 이미지
                    Image(
                        modifier = Modifier
                            .weight(1f, true)
                            .padding(16.dp)
                            .fillMaxSize(),
                        painter = painterResource(R.drawable.info_chart),
                        contentDescription = "Contact profile picture",
                        // Clip image to be shaped as a circle
                    )

                    // 다육식물 텍스트
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = "다육식물",
                        color = Color(android.graphics.Color.parseColor("#ffffff")),
                        fontSize = 28.sp
                    )
                    // 다육식물에 관한 설명
                    Box(
                        modifier = Modifier
                            .weight(1f, true)
                            .padding(16.dp)
                            .border(
                                width = 5.dp,
                                color = Color(android.graphics.Color.parseColor("#000000")),
                            )
                            .background(Color(android.graphics.Color.parseColor("#ffffff")))
                            .padding(16.dp)
                    ){
                        Row(
                            modifier = Modifier.align(Alignment.Center),
                        ){
                            Image(
                                modifier = Modifier.weight(1f, true).fillMaxSize(),
                                painter = painterResource(R.drawable.info_plant),
                                contentDescription = "Contact profile picture",
                                // Clip image to be shaped as a circle
                            )
                            Text(
                                modifier = Modifier.weight(1f, true),
                                text = "다육식물은 건조한 기후에 적응하기 위하여 잎이나 줄기, 혹은 뿌리에 물을 저장하는 구조를 지니고 있는 식물들을 일컫는다. 통통하고 탱글탱글한 외형 덕분에 인기가 많으며, 친근하게 '다육이'라고 부르는 경우가 많다."
                            )
                        }
                    }
                }
            }
        }
    }
}