package com.example.a3navalhas

data class Agendamento(
    val id: String,
    val nome_cliente: String,
    val telefone_cliente: String,
    val data_agendamento: String,
    val hora_agendamento: String
    // Adicione outros campos se a sua API retornar mais dados
)
