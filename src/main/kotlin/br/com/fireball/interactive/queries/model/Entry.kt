package br.com.fireball.interactive.queries.model

import java.time.LocalDate

data class Entry(
        val id: Long,
        val moipAccount: String,
        val amount: Long,
        val settledAt: LocalDate
)