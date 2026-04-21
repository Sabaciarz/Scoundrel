package com.sabat.scoundrel

object GameEngine {
    fun startRoom(state: GameState, skipped: Boolean) {

        if (!skipped) state.escapeAvailable = true

        for (i in 0 until 4) {
            if (state.room[i] == null) {

                if (state.deck.isEmpty()) {
                    state.win = true
                    state.gameOver = true
                    return
                }

                state.room[i] = state.deck.removeAt(0)
            }
        }
    }

    fun onCardClicked(state: GameState, index: Int): Pair<GameState, String?> {
        if (state.gameOver) return Pair(state, null)

        val newState = state.copy(
            room = state.room.toMutableList(),
            deck = state.deck.toMutableList()
        )

        val cardId = newState.room[index] ?: return Pair(newState, null)

        when (getCardType(cardId)) {

            CardType.POTION -> {
                val power = getCardPower(cardId)
                newState.hp = (newState.hp + power).coerceAtMost(20)
            }

            CardType.WEAPON -> {
                newState.weapon = cardId
                newState.weaponActive = false
                newState.lastKilledMonster = null
            }

            CardType.MONSTER -> {


                val monsterPower = getCardPower(cardId)
                var damage = monsterPower

                if (newState.weaponActive && newState.weapon != null) {

                    val last = newState.lastKilledMonster
                    if (last != null && monsterPower >= getCardPower(last)) {
                        return Pair(state, "Potwór za silny dla tej broni")
                    }

                    val weaponPower = getCardPower(newState.weapon!!)
                    damage -= weaponPower
                    if (damage < 0) damage = 0

                    // 👉 TYLKO TU ustawiamy LK
                    newState.lastKilledMonster = cardId
                }

                newState.hp -= damage

                if (newState.hp <= 0) {
                    newState.hp = 0
                    newState.gameOver = true
                    newState.win = false
                }
            }
        }

        newState.room[index] = null

        if (newState.room.count { it != null } == 1) {
            startRoom(newState, skipped = false)
        }

        return Pair(newState, null)
    }

    fun fightMonster(state: GameState, cardId: Int): String? {
        val monsterPower = getCardPower(cardId)
        var damage = monsterPower

        if (state.weaponActive && state.weapon != null) {

            val last = state.lastKilledMonster
            if (last != null && monsterPower >= last) {
                return "Nie możesz pokonać silniejszego potwora tą bronią!"
            }

            val weaponPower = getCardPower(state.weapon!!)
            damage -= weaponPower
            if (damage < 0) damage = 0

            state.lastKilledMonster = cardId
        }

        state.hp -= damage
        checkGameOver(state)
        if (state.hp <= 0) {
            state.gameOver = true
        }

        return null
    }

    fun toggleWeapon(state: GameState): GameState {

        if (state.weapon == null) return state

        val newState = state.copy()

        newState.weaponActive = !state.weaponActive

        return newState
    }

    fun escape(state: GameState): GameState {

        // ❌ nie można jeśli nie pełny pokój
        if (state.room.count { it != null } != 4) return state

        if (!state.escapeAvailable) return state

        val newState = state.copy(
            room = state.room.toMutableList(),
            deck = state.deck.toMutableList()
        )

        newState.escapeAvailable = false

        val remaining = newState.room.filterNotNull().shuffled()
        newState.deck.addAll(remaining)

        newState.room = MutableList(4) { null }

        startRoom(newState, skipped = true)


        return newState
    }
    fun generateDeck(): MutableList<Int> {
        val deck = mutableListOf<Int>()

        // Mikstury 1–10
        deck.addAll(1..10)

        // Bronie 11–20
        deck.addAll(11..20)

        // Potwory
        deck.addAll(21..33)
        deck.addAll(41..53)

        deck.shuffle()
        return deck
    }
    fun checkGameOver(state: GameState) {
        if (state.hp <= 0) {
            state.hp = 0
            state.gameOver = true
            state.win = false
        }
    }
}

