package com.patrykandpatry.liftapp.data.unit

import com.patrykandpatryk.liftapp.domain.extension.getTypeErrorMessage
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.repository.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.MediumDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.PercentageUnit
import com.patrykandpatryk.liftapp.domain.unit.ShortDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.UnitConverter
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import com.patrykmichalik.opto.core.first
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UnitConverterImpl @Inject constructor(
    private val formatter: Formatter,
    private val stringProvider: StringProvider,
    private val preferences: PreferenceRepository,
) : UnitConverter {

    override suspend fun convertToPreferredUnit(from: LongDistanceUnit, value: Float): Float =
        when (getPreferredLongDistanceUnit()) {
            LongDistanceUnit.Kilometer -> from.toKilometers(value)
            LongDistanceUnit.Mile -> from.toMiles(value)
        }

    override suspend fun convertToPreferredUnit(from: MediumDistanceUnit, value: Float): Float =
        when (getPreferredMediumDistanceUnit()) {
            MediumDistanceUnit.Meter -> from.toMeters(value)
            MediumDistanceUnit.Foot -> from.toFeet(value)
        }

    override suspend fun convertToPreferredUnit(from: ShortDistanceUnit, value: Float): Float =
        when (getPreferredShortDistanceUnit()) {
            ShortDistanceUnit.Centimeter -> from.toCentimeters(value)
            ShortDistanceUnit.Inch -> from.toInch(value)
            else -> error(getTypeErrorMessage(unit = from))
        }

    override suspend fun convertToPreferredUnit(from: MassUnit, value: Float): Float =
        when (getPreferredMassUnit()) {
            MassUnit.Kilograms -> from.toKilograms(value)
            MassUnit.Pounds -> from.toPounds(value)
        }

    override suspend fun convertToPreferredUnitAndFormat(from: ValueUnit, vararg values: Float): String {

        val convertedValues = when (from) {
            is MassUnit -> values.map { convertToPreferredUnit(from = from, value = it) }.toTypedArray()
            is LongDistanceUnit -> values.map { convertToPreferredUnit(from = from, value = it) }.toTypedArray()
            is MediumDistanceUnit -> values.map { convertToPreferredUnit(from = from, value = it) }.toTypedArray()
            is ShortDistanceUnit -> values.map { convertToPreferredUnit(from = from, value = it) }.toTypedArray()
            is PercentageUnit -> values.toTypedArray()
            else -> error(getTypeErrorMessage(unit = from))
        }

        val postfix = when (from) {
            is MassUnit -> stringProvider.getDisplayUnit(getPreferredMassUnit())
            is LongDistanceUnit -> stringProvider.getDisplayUnit(getPreferredLongDistanceUnit())
            is MediumDistanceUnit -> stringProvider.getDisplayUnit(getPreferredMediumDistanceUnit())
            is ShortDistanceUnit -> stringProvider.getDisplayUnit(getPreferredShortDistanceUnit())
            is PercentageUnit -> stringProvider.getDisplayUnit(PercentageUnit)
            else -> error(getTypeErrorMessage(unit = from))
        }

        return formatter.formatNumber(
            *convertedValues,
            format = Formatter.NumberFormat.Decimal,
            postfix = postfix,
        )
    }

    private suspend fun getPreferredLongDistanceUnit(): LongDistanceUnit =
        preferences.longDistanceUnit.first()

    private suspend fun getPreferredMediumDistanceUnit(): MediumDistanceUnit =
        preferences.mediumDistanceUnit.first()

    private suspend fun getPreferredShortDistanceUnit(): ShortDistanceUnit =
        preferences.shortDistanceUnit.first()

    private suspend fun getPreferredMassUnit(): MassUnit =
        preferences.massUnit.first()
}
