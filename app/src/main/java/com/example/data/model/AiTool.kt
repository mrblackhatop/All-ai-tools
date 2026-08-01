package com.example.data.model

data class AiTool(
    val id: String,
    val categoryId: String,
    val titleEn: String,
    val titleBn: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val iconName: String,
    val systemInstructionEn: String,
    val systemInstructionBn: String,
    val promptPlaceholderEn: String,
    val promptPlaceholderBn: String,
    val promptExamplesEn: List<String>,
    val promptExamplesBn: List<String>,
    val tags: List<String> = listOf("popular")
)
