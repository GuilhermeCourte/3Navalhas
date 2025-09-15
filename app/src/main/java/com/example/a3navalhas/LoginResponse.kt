package com.example.a3navalhas

data class LoginResponse(
    val usuarioId: Int,
    val usuarioNome: String,
    val usuarioEmail: String,
    val usuarioSenha: String,
    val usuarioCpf: String
)