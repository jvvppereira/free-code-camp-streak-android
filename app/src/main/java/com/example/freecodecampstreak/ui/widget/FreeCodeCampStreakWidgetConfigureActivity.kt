package com.example.freecodecampstreak.ui.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import com.example.freecodecampstreak.R
import com.example.freecodecampstreak.databinding.FreeCodeCampStreakWidgetConfigureBinding
import androidx.core.content.edit

/**
 * The configuration screen for the [FreeCodeCampStreakWidget] AppWidget.
 */
class FreeCodeCampStreakWidgetConfigureActivity : Activity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var appWidgetText: EditText
    @RequiresApi(Build.VERSION_CODES.O)
    private var onClickListener = View.OnClickListener {
        val context = this@FreeCodeCampStreakWidgetConfigureActivity

        val widgetText = appWidgetText.text.toString()
        saveTitlePref(context, appWidgetId, widgetText)

        val appWidgetManager = AppWidgetManager.getInstance(context)
        updateAppWidget(context, appWidgetManager, appWidgetId)

        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }
    private lateinit var binding: FreeCodeCampStreakWidgetConfigureBinding

    @RequiresApi(Build.VERSION_CODES.O)
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        setResult(RESULT_CANCELED)

        binding = FreeCodeCampStreakWidgetConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appWidgetText = binding.appwidgetText
        binding.addButton.setOnClickListener(onClickListener)

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        appWidgetText.setText(
            loadTitlePref(
                this@FreeCodeCampStreakWidgetConfigureActivity,
                appWidgetId
            )
        )
    }

}

private const val PREFS_NAME = "com.example.freecodecampstreak.ui.widget.FreeCodeCampStreakWidget.UserName"
private const val PREF_PREFIX_KEY = "appwidget_"

// Write the prefix to the SharedPreferences object for this widget
internal fun saveTitlePref(context: Context, appWidgetId: Int, text: String) {
    context.getSharedPreferences(PREFS_NAME, 0).edit {
        putString(PREF_PREFIX_KEY + appWidgetId, text)
    }
}

// Read the prefix from the SharedPreferences object for this widget.
// If there is no preference saved, get the default from a resource
internal fun loadTitlePref(context: Context, appWidgetId: Int): String {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    val titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, "username")
    return titleValue ?: context.getString(R.string.appwidget_text)
}

internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
    context.getSharedPreferences(PREFS_NAME, 0).edit {
        remove(PREF_PREFIX_KEY + appWidgetId)
    }
}