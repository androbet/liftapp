package com.patrykandpatryk.liftapp.domain.di

import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.format.FormatterImpl
import com.patrykandpatryk.liftapp.domain.serialization.PolymorphicEnumSerializer
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.MediumDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.PercentageUnit
import com.patrykandpatryk.liftapp.domain.unit.ShortDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Module
@InstallIn(SingletonComponent::class)
interface DomainModule {

    @Binds
    fun bindFormatter(formatter: FormatterImpl): Formatter

    companion object {

        @Provides
        fun provideJson(): Json = Json {
            serializersModule = SerializersModule {
                polymorphic(ValueUnit::class) {
                    subclass(MassUnit::class, PolymorphicEnumSerializer(MassUnit.serializer()))
                    subclass(LongDistanceUnit::class, PolymorphicEnumSerializer(LongDistanceUnit.serializer()))
                    subclass(MediumDistanceUnit::class, PolymorphicEnumSerializer(MediumDistanceUnit.serializer()))
                    subclass(ShortDistanceUnit::class, PolymorphicEnumSerializer(ShortDistanceUnit.serializer()))
                    subclass(PercentageUnit::class)
                }
            }
        }
    }
}
