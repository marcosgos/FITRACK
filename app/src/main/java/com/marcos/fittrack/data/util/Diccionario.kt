package com.marcos.fittrack.data.util

object Diccionario {

    private val mapa = mapOf(
        // Partes del cuerpo
        "chest" to "Pecho",
        "back" to "Espalda",
        "shoulders" to "Hombros",
        "upper arms" to "Brazos (bíceps/tríceps)",
        "lower arms" to "Antebrazos",
        "upper legs" to "Piernas (muslo)",
        "lower legs" to "Piernas (pantorrilla)",
        "waist" to "Abdomen",
        "cardio" to "Cardio",
        "neck" to "Cuello",
        // Equipamiento
        "body weight" to "Peso corporal",
        "dumbbell" to "Mancuerna",
        "barbell" to "Barra",
        "cable" to "Polea",
        "machine" to "Máquina",
        "kettlebell" to "Kettlebell",
        "resistance band" to "Banda elástica",
        "medicine ball" to "Balón medicinal",
        "exercise ball" to "Fitball",
        "ez barbell" to "Barra Z",
        "smith machine" to "Máquina Smith",
        "bands" to "Bandas",
        "leverage machine" to "Máquina de palanca",
        "sled machine" to "Máquina de trineo",
        "assisted" to "Asistido",
        "rope" to "Cuerda",
        "roller" to "Rodillo",
        "stability ball" to "Balón de estabilidad",
        "trap bar" to "Barra hexagonal",
        "wheel roller" to "Rueda abdominal",
        "olympic barbell" to "Barra olímpica",
        "elliptical machine" to "Elíptica",
        "stationary bike" to "Bicicleta estática",
        // Músculos
        "biceps" to "Bíceps",
        "triceps" to "Tríceps",
        "forearms" to "Antebrazos",
        "abductors" to "Abductores",
        "adductors" to "Aductores",
        "glutes" to "Glúteos",
        "hamstrings" to "Isquiotibiales",
        "quadriceps" to "Cuádriceps",
        "calves" to "Gemelos",
        "abdominals" to "Abdominales",
        "lats" to "Dorsales",
        "traps" to "Trapecios",
        "delts" to "Deltoides",
        "pectorals" to "Pectorales",
        "spine" to "Columna",
        // Categoría
        "strength" to "Fuerza",
        "stretching" to "Estiramiento",
        "plyometrics" to "Pliometría",
        "strongman" to "Strongman",
        "powerlifting" to "Halterofilia/Powerlifting"
    )

    fun traducir(texto: String?): String {
        if (texto.isNullOrBlank()) return ""
        return mapa[texto.trim().lowercase()] ?: texto.replaceFirstChar { it.uppercase() }
    }
}