package vn.edu.hust.tictactoe.ui.menu

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import vn.edu.hust.tictactoe.R
import vn.edu.hust.tictactoe.data.GameStats
import vn.edu.hust.tictactoe.databinding.ActivityMenuBinding
import vn.edu.hust.tictactoe.domain.Opponent
import vn.edu.hust.tictactoe.ui.ThemeMode
import vn.edu.hust.tictactoe.ui.game.GameActivity

class MenuActivity : AppCompatActivity() {

    // [KT Ch5] View Binding thay cho findViewById · [KT Ch1] lateinit: gán sau trong onCreate
    private lateinit var binding: ActivityMenuBinding
    private lateinit var stats: GameStats

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        stats = GameStats(this)

        binding.btnTheme.setIconResource(if (ThemeMode.isDark(this)) R.drawable.ic_sun else R.drawable.ic_moon)
        binding.btnTheme.setOnClickListener { ThemeMode.toggle(this) }
        // [KT Ch4] xử lý sự kiện bấm · [KT Ch2.4] lambda
        binding.btnEasy.setOnClickListener { startGame(Opponent.EASY) }
        binding.btnHard.setOnClickListener { startGame(Opponent.HARD) }
        binding.btnTwoPlayers.setOnClickListener { startGame(Opponent.HUMAN) }
        binding.btnResetStats.setOnClickListener {
            // [KT Ch4] hộp thoại xác nhận trước thao tác không hoàn tác · _ là tham số lambda không dùng
            MaterialAlertDialogBuilder(this)
                .setMessage(R.string.reset_confirm)
                .setPositiveButton(R.string.yes) { _, _ ->
                    stats.reset()
                    showStats()
                }
                .setNegativeButton(R.string.no, null)
                .show()
        }
    }

    // [KT Ch7] vòng đời: Back từ màn chơi chỉ chạy onResume, không chạy lại onCreate
    // Quay lại từ màn chơi thì thành tích đã đổi nên phải cập nhật
    override fun onResume() {
        super.onResume()
        showStats()
    }

    private fun startGame(opponent: Opponent) {
        // [KT Ch6] Intent mở màn chơi, kèm chế độ đối thủ
        startActivity(GameActivity.intent(this, opponent))
    }

    private fun showStats() {
        bindStats(Opponent.EASY, binding.tvEasyWin, binding.tvEasyDraw, binding.tvEasyLoss)
        bindStats(Opponent.HARD, binding.tvHardWin, binding.tvHardDraw, binding.tvHardLoss)
    }

    private fun bindStats(opponent: Opponent, win: TextView, draw: TextView, loss: TextView) {
        win.text = stats.wins(opponent).toString()
        draw.text = stats.draws(opponent).toString()
        loss.text = stats.losses(opponent).toString()
    }
}
