package com.maeldev.conquest.data.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Guards the upgrade path. A schema change that bumps [CosplayDatabase]'s version without a
 * matching migration crashes on first database access for anyone upgrading in place — which is
 * exactly how 12 -> 14 shipped broken. These tests fail in CI instead.
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    @get:Rule
    val helper =
        MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            CosplayDatabase::class.java,
        )

    /**
     * The exported schema for the current version must be committed, otherwise no future migration
     * can be written or validated against it. Schemas 12 and 13 were never exported, which is why
     * [MIGRATION_12_14] had to be reconstructed from the entity history by hand.
     */
    @Test
    fun currentSchemaIsExported() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val exported = context.assets.list(SCHEMA_DIR).orEmpty().toSet()
        assertTrue(
            "Schema $CURRENT_VERSION.json is missing from app/schemas — commit it.",
            "$CURRENT_VERSION.json" in exported,
        )
    }

    /**
     * Walks every exported schema up to the current version, applying the registered migrations and
     * validating the result against the expected schema at each step.
     */
    @Test
    fun migratesFromEveryExportedSchema() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val versions =
            context.assets.list(SCHEMA_DIR).orEmpty()
                .mapNotNull { it.removeSuffix(".json").toIntOrNull() }
                .filter { it < CURRENT_VERSION }
                .sorted()

        for (from in versions) {
            helper.createDatabase(TEST_DB, from).close()
            helper.runMigrationsAndValidate(TEST_DB, CURRENT_VERSION, true, *ALL_MIGRATIONS).close()
        }
    }

    private companion object {
        const val TEST_DB = "migration-test"
        const val CURRENT_VERSION = 14
        const val SCHEMA_DIR = "com.maeldev.conquest.data.database.CosplayDatabase"
    }
}
