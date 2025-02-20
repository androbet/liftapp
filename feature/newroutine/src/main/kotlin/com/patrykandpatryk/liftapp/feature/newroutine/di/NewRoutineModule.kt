package com.patrykandpatryk.liftapp.feature.newroutine.di

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.validation.NonEmptyCollectionValidator
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.validation.Validator
import com.patrykandpatryk.liftapp.feature.newroutine.mapper.ExerciseToItemMapper
import com.patrykandpatryk.liftapp.feature.newroutine.ui.ExerciseItem
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface NewRoutineModule {

    @Binds
    fun bindMapper(mapper: ExerciseToItemMapper): Mapper<Exercise, ExerciseItem>

    @Binds
    fun bindNonEmptyCollectionValidator(
        validator: NonEmptyCollectionValidator<ExerciseItem, List<ExerciseItem>>,
    ): Validator<List<ExerciseItem>>

    companion object {

        @Provides
        @RoutineId
        fun provideRoutineId(savedStateHandle: SavedStateHandle): Long =
            savedStateHandle.get<Long>(Routes.ARG_ID) ?: ID_NOT_SET
    }
}
