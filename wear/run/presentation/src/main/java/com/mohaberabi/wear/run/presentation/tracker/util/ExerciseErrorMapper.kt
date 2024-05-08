package com.mohaberabi.wear.run.presentation.tracker.util

import com.mohaberabi.core.presentation.ui.util.UiText
import com.mohaberabi.wear.run.domain.repository.ExerciseError
import com.mohaberabi.wear.run.presentation.R


fun ExerciseError.toUiText(): UiText? {

    return when (this) {
        ExerciseError.TRACKING_NOT_SUPPORTED -> null
        ExerciseError.ONGOING_OWN_EXERCISE -> UiText.StringResource(R.string.error_own_exercise)
        ExerciseError.ONGOING_OTHER_EXERCISE -> UiText.StringResource(R.string.error_ongoing_ex)
        ExerciseError.EXERCISE_ALREADY_ENDED -> UiText.StringResource(R.string.error_exercise_ended)
        ExerciseError.UNKNOWN -> UiText.StringResource(R.string.unknown_error)
    }
}