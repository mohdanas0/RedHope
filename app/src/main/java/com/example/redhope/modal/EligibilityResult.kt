package com.example.redhope.modal

 sealed class EligibilityResult {
     object Eligible : EligibilityResult()
     data class NotEligible(val reason: String) : EligibilityResult()
}