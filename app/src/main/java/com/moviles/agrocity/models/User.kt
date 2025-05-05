package com.moviles.agrocity.models

data class User(
    val userId: Int?,
    val name: String?,
    val firstName: String?,
    val surname: String?,
    val age: Int?,
    val telephone: String?,
    val email: String,
    val password: String,
    val createdAt: String?, // O usa java.time.LocalDateTime si lo estás parseando

    val comments: List<Comment> = emptyList(),
    val gardens: List<Garden> = emptyList(),
    val notifications: List<Notification> = emptyList(),
    val publications: List<Publication> = emptyList(),
    val reminders: List<Reminder> = emptyList()
)
