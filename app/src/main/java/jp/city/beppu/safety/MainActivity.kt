package jp.city.beppu.safety

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.view.ViewGroup
import android.widget.FrameLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 画面レイアウトをコードで最小表示（XML不要）
        val tv = TextView(this).apply {
            text = "起動テスト：外勤安全サポート"
            textSize = 20f
            setPadding(32, 32, 32, 32)
        }
        val root = FrameLayout(this).apply {
            addView(tv, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ))
        }
        setContentView(root)
    }
}
