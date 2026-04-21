package com.sabat.scoundrel

data class GameState(
    var deck: MutableList<Int> = mutableListOf(),
    var room: MutableList<Int?> = MutableList(4) { null },

    var hp: Int = 20,
    var maxHp: Int = 20,

    var weapon: Int? = null,
    var weaponActive: Boolean = false,

    var lastKilledMonster: Int? = null,

    var escapeAvailable: Boolean = true,
    var gameOver: Boolean = false,
    var win: Boolean = false

)