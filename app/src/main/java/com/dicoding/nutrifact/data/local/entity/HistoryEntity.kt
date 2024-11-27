package com.dicoding.nutrifact.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "result_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int ?= null,

    @ColumnInfo(name = "merk")
    var merk: String ?= null,

    @ColumnInfo(name = "varian")
    var varian: String ?= null,

    @ColumnInfo(name = "sugar")
    var sugar: String ?= null,

    @ColumnInfo(name = "fat")
    var fat: String ?= null,

    @ColumnInfo(name = "healthGrade")
    var healthGrade: String ?= null,
)