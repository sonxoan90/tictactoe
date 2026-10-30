package vn.edu.hust.tictactoe.ui.game

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.GridLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import vn.edu.hust.tictactoe.R
import vn.edu.hust.tictactoe.databinding.ActivityGameBinding
import vn.edu.hust.tictactoe.domain.Board
import vn.edu.hust.tictactoe.domain.GameState
import vn.edu.hust.tictactoe.domain.Opponent
import vn.edu.hust.tictactoe.domain.Player
import vn.edu.hust.tictactoe.ui.ThemeMode

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    // [KT Ch8] by viewModels(): Activity tạo lại vẫn lấy đúng ViewModel cũ
    private val viewModel: GameViewModel by viewModels()
    private lateinit var opponent: Opponent
    private val cells = mutableListOf<CellView>()
    private var lastState: GameState? = null
    private var resultShown = false
    private val showResultRunnable = Runnable { showResult() }

    // [KT Ch3.1] property có getter tính toán
    private val vsComputer get() = opponent != Opponent.HUMAN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val gap = (16 * resources.displayMetrics.density).toInt()
            v.setPadding(bars.left + gap, bars.top + gap, bars.right + gap, bars.bottom + gap)
            insets
        }

        // [KT Ch6] đọc extra từ Intent · [KT Ch1] ?: giá trị mặc định khi không có extra
        opponent = Opponent.valueOf(intent.getStringExtra(EXTRA_OPPONENT) ?: Opponent.HUMAN.name)
        viewModel.start(opponent)

        // [KT Ch1.5] when là biểu thức, đủ 3 nhánh của enum nên không cần else
        binding.tvTitle.setText(
            when (opponent) {
                Opponent.HUMAN -> R.string.mode_two_players
                Opponent.EASY -> R.string.mode_easy
                Opponent.HARD -> R.string.mode_hard
            }
        )
        binding.tvLabelX.text = if (vsComputer) getString(R.string.label_you) else Player.X.name
        binding.tvLabelO.text = if (vsComputer) getString(R.string.label_cpu) else Player.O.name

        buildBoard()
        binding.btnTheme.setIconResource(if (ThemeMode.isDark(this)) R.drawable.ic_sun else R.drawable.ic_moon)
        binding.btnTheme.setOnClickListener { ThemeMode.toggle(this) }
        binding.btnNewRound.setOnClickListener { viewModel.newRound() }
        binding.btnReset.setOnClickListener { viewModel.resetAll() }
        binding.btnBack.setOnClickListener { finish() }
        binding.btnResultNew.setOnClickListener { viewModel.newRound() }
        binding.btnResultMenu.setOnClickListener { finish() }

        // [KT Ch8] observe LiveData: dữ liệu đổi thì tự gọi render
        // Activity chỉ quan sát dữ liệu rồi vẽ lại
        viewModel.state.observe(this) { render(it) }
        viewModel.scores.observe(this) {
            binding.tvScoreX.text = it.x.toString()
            binding.tvScoreDraw.text = it.draws.toString()
            binding.tvScoreO.text = it.o.toString()
        }
    }

    private fun buildBoard() {
        val margin = (4 * resources.displayMetrics.density).toInt()
        // [KT Ch1.5] repeat tạo 9 ô · [KT Ch2.4] lambda nhận tham số index
        repeat(Board.SIZE * Board.SIZE) { index ->
            val cell = CellView(this).apply {
                setBackgroundResource(R.drawable.bg_cell)
                setOnClickListener { viewModel.onCellClicked(index) }
            }
            val params = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
            ).apply {
                width = 0
                height = 0
                setMargins(margin, margin, margin, margin)
            }
            binding.grid.addView(cell, params)
            cells.add(cell)
        }
    }

    private fun render(state: GameState) {
        val previous = lastState
        val line = state.winningLine.orEmpty()
        // [KT Ch1.5] vòng for trên range chỉ số 0..8
        for (i in cells.indices) {
            val cell = cells[i]
            val player = state.board.cells[i]
            // chỉ vẽ dần quân vừa mới đánh, không vẽ lại cả bàn khi xoay màn hình
            // [KT Ch2.2] named argument cho dễ đọc
            cell.setMark(player, animate = previous != null && previous.board.cells[i] == null)
            cell.setHighlighted(i in line)
            cell.setBackgroundResource(if (i in line) R.drawable.bg_cell_win else R.drawable.bg_cell)
            cell.isEnabled = !state.isOver && player == null
        }
        lastState = state

        renderStatus(state)
        binding.colX.setBackgroundResource(if (!state.isOver && state.turn == Player.X) R.drawable.bg_turn_active else 0)
        binding.colO.setBackgroundResource(if (!state.isOver && state.turn == Player.O) R.drawable.bg_turn_active else 0)

        if (state.isOver) scheduleResult() else hideResult()
    }

    private fun renderStatus(state: GameState) {
        val winner = state.winner
        val text = when {
            winner != null -> winnerText(winner)
            state.isDraw -> getString(R.string.status_draw)
            vsComputer -> getString(if (state.turn == Player.X) R.string.status_you else R.string.status_cpu)
            else -> getString(R.string.status_turn, state.turn.name)
        }
        binding.tvStatus.text = text
        // icon nhỏ của quân đang đến lượt, ẩn khi ván đã xong
        // [KT Ch1] null safety: ?.apply chỉ chạy khi có drawable
        val icon = if (state.isOver) null else AppCompatResources.getDrawable(
            this, if (state.turn == Player.X) R.drawable.ic_x else R.drawable.ic_o
        )?.apply {
            val size = (24 * resources.displayMetrics.density).toInt()
            setBounds(0, 0, size, size)
        }
        binding.tvStatus.setCompoundDrawablesRelative(icon, null, null, null)
    }

    private fun winnerText(winner: Player) = when {
        !vsComputer -> getString(R.string.status_win, winner.name)
        winner == Player.X -> getString(R.string.result_win_you)
        else -> getString(R.string.result_lose)
    }

    /** Thẻ kết quả hiện sau một nhịp để người chơi kịp nhìn đường thắng. */
    private fun scheduleResult() {
        if (resultShown) {
            fillResult()
            return
        }
        binding.root.removeCallbacks(showResultRunnable)
        binding.root.postDelayed(showResultRunnable, RESULT_DELAY_MS)
    }

    private fun showResult() {
        val state = viewModel.state.value ?: return
        if (!state.isOver || resultShown || isFinishing || isDestroyed) return
        resultShown = true
        fillResult()
        binding.resultOverlay.visibility = View.VISIBLE
        binding.resultOverlay.alpha = 0f
        binding.resultOverlay.animate().alpha(1f).setDuration(200).start()
        val offset = 60 * resources.displayMetrics.density
        // [KT Android] ViewPropertyAnimator: thẻ kết quả trượt lên và hiện dần
        binding.resultCard.translationY = offset
        binding.resultCard.alpha = 0f
        binding.resultCard.animate()
            .translationY(0f).alpha(1f)
            .setDuration(300).setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun fillResult() {
        val state = viewModel.state.value ?: return
        val winner = state.winner
        binding.ivResult.setImageResource(
            when {
                winner == null -> R.drawable.ic_equal
                vsComputer && winner == Player.O -> R.drawable.ic_sad
                else -> R.drawable.ic_trophy
            }
        )
        binding.tvResultTitle.text = winner?.let { winnerText(it) } ?: getString(R.string.status_draw)
        val scores = viewModel.scores.value ?: return
        binding.tvResultSummary.text = getString(
            R.string.result_summary,
            binding.tvLabelX.text, scores.x, scores.draws, binding.tvLabelO.text, scores.o,
        )
    }

    private fun hideResult() {
        binding.root.removeCallbacks(showResultRunnable)
        resultShown = false
        binding.resultOverlay.animate().cancel()
        binding.resultCard.animate().cancel()
        binding.resultOverlay.visibility = View.GONE
    }

    override fun onDestroy() {
        binding.root.removeCallbacks(showResultRunnable)
        super.onDestroy()
    }

    // [KT Ch3.4] companion object · [KT Ch6] hàm tạo Intent mở màn này
    companion object {
        private const val EXTRA_OPPONENT = "opponent"
        private const val RESULT_DELAY_MS = 700L

        fun intent(context: Context, opponent: Opponent) =
            Intent(context, GameActivity::class.java).putExtra(EXTRA_OPPONENT, opponent.name)
    }
}
