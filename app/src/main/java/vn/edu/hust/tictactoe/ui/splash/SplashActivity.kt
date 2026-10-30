package vn.edu.hust.tictactoe.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import vn.edu.hust.tictactoe.databinding.ActivitySplashBinding
import vn.edu.hust.tictactoe.ui.menu.MenuActivity

/** Màn khởi động: logo phóng to dần, hiện tên game rồi chuyển sang menu. */
// [KT Ch4] Activity LAUNCHER · [KT Ch3.2] kế thừa AppCompatActivity
class SplashActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [KT Ch2.4] scope function apply: cấu hình nhiều thuộc tính của cùng một View
        binding.logo.apply {
            scaleX = 0.5f
            scaleY = 0.5f
            alpha = 0f
            animate().scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(600).setInterpolator(OvershootInterpolator()).start()
        }
        binding.title.apply {
            alpha = 0f
            animate().alpha(1f).setStartDelay(400).setDuration(500).start()
        }

        // [KT Ch2.4] lambda truyền vào postDelayed · [KT Ch6] Intent sang Menu
        handler.postDelayed({
            startActivity(Intent(this, MenuActivity::class.java))
            finish() // bấm Back ở menu sẽ không quay lại splash
        }, SPLASH_MS)
    }

    // [KT Ch7] vòng đời: Activity bị hủy thì gỡ hẹn giờ
    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    companion object {
        private const val SPLASH_MS = 2000L
    }
}
