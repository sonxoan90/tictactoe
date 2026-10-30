package vn.edu.hust.tictactoe.domain

/** Trạng thái một ván: bàn cờ và lượt của ai. */
// [KT Ch3.4] data class · [KT Ch2.2] tham số mặc định: bàn trống, X đi trước
data class GameState(val board: Board = Board(), val turn: Player = Player.X) {

    // [KT Ch3.1] property có getter tính toán: tính lại mỗi lần đọc nên luôn khớp với bàn
    val winner: Player? get() = board.winner()
    val winningLine: List<Int>? get() = board.winningLine()
    val isDraw: Boolean get() = winner == null && board.isFull()
    val isOver: Boolean get() = winner != null || board.isFull()

    /** Đánh vào ô [index]; trả về chính nó nếu ván đã xong hoặc ô đã có quân. */
    fun play(index: Int): GameState {
        // [KT Ch1.5] if: nước không hợp lệ thì trả về chính đối tượng cũ
        if (isOver || board.cells[index] != null) return this
        return GameState(board.place(index, turn), turn.other())
    }
}
