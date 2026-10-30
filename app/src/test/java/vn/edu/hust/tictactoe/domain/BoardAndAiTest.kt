package vn.edu.hust.tictactoe.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/** Test bổ sung cho Board, Player và Ai (GameTest giữ nguyên các test ban đầu). */
class BoardAndAiTest {

    private fun play(vararg moves: Int) = moves.fold(GameState()) { s, m -> s.play(m) }

    /** Dựng bàn từ chuỗi 9 ký tự, ví dụ "XX..O....". */
    private fun board(text: String): Board =
        Board(text.map { if (it == 'X') Player.X else if (it == 'O') Player.O else null })

    // ---------- Xét thắng ----------

    @Test
    fun xWinsOnColumn() {
        val s = play(1, 0, 4, 2, 7) // X: 1, 4, 7
        assertEquals(Player.X, s.winner)
        assertEquals(listOf(1, 4, 7), s.winningLine)
    }

    @Test
    fun xWinsOnMainDiagonal() {
        val s = play(0, 1, 4, 2, 8) // X: 0, 4, 8
        assertEquals(Player.X, s.winner)
        assertEquals(listOf(0, 4, 8), s.winningLine)
    }

    @Test
    fun oWinsOnAntiDiagonal() {
        val s = play(0, 2, 1, 4, 8, 6) // O: 2, 4, 6
        assertEquals(Player.O, s.winner)
        assertEquals(listOf(2, 4, 6), s.winningLine)
    }

    @Test
    fun emptyBoardHasNoWinnerAndIsNotOver() {
        val s = GameState()
        assertNull(s.winner)
        assertNull(s.winningLine)
        assertFalse(s.isOver)
        assertEquals((0..8).toList(), s.board.emptyCells())
    }

    @Test
    fun allEightLinesAreDetected() {
        val lines = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6),
        )
        for (line in lines) {
            val b = line.fold(Board()) { acc, i -> acc.place(i, Player.O) }
            assertEquals(Player.O, b.winner())
            assertEquals(line, b.winningLine())
        }
    }

    // ---------- Lượt đi và bất biến ----------

    @Test
    fun turnAlternatesBetweenXAndO() {
        assertEquals(Player.O, play(4).turn)
        assertEquals(Player.X, play(4, 0).turn)
        assertEquals(Player.O, Player.X.other())
        assertEquals(Player.X, Player.O.other())
    }

    @Test
    fun placeDoesNotChangeOriginalBoard() {
        val before = Board()
        val after = before.place(4, Player.X)
        assertNull(before.cells[4])      // bàn cũ giữ nguyên
        assertEquals(Player.X, after.cells[4])
        assertNotEquals(before, after)
    }

    @Test(expected = IllegalArgumentException::class)
    fun placeOnTakenCellThrows() {
        Board().place(0, Player.X).place(0, Player.O)
    }

    // ---------- Máy chơi ----------

    @Test
    fun hardAiBlocksOpponent() {
        // X X .
        // . O .     O phải đánh ô 2 để chặn
        // . . .
        assertEquals(2, Ai.chooseMove(board("XX..O...."), Player.O, hard = true))
    }

    @Test
    fun hardAiPrefersWinningOverBlocking() {
        // X X .
        // O O .     O thắng ngay ở ô 5, không cần chặn ô 2
        // X . .
        assertEquals(5, Ai.chooseMove(board("XX.OO.X.."), Player.O, hard = true))
    }

    @Test
    fun hardAiAnswersCornersWithAnEdge() {
        // X . .
        // . O .     O đánh góc thì X tạo hai đường dọa cùng lúc; phải đánh cạnh
        // . . X
        val move = Ai.chooseMove(board("X...O...X"), Player.O, hard = true)
        assertTrue(move in listOf(1, 3, 5, 7))
    }

    @Test
    fun hardAiNeverLosesWhenComputerStarts() {
        // Như hardAiNeverLoses nhưng máy (O) đi trước: người X thử mọi chuỗi nước đáp lại
        fun explore(s: GameState) {
            if (s.isOver) {
                assertNotEquals(Player.X, s.winner)
                return
            }
            if (s.turn == Player.X) {
                s.board.emptyCells().forEach { explore(s.play(it)) }
            } else {
                explore(s.play(Ai.chooseMove(s.board, Player.O, hard = true)))
            }
        }
        explore(GameState(turn = Player.O))
    }

    @Test
    fun twoHardAisAlwaysDraw() {
        repeat(5) { seed ->
            val random = Random(seed)
            var s = GameState()
            while (!s.isOver) s = s.play(Ai.chooseMove(s.board, s.turn, hard = true, random = random))
            assertTrue(s.isDraw)
        }
    }

    @Test
    fun easyAiPicksEmptyCellAndIsReproducibleWithSeed() {
        val b = play(4, 0).board
        val first = Ai.chooseMove(b, Player.X, hard = false, random = Random(42))
        val again = Ai.chooseMove(b, Player.X, hard = false, random = Random(42))
        assertEquals(first, again)          // cùng seed thì cùng nước
        assertTrue(first in b.emptyCells()) // chỉ đánh vào ô trống
    }
}
