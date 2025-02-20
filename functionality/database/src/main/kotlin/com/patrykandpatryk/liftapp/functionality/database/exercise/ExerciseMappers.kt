package com.patrykandpatryk.liftapp.functionality.database.exercise

import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.model.NameResolver
import javax.inject.Inject

class ExerciseEntityToDomainMapper @Inject constructor(
    private val nameResolver: NameResolver,
) : Mapper<ExerciseEntity, Exercise> {

    override suspend fun map(input: ExerciseEntity): Exercise =
        Exercise(
            id = input.id,
            displayName = nameResolver.getResolvedString(input.name),
            name = input.name,
            exerciseType = input.exerciseType,
            mainMuscles = input.mainMuscles,
            secondaryMuscles = input.secondaryMuscles,
            tertiaryMuscles = input.tertiaryMuscles,
        )
}

class ExerciseInsertToEntityMapper @Inject constructor() : Mapper<Exercise.Insert, ExerciseEntity> {

    override suspend fun map(input: Exercise.Insert): ExerciseEntity =
        ExerciseEntity(
            name = input.name,
            exerciseType = input.exerciseType,
            mainMuscles = input.mainMuscles,
            secondaryMuscles = input.secondaryMuscles,
            tertiaryMuscles = input.tertiaryMuscles,
        )
}

class ExerciseUpdateToEntityMapper @Inject constructor() : Mapper<Exercise.Update, ExerciseEntity.Update> {

    override suspend fun map(input: Exercise.Update): ExerciseEntity.Update =
        ExerciseEntity.Update(
            id = input.id,
            name = input.name,
            mainMuscles = input.mainMuscles,
            secondaryMuscles = input.secondaryMuscles,
            tertiaryMuscles = input.tertiaryMuscles,
        )
}
