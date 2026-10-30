package vn.edu.hust.tictactoe.ui.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.edu.hust.tictactoe.data.GameStats
import vn.edu.hust.tictactoe.domain.Ai
import vn.edu.hust.tictactoe.domain.GameState
import vn.edu.hust.tictactoe.domain.Opponent
import vn.edu.hust.tictactoe.domain.Player

/** Số ván thắng của mỗi bên trong lần mở màn hình này. */
// [KT Ch3.4] data class, cập nhật bằng copy
data class Scores(val x: Int = 0, val o: Int = 0, val draws: Int = 0)

/** Giữ ván đang chơi (sống sót khi xoay màn hình). Người chơi là X, máy (nếu có) là O. */
// [KT Ch8] ViewModel sống qua lúc Activity tạo lại · [KT Ch3.2] kế thừa AndroidViewModel để có Context
class GameViewModel(app: Application) : AndroidViewModel(app) {

    private val stats = GameStats(app)
    // [KT Ch8] MutableLiveData để private (chỉ ViewModel sửa), LiveData public (Activity chỉ đọc)
    private val _state = MutableLiveData(GameState())
    private val _scores = MutableLiveData(Scores())
    private var opponent = Opponent.HUMAN
    private var started = false
    private var starter = Player.X
    private var job: Job? = null

    val state: LiveData<GameState> = _state
    val scores: LiveData<Scores> = _scores

    fun start(opponent: Opponent) {
        if (started) return
        started = true
        this.opponent = opponent
    }

    fun onCellClicked(index: Int) {
        if (isComputerTurn()) return
        applyMove(index)
    }

    /** Ván mới: đổi bên đi trước cho công bằng. */
    fun newRound() {
        job?.cancel()
        starter = starter.other()
        _state.value = GameState(turn = starter)
        playComputerIfNeeded()
    }

    /** Reset: xóa bàn và đưa bảng điểm về 0 (thành tích lưu lâu dài vẫn giữ). */
    fun resetAll() {
        job?.cancel()
        starter = Player.X
        _scores.value = Scores()
        _state.value = GameState()
    }

    private fun isComputerTurn(): Boolean {
        val s = _state.value!!
        return opponent != Opponent.HUMAN && !s.isOver && s.turn == Player.O
    }

    private fun applyMove(index: Int) {
        val before = _state.value!!
        val after = before.play(index)
        // [KT Ch1] === so sánh tham chiếu: play trả chính nó khi nước không hợp lệ
        if (after === before) return
        _state.value = after
        if (after.isOver) recordResult(after) else playComputerIfNeeded()
    }

    private fun playComputerIfNeeded() {
        if (!isComputerTurn()) return
        // [KT Ch9] coroutine: delay không chặn giao diện, tự hủy khi ViewModel bị hủy
        job = viewModelScope.launch {
            delay(450) // chờ một chút để người chơi kịp nhìn
            val move = Ai.chooseMove(_state.value!!.board, Player.O, hard = opponent == Opponent.HARD)
            applyMove(move)
        }
    }

    private fun recordResult(s: GameState) {
        val old = _scores.value!!
        // [KT Ch1.5] when đủ nhánh X, O, null · [KT Ch2.2] copy với named argument
        _scores.value = when (s.winner) {
            Player.X -> old.copy(x = old.x + 1)
            Player.O -> old.copy(o = old.o + 1)
            null -> old.copy(draws = old.draws + 1)
        }
        if (opponent == Opponent.HUMAN) return
        when (s.winner) {
            Player.X -> stats.addWin(opponent)
            Player.O -> stats.addLoss(opponent)
            null -> stats.addDraw(opponent)
        }
    }
}
