package jp.city.beppu.safety

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fused = LocationServices.getFusedLocationProviderClient(this)
        val btn = findViewById<Button>(R.id.sendLocationButton)

        btn.setOnClickListener {
            // 位置権限チェック（初回は許可ダイアログを出す）
            val fineOk = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if (!fineOk) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100
                )
                return@setOnClickListener
            }

            // 最後に取得した位置を使う（屋外での実行推奨）
            fused.lastLocation.addOnSuccessListener { loc: Location? ->
                if (loc == null) {
                    Toast.makeText(this, "位置が取得できませんでした。屋外で再試行してください。", Toast.LENGTH_SHORT).show()
                } else {
                    postToGAS(loc.latitude, loc.longitude, loc.accuracy)
                }
            }.addOnFailureListener {
                Toast.makeText(this, "位置取得に失敗しました。", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun postToGAS(lat: Double, lng: Double, acc: Float) {
        // ★ 後であなたの値に差し替え ★
        val gasUrl = "https://YOUR_SCRIPT_ID/exec"   // ← あなたのGAS /exec
        val apiKey = "YOUR_API_KEY"                  // ← setApiKeyOnce() の値
        val email  = "user@example.jp"               // ← 職員の業務メール

        val json = JSONObject().apply {
            put("apiKey", apiKey)
            put("email", email)
            put("lat", lat)
            put("lng", lng)
            put("acc", acc)
        }.toString()

        val req = Request.Builder()
            .url(gasUrl)
            .post(json.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        // 簡易実装：同期送信→結果だけトースト
        Thread {
            runCatching {
                client.newCall(req).execute().use { resp -> resp.isSuccessful }
            }.onSuccess { ok ->
                runOnUiThread {
                    Toast.makeText(this, if (ok) "送信しました" else "送信失敗", Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                runOnUiThread {
                    Toast.makeText(this, "送信エラー", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}
