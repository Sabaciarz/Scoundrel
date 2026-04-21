package com.sabat.scoundrel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sabat.scoundrel.GameEngine.generateDeck

class GameViewModel : ViewModel() {

    var state by mutableStateOf(GameState())
        private set

    init {
        startGame()
    }

    fun startGame() {
        val newState = GameState(
            deck = generateDeck()
        )

        state = newState
        GameEngine.startRoom(state, false)
    }

    fun onCardClick(i: Int): String? {
        val (newState, msg) = GameEngine.onCardClicked(state, i)
        state = newState
        return msg
    }

    fun onEscape() {
        state = GameEngine.escape(state)

    }

    fun onWeaponClick() {
        state = GameEngine.toggleWeapon(state)
    }
}