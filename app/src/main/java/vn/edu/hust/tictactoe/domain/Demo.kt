package vn.edu.hust.tictactoe.domain

import kotlin.random.Random

/*
 * Chạy thử luật chơi và máy chơi KHÔNG cần emulator:
 * trong Android Studio bấm mũi tên xanh cạnh `fun main()`.
 */

// [KT Ch2.3] hàm rút gọn · [KT Ch1] ?: ô trống in dấu chấm
private fun cellText(p: Player?) = p?.name ?: "."

/** In bàn cờ 3 dòng, ví dụ "X . O". */
fun Board.toText(): String =
    // [KT Ch2.5] chunked chia 9 ô thành 3 hàng · [KT Ch2.4] joinToString với lambda
    cells.chunked(Board.SIZE).joinToString("\n") { row -> row.joinToString(" ") { cellText(it) } }

/** Cho hai máy đấu với nhau và in từng nước. */
fun playOut(xHard: Boolean, oHard: Boolean, random: Random = Random(2026)): GameState {
    var state = GameState()
    // [KT Ch1.5] vòng while chạy tới khi ván kết thúc
    while (!state.isOver) {
        val hard = if (state.turn == Player.X) xHard else oHard
        val move = Ai.chooseMove(state.board, state.turn, hard, random)
        state = state.play(move)
        println("${state.turn.other()} đánh ô $move:\n${state.board.toText()}\n")
    }
    return state
}

fun main() {
    println("=== Máy khó (X) đấu máy khó (O): hai bên chơi hoàn hảo nên luôn hòa ===")
    val perfect = playOut(xHard = true, oHard = true) // [KT Ch2.2] named argument
    println("Kết quả: ${perfect.winner?.let { "$it thắng" } ?: "Hòa"}\n")

    println("=== Máy dễ (X) đấu máy khó (O), 100 ván ===")
    val random = Random(1)
    var easyWins = 0
    var hardWins = 0
    var draws = 0
    // [KT Ch1.5] repeat lặp 100 lần · when phân loại kết quả
    repeat(100) {
        var s = GameState()
        while (!s.isOver) {
            s = s.play(Ai.chooseMove(s.board, s.turn, hard = s.turn == Player.O, random = random))
        }
        when (s.winner) {
            Player.X -> easyWins++
            Player.O -> hardWins++
            null -> draws++
        }
    }
    println("Máy dễ thắng $easyWins · Hòa $draws · Máy khó thắng $hardWins")
}
