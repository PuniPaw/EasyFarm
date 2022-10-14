package kr.puze.easyfarm

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import kr.puze.easyfarm.Bluetooth.Beacon
import kr.puze.easyfarm.Bluetooth.Paired
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("LongLogTag")
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class PlantActivity : AppCompatActivity() {

    lateinit var mScanCallback: ScanCallback
    var REQUEST_ENABLE_BT = 200
    lateinit var mBluetoothAdapter: BluetoothAdapter
    lateinit var mBluetoothLeScanner: BluetoothLeScanner
    lateinit var mBluetoothLeAdvertiser: BluetoothLeAdvertiser
    lateinit var beacon: Vector<Beacon>
    lateinit var mDevices: Set<BluetoothDevice>
    lateinit var mSocket: BluetoothSocket
    lateinit var mOutputStream: OutputStream
    lateinit var mInputStream: InputStream
    lateinit var mWorkerThread: Thread
    var simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.KOREAN)
    var isBluetoothConnected = false
    var result = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //블루투스 설정 코드 실행
        settingBluetooth()

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
                                text = "${items[selectedIndex]}%",
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
                                text = if(ledStatus){"ON"}else{"OFF"},
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
                                            sendDataToBluetooth(items[selectedIndex])
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
                                    sendDataToBluetooth(ledStatus.toString())
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

    private fun settingBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!mBluetoothAdapter.isEnabled) {
            startActivityForResult(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_ENABLE_BT
            )
        } else {
            mBluetoothLeScanner = mBluetoothAdapter.bluetoothLeScanner
            mBluetoothLeAdvertiser = mBluetoothAdapter.bluetoothLeAdvertiser
            beacon = Vector()
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }

            //TODO 연결할 블루투스의 어드레스를 입력합니다.
            mBluetoothLeScanner.startScan(getScanCallback("bluetooth_address"))
        }

        selectDevice()
    }

    fun getScanCallback(address: String): ScanCallback {
        mScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                try {
                    runOnUiThread {
                        beacon.add(
                            Beacon(
                                result.device.address,
                                result.rssi,
                                simpleDateFormat.format(Date()),
                                result.device
                            )
                        )
                        if (ActivityCompat.checkSelfPermission(
                                this@PlantActivity,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return@runOnUiThread
                        }
                        if (beacon.lastElement().address.contains(address)) beacon.lastElement().device.createBond()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onBatchScanResults(results: List<ScanResult?>) {
                super.onBatchScanResults(results)
                Log.d("onBatchScanResults", results.size.toString() + "")
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.d("onScanFailed()", errorCode.toString() + "")
            }
        }

        return mScanCallback
    }

    private fun selectDevice() {
        mDevices = mBluetoothAdapter.bondedDevices;
        var mPairedDeviceCount = mDevices.size

        if (mPairedDeviceCount > 0) {
            // 페어링 된 블루투스 장치의 이름 목록 작성
            var paring: Vector<Paired> = Vector()
            for (device: BluetoothDevice in mDevices) {
                Log.d("LOGTAG", device.toString())
                Log.d("LOGTAG", device.name)
                Log.d("LOGTAG", device.address)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                if (device.uuids != null) {
                    if (device.uuids.isNotEmpty()) {
                        paring.add(
                            Paired(
                                device.name,
                                device.address,
                                device.uuids[0].uuid.toString()
                            )
                        )
                    } else {
                        paring.add(Paired(device.name, device.address, "null"))
                    }
                } else {
                    paring.add(Paired(device.name, device.address, "null"))
                }
            }
            paring.forEach {
                //TODO 연결할 블루투스의 이름을 입력합니다.
                if (it.name.contains("FB153")) connectToSelectedDevice(it.name)
            }
        }
    }

    private fun connectToSelectedDevice(selectedDeviceName: String) {
        var mRemoteDevice = getDeviceFromBondedList(selectedDeviceName)
        //블루투스의 기본 UUID 로 사용하는 블루투스 기기의 UUID 를 임의로 변경했다면 변경한 값을 입력해야 함
        var uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

        if (mRemoteDevice != null) {
            try {
                Log.d("LOGTAG", "connectToSelectedDevice: $selectedDeviceName")
                // 소켓 생성
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid)
                // RFCOMM 채널을 통한 연결
                mSocket.connect()
                // 데이터 송수신을 위한 스트림 열기
                mOutputStream = mSocket.outputStream
                mInputStream = mSocket.inputStream

                beginListenForData()
                isBluetoothConnected = true
                Toast.makeText(this@PlantActivity, "블루투스 연결 성공!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // 블루투스 연결 중 오류 발생
                Toast.makeText(this@PlantActivity, "블루투스 연결 실패!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getDeviceFromBondedList(name: String): BluetoothDevice? {
        var selectedDevice: BluetoothDevice? = null

        for (device: BluetoothDevice in mDevices) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null
            }
            if (name == device.name) {
                selectedDevice = device
                break
            }
        }

        return selectedDevice
    }

    private fun sendDataToBluetooth(data: String){
        if (isBluetoothConnected) {
            // 전송할 데이터를 입력
            // 보편적으로 \r (리턴) 문자를 통해 데이터 전송의 끝을 나타내며 ByteArray 형식으로 변경하여 전송함
            val msgBuffer = ("DATA${data}\r").toByteArray()
            try {
                //OutputStream 의 write 를 통해 블루투스에게 데이터를 전송
                mOutputStream.write(msgBuffer)
            } catch (e: IOException) {
                Toast.makeText(this@PlantActivity, "Connection Failure", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this@PlantActivity, "블루투스가 연결되지 않았습니다. 재연결합니다.", Toast.LENGTH_LONG).show()
            selectDevice()
        }
    }

    // 블루투스로부터 데이터를 수신받는 코드
    // Thread 를 사용하여 실시간으로 수신할 수 있음
    private fun beginListenForData() {
        val handler = Handler()
        mWorkerThread = Thread(Runnable {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    var bytesAvailable = mInputStream.available()
                    if (bytesAvailable > 0) {
                        var packetBytes = ByteArray(bytesAvailable)
                        mInputStream.read(packetBytes)
                        for (i in 0 until bytesAvailable) {
                            val b = packetBytes[i]
                            if (b.toInt() == 13) {
                                Log.d("LOGTAG", "result: $result")
                                //TODO 블루투스 값을 실시간으로 수신받는다면 여기에서 수신할 수 있음
//                                handler.post {
//                                    if (result.contains("BV")) setBatteryProgress(result.split("BV")[1].toInt())
//                                }
                                result = ""
                            } else {
                                if (b.toInt() != 0) {
                                    val ch = b.toChar()
                                    result += ch
                                }
                            }
                        }
                    }
                } catch (e: IOException) {

                }
            }
        })
        mWorkerThread.start()

        isBluetoothConnected = true
    }

    // 해당 액티비티를 나가게 되면 블루투스 스캔을 정지
    override fun onDestroy() {
        super.onDestroy()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mBluetoothLeScanner.stopScan(mScanCallback)
        try {
            mWorkerThread.interrupt();   // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mOutputStream.close();
            mSocket.close();
        } catch (e: Exception) {

        }
    }

    // 블루투스가 사용 가능하다면 블루투스 설정 코드를 실행
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) settingBluetooth()
    }
}