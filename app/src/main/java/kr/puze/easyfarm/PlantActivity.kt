package kr.puze.easyfarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PlantActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Android Compose 를 이용한 View 구성
        setContent {
            var expanded by remember { mutableStateOf(false) }
            val items = listOf("5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "65", "70", "75", "80", "85", "90", "95", "100")
            var selectedIndex by remember { mutableStateOf(11) }
            var ledStatus by remember { mutableStateOf(true) }
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
                    // ActionBar 를 제외한 나머지 부분의 위쪽 1/3 영역을 차지하도록 설정
                    Row(modifier = Modifier
                        .weight(1f, true)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(1f, true)
                        )
                        // 습도가 표기된 원형 뷰
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(2f, true)
                                .clip(CircleShape)
                                .fillMaxSize()
                                .aspectRatio(1f)
                                .border(
                                    width = 5.dp,
                                    color = Color(android.graphics.Color.parseColor("#ffffff")),
                                    shape = CircleShape
                                )
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = "60%",
                                color = Color(android.graphics.Color.parseColor("#ffffff")),
                                fontSize = 28.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(1f, true)
                        )
                    }
                    // ActionBar 를 제외한 나머지 부분의 중간쪽 1/3 영역을 차지하도록 설정
                    Row(modifier = Modifier
                        .weight(1f, true)
                    ) {
                        // 온도가 표기된 원형 뷰와 LED 여부가 표기된 원형 뷰를 1:1 로 구성
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(1f, true)
                                .clip(CircleShape)
                                .fillMaxSize()
                                .aspectRatio(1f)
                                .border(
                                    width = 5.dp,
                                    color = Color(android.graphics.Color.parseColor("#ffffff")),
                                    shape = CircleShape
                                )
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = "70°C",
                                color = Color(android.graphics.Color.parseColor("#ffffff")),
                                fontSize = 28.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(1f, true)
                                .clip(CircleShape)
                                .fillMaxSize()
                                .aspectRatio(1f)
                                .border(
                                    width = 5.dp,
                                    color = Color(android.graphics.Color.parseColor("#ffffff")),
                                    shape = CircleShape
                                )
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = "ON",
                                color = Color(android.graphics.Color.parseColor("#ffffff")),
                                fontSize = 28.sp
                            )
                        }
                    }
                    // ActionBar 를 제외한 나머지 부분의 아래쪽 1/3 영역을 차지하도록 설정
                    Row(modifier = Modifier
                        .weight(1f, true)
                        .background(androidx.compose.ui.graphics.Color.White)
                    ) {
                        // 습도를 변경 영역
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(1f, true)
                                .fillMaxSize()
                                .aspectRatio(1f)
                        ) {
                            Column(
                                Modifier.align(Alignment.Center).clickable { expanded = true },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "습도",
                                    color = Color(android.graphics.Color.parseColor("#000000")),
                                    fontSize = 28.sp
                                )
                                Text(
                                    text = "${items[selectedIndex]}%",
                                    color = Color(android.graphics.Color.parseColor("#000000")),
                                    fontSize = 56.sp
                                )
                                // 습도를 선택할 수 있는 DropdownMenu 구성
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.fillMaxWidth().background(
                                        Color.White)
                                ) {
                                    items.forEachIndexed { index, s ->
                                        DropdownMenuItem(onClick = {
                                            selectedIndex = index
                                            expanded = false
                                        }) {
                                            Text(text = "$s%")
                                        }
                                    }
                                }
                            }
                        }
                        // LED 여부 변경의 영역
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(1f, true)
                                .fillMaxSize()
                                .aspectRatio(1f)
                        ) {
                            Column(
                                // 해당 View 를 눌렀을 때에 led 값의 상태를 변경하는 로직
                                Modifier.align(Alignment.Center).clickable {
                                    ledStatus = !ledStatus
                                },
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = "LED",
                                    color = Color(android.graphics.Color.parseColor("#000000")),
                                    fontSize = 28.sp
                                )
                                Text(
                                    text = if(ledStatus){"ON"}else{"OFF"},
                                    color = Color(android.graphics.Color.parseColor("#000000")),
                                    fontSize = 56.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}