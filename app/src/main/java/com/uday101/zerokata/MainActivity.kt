package com.uday101.zerokata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.uday101.zerokata.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity()
{
    enum class Turn
    {
        NOUGHT,
        CROSS
    }

    private var firstTurn = Turn.CROSS
    private var currentTurn = Turn.CROSS

    private var crossesScore = 0
    private var noughtsScore = 0

    private var boardList = mutableListOf<Button>()

    // Why this not work
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.smart.setOnClickListener {
            smartmove(currentTurn)
        }
        initBoard()
    }

    private fun initBoard()
    {
        boardList.add(binding.a1)
        boardList.add(binding.a2)
        boardList.add(binding.a3)
        boardList.add(binding.b1)
        boardList.add(binding.b2)
        boardList.add(binding.b3)
        boardList.add(binding.c1)
        boardList.add(binding.c2)
        boardList.add(binding.c3)
    }

    fun boardTapped(view: View)
    {
        if(view !is Button)
            return
        addToBoard(view)

        if(checkForVictory(NOUGHT))
        {
            noughtsScore++
            result("Noughts Win!")
        }
        else if(checkForVictory(CROSS))
        {
            crossesScore++
            result("Crosses Win!")
        }

        if(fullBoard())
        {
            result("Draw")
        }

    }
    fun winning(board : Array<String>, player : String): Boolean {
        return (board[0] == player && board[1] == player && board[2] == player) ||
                (board[3] == player && board[4] == player && board[5] == player) ||
                (board[6] == player && board[7] == player && board[8] == player) ||
                (board[0] == player && board[3] == player && board[6] == player) ||
                (board[1] == player && board[4] == player && board[7] == player) ||
                (board[2] == player && board[5] == player && board[8] == player) ||
                (board[0] == player && board[4] == player && board[8] == player) ||
                (board[2] == player && board[4] == player && board[6] == player)
    }

    fun emptyIndexies(board : Array<String>) : List<Int> {
        var emptyindices = mutableListOf<Int>()
        for (i in 0..8) {
            if (board[i] == "") {
                emptyindices.add(i)
            }
        }
        return emptyindices
    }

    data class Move(
        var index: String? = null,
        var score: Int = 0
    )

    fun minimax(newBoard: Array<String>, player: String, aiPlayer : String, huPlayer : String): Move {
        val availSpots = emptyIndexies(newBoard)
        if (winning(newBoard, huPlayer)) {
            return Move(score = -10)
        } else if (winning(newBoard, aiPlayer)) {
            return Move(score = 10)
        } else if (availSpots.isEmpty()) {
            return Move(score = 0)
        }
        val moves = mutableListOf<Move>()

        for (i in availSpots.indices) {
            val move = Move()
            move.index = newBoard[availSpots[i]]

            newBoard[availSpots[i]] = player

            val result = if (player == aiPlayer) {
                minimax(newBoard, huPlayer, aiPlayer, huPlayer)
            } else {
                minimax(newBoard, aiPlayer, aiPlayer, huPlayer)
            }
            move.score = result.score
            newBoard[availSpots[i]] = move.index!!
            moves.add(move)
        }

        var bestMove = -1
        if (player == aiPlayer) {
            var bestScore = -10000
            for (i in moves.indices) {
                if (moves[i].score > bestScore) {
                    bestScore = moves[i].score
                    bestMove = i
                }
            }
        } else {
            var bestScore = 10000
            for (i in moves.indices) {
                if (moves[i].score < bestScore) {
                    bestScore = moves[i].score
                    bestMove = i
                }
            }
        }

        // Return the chosen move (object) from the moves array
        return moves[bestMove]

    }

    private fun smartmove(currentTurn : Turn)
    {
        var aiplayer = "O"
        var huplayer = "X"
        var board = arrayOf(binding.a1.text as String,binding.a2.text as String,binding.a3.text as String,
                            binding.b1.text as String,binding.b2.text as String,binding.b3.text as String,
                            binding.c1.text as String,binding.c2.text as String,binding.c3.text as String,)
        if (currentTurn == Turn.CROSS)
        {
            var aiplayer = "X"
            var huplayer = "O"
        }
        else
        {
            var aiplayer = "O"
            var huplayer = "X"
        }

        var bestmove = 1
        Log.i("idk", minimax(board, aiplayer, aiplayer, huplayer).index.toString())

        when (bestmove) {
            1 -> addToBoard(binding.a2)
            2 -> addToBoard(binding.a3)
            3 -> addToBoard(binding.b1)
            4 -> addToBoard(binding.b2)
            5 -> addToBoard(binding.b3)
            6 -> addToBoard(binding.c1)
            7 -> addToBoard(binding.c2)
            8 -> addToBoard(binding.c3)
            0 -> addToBoard(binding.a1)
        }
    }

    private fun checkForVictory(s: String): Boolean
    {
        //Horizontal Victory
        if(match(binding.a1,s) && match(binding.a2,s) && match(binding.a3,s))
            return true
        if(match(binding.b1,s) && match(binding.b2,s) && match(binding.b3,s))
            return true
        if(match(binding.c1,s) && match(binding.c2,s) && match(binding.c3,s))
            return true

        //Vertical Victory
        if(match(binding.a1,s) && match(binding.b1,s) && match(binding.c1,s))
            return true
        if(match(binding.a2,s) && match(binding.b2,s) && match(binding.c2,s))
            return true
        if(match(binding.a3,s) && match(binding.b3,s) && match(binding.c3,s))
            return true

        //Diagonal Victory
        if(match(binding.a1,s) && match(binding.b2,s) && match(binding.c3,s))
            return true
        if(match(binding.a3,s) && match(binding.b2,s) && match(binding.c1,s))
            return true

        return false
    }

    private fun match(button: Button, symbol : String): Boolean = button.text == symbol

    private fun result(title: String)
    {
        val message = "\nNoughts $noughtsScore\n\nCrosses $crossesScore"
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Reset")
            { _,_ ->
                resetBoard()
            }
            .setCancelable(false)
            .show()
    }

    private fun resetBoard()
    {
        for(button in boardList)
        {
            button.text = ""
        }

        if(firstTurn == Turn.NOUGHT)
            firstTurn = Turn.CROSS
        else if(firstTurn == Turn.CROSS)
            firstTurn = Turn.NOUGHT

        currentTurn = firstTurn
        setTurnLabel()
    }

    private fun fullBoard(): Boolean
    {
        for(button in boardList)
        {
            if(button.text == "")
                return false
        }
        return true
    }

    private fun addToBoard(button: Button)
    {
        if(button.text != "")
            return

        if(currentTurn == Turn.NOUGHT)
        {
            button.text = NOUGHT
            currentTurn = Turn.CROSS
        }
        else if(currentTurn == Turn.CROSS)
        {
            button.text = CROSS
            currentTurn = Turn.NOUGHT
        }
        setTurnLabel()
    }

    private fun setTurnLabel()
    {
        var turnText = ""
        if(currentTurn == Turn.CROSS)
            turnText = "Turn $CROSS"
        else if(currentTurn == Turn.NOUGHT)
            turnText = "Turn $NOUGHT"

        binding.turnTV.text = turnText
    }

    companion object
    {
        const val NOUGHT = "O"
        const val CROSS = "X"
    }

}