@file:Suppress("Indentation")
package pl.patrykgoworowski.mintlift.functionality.database.measurement

import androidx.room.DatabaseView
import androidx.room.Embedded

@DatabaseView(
    value = "SELECT measurement.*, latest_entry.* FROM measurement " +
            "LEFT JOIN " +
            "(SELECT * FROM measurement_entry as entry WHERE entry.timestamp in" +
            "(SELECT MAX(E.timestamp) FROM measurement_entry as E GROUP BY E.parent_id) " +
            "ORDER BY entry.entry_id DESC) " +
            "as latest_entry on measurement.id = latest_entry.parent_id",
    viewName = "measurement_with_latest_entry",
)
class MeasurementWithLatestEntryView(
    @Embedded val measurement: MeasurementEntity,
    @Embedded val entry: MeasurementEntryEntity?,
)
