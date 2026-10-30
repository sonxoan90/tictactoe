package vn.edu.hust.tictactoe.domain

import kotlin.random.Random

/** Máy chơi cờ. */
// [KT Ch3.4] object: chỉ cần một máy chơi duy nhất cho cả app
object Ai {

    /** Chọn ô cho quân [me]. Máy dễ: thắng nếu có thể, còn lại đi ngẫu nhiên. Máy khó: minimax, không bao giờ thua. */
    // [KT Ch2.2] tham số mặc định random: test truyền Random(seed) để kết quả lặp lại được
    fun chooseMove(board: Board, me: Player, hard: Boolean, random: Random = Random): Int {
        val empty = board.emptyCells()
        require(empty.isNotEmpty()) { "Bàn cờ đã đầy" }
        if (!hard) {
            // [KT Ch2.4] lambda: tìm ô giúp thắng ngay
            val winning = empty.firstOrNull { board.place(it, me).winner() == me }
            // [KT Ch1] toán tử Elvis ?: không có nước thắng thì đi ngẫu nhiên
            return winning ?: empty.random(random)
        }
        // Nhiều nước cùng điểm thì chọn ngẫu nhiên cho đỡ nhàm
        // [KT Ch2.5] map tạo cặp (ô, điểm) bằng infix to · maxOf, filter chọn nước tốt nhất
        val scored = empty.map { it to minimax(board.place(it, me), me, me.other()) }
        val best = scored.maxOf { it.second }
        return scored.filter { it.second == best }.random(random).first
    }

    /** Điểm của bàn cờ theo góc nhìn của [me]: dương là thắng (thắng càng sớm càng cao), âm là thua. */
    private fun minimax(board: Board, me: Player, turn: Player): Int {
        val winner = board.winner()
        // [KT Ch1.5] when: ba nhánh đầu là điểm dừng của đệ quy
        return when {
            winner == me -> 1 + board.emptyCells().size
            winner != null -> -1 - board.emptyCells().size
            board.isFull() -> 0
            else -> {
                // [KT Ch2] đệ quy: minimax gọi lại chính nó cho từng nước đi có thể
                val scores = board.emptyCells().map { minimax(board.place(it, turn), me, turn.other()) }
                // minimax: lượt máy chọn max, lượt đối thủ (giả định chơi giỏi nhất) chọn min
                if (turn == me) scores.max() else scores.min()
            }
        }
    }
}
