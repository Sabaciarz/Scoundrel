package com.sabat.scoundrel

enum class CardType {
    POTION, WEAPON, MONSTER
}

fun getCardType(id: Int): CardType = when (id) {
    in 1..10 -> CardType.POTION
    in 11..20 -> CardType.WEAPON
    in 21..33, in 41..53 -> CardType.MONSTER
    else -> error("Nieznana karta: $id")
}

fun getCardPower(id: Int): Int = when (id) {
    in 1..10 -> id
    in 11..20 -> id - 10
    in 21..33 -> id - 20
    in 41..53 -> id - 40
    else -> error("Nieznana karta: $id")
}