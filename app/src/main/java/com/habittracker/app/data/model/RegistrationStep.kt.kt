package com.habittracker.app.data.model

sealed class RegistrationStep {
    object MainQuestion : RegistrationStep()
    object TimeBlockQuestion : RegistrationStep()
    object TargetTimeQuestion : RegistrationStep()
    object AllDone : RegistrationStep()
}