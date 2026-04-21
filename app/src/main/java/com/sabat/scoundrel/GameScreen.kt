package com.sabat.scoundrel

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun GameScreen(vm: GameViewModel) {

    val state by vm::state
    var message by remember { mutableStateOf<String?>(null) }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        // 🔵 LEWA KOLUMNA (HP + DECK)
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text("HP: ${state.hp}")

            Spacer(modifier = Modifier.height(12.dp))

            Image(
                painter = painterResource(R.drawable.deck),
                contentDescription = "Deck",
                modifier = Modifier.size(90.dp)
            )

            Text("x${state.deck.size}")
        }

        // 🟢 ŚRODEK (plansza)
        Column(
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // rząd kart
            for (i in 0 until 4) {
                val card = state.room.getOrNull(i)

                CardSlot(
                    cardId = card,
                    onClick = { vm.onCardClick(i) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // dół: W + LK
            Row {

                // 🟣 BROŃ
                CardSlot(
                    cardId = state.weapon,
                    onClick = { vm.onWeaponClick() },
                    highlighted = state.weaponActive
                )

                Spacer(modifier = Modifier.width(24.dp))

                // ⚫ LK (TERAZ KARTA!)
                CardSlot(
                    cardId = state.lastKilledMonster,
                    onClick = null
                )
            }
        }

        // 🔴 PRAWA KOLUMNA (przyciski)
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {

            Button(
                onClick = { vm.onEscape() },
                enabled = state.escapeAvailable && state.room.count { it != null } == 4
            ) {
                Text("Escape")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = { vm.startGame() }) {
                Text("Restart")
            }
        }
    }

    // 📢 komunikat (np. nie możesz zaatakować)
    if (message != null) {
        AlertDialog(
            onDismissRequest = { message = null },
            confirmButton = {
                Button(onClick = { message = null }) {
                    Text("OK")
                }
            },
            text = { Text(message!!) }
        )
    }

    // 🏁 koniec gry
    if (state.gameOver) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Button(onClick = { vm.startGame() }) {
                    Text("Restart")
                }
            },
            text = {
                if (state.win)
                    Text("WYGRANA\nHP: ${state.hp}")
                else
                    Text("PRZEGRANA\nDeck: ${state.deck.size}")
            }
        )
    }
}

@Composable
fun CardImage(cardId: Int?) {
    if (cardId == null) return

    val context = LocalContext.current
    val name = getCardDrawableName(cardId)

    val resId = context.resources.getIdentifier(
        name,
        "drawable",
        context.packageName
    )

    if (resId != 0) {
        Image(
            painter = painterResource(resId),
            contentDescription = name,
            modifier = Modifier.fillMaxSize()
        )
    }
}

fun getCardDrawableName(id: Int): String {
    return when (id) {
        in 1..10 -> "h%02d".format(id)
        in 11..20 -> "d%02d".format(id)
        in 21..33 -> "w%02d".format(id)
        in 41..53 -> "t%02d".format(id)
        else -> error("Nieznana karta")
    }
}

@Composable
fun DeckView(size: Int) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Image(
            painter = painterResource(R.drawable.deck),
            contentDescription = "Deck",
            modifier = Modifier.size(80.dp)
        )

        Text("x$size")
    }
}

@Composable
fun LastKilledView(power: Int?) {

    Box(
        modifier = Modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        if (power != null) {
            Text("LK\n$power")
        } else {
            Text("LK")
        }
    }
}

@Composable
fun CardSlot(
    cardId: Int?,
    onClick: (() -> Unit)? = null,
    highlighted: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(90.dp)
            .padding(4.dp)
            .then(
                if (highlighted)
                    Modifier.border(3.dp, androidx.compose.ui.graphics.Color.Green)
                else Modifier
            )
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            }
    ) {
        CardImage(cardId)
    }
}
