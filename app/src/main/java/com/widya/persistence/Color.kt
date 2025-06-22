package com.widya.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Color(
    val name: String,
    val hex: String,
    @PrimaryKey(autoGenerate = true) val val_id:Int=0,
)