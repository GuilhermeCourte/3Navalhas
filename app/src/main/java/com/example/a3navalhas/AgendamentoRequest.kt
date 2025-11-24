package com.example.a3navalhas

data class AgendamentoRequest(
    val nome_cliente: String,
    val telefone_cliente: String,
    val data_agendamento: String,
    val hora_agendamento: String
)
