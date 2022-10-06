package com.patrykandpatryk.liftapp.domain.routine

import com.patrykandpatryk.liftapp.domain.exercise.Exercise

class RoutineWithExercises(
    val id: Long,
    val name: String,
    val exercises: List<Exercise>,
)
