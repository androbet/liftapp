package com.patrykandpatryk.liftapp.domain.routine

import kotlinx.coroutines.flow.Flow

interface RoutineRepository {

    fun getRoutinesWithNames(): Flow<List<RoutineWithExerciseNames>>

    fun getRoutineWithExercises(routineId: Long): Flow<RoutineWithExercises?>
}
