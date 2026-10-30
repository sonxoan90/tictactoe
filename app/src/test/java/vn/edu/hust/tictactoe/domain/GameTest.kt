package vn.edu.hust.tictactoe.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameTest {

    private fun play(vararg moves: Int) = moves.fold(GameState()) { s, m -> s.play(m) }

    @Test
    fun xWinsOnRow() {
        val s = play(0, 3, 1, 4, 2)
        assertEquals(Player.X, s.winner)
        assertEquals(listOf(0, 1, 2), s.winningLine)
        assertTrue(s.isOver)
    }

    @Test
    fun drawWhenFullWithoutWinner() {
        val s = play(0, 1, 2, 4, 3, 5, 7, 6, 8)
        assertNull(s.winner)
        assertTrue(s.isDraw)
    }

    @Test
    fun cannotPlayOnTakenCellOrAfterGameOver() {
        val s = play(0)
        assertTrue(s.play(0) === s)
        val won = play(0, 3, 1, 4, 2)
        assertTrue(won.play(8) === won)
    }

    @Test
    fun easyAiTakesWinningMove() {
        val board = Board().place(0, Player.O).place(1, Player.O).place(4, Player.X).place(8, Player.X)
        assertEquals(2, Ai.chooseMove(board, Player.O, hard = false))
    }

    @Test
    fun hardAiNeverLoses() {
        // Người chơi X thử mọi chuỗi nước đi; máy O (khó) phải không bao giờ thua
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
        explore(GameState())
    }
}
