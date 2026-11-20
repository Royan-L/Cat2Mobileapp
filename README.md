# Real Estate Management App (CAT 2)

## Project Overview
An Android app (Kotlin, Jetpack Compose) for property managers and landlords. It replaces paper and spreadsheet records with one mobile app for properties, tenants, rent payments and leases, with a live dashboard summary.

## Features
*   **Dashboard:** properties, occupancy, paid revenue and active leases at a glance
*   **Properties:** add, view, edit status and delete; search and filter by status
*   **Tenants:** add, view, change status and delete; search by name, email or phone
*   **Rent Collection:** track Paid, Pending and Overdue payments; "Mark Paid" action
*   **Leases:** add, update, delete; shows terms and security deposits
*   Loading, empty and error states on list screens
*   Data stored locally and kept after the app is closed

## Team Members & Roles
*   **Member 1:** UI/UX & Frontend
*   **Member 2:** Database & CRUD
*   **Member 3:** Application Architecture & Kotlin Logic
*   **Member 4:** Navigation, API & Background Processing
*   **Member 5 :** Testing, Validation & GitHub Collaboration

## Architecture Overview
MVVM: **UI (Compose) → ViewModel → Repository → DAO → Room (SQLite)**

*   **UI Layer:** Compose screens in `ui/screens`; display data and collect input
*   **ViewModel Layer:** `RealEstateViewModel` exposes `StateFlow` state, including loading and error messages
*   **Repository Layer:** `RealEstateRepository` is the single access point to the DAOs
*   **Data Source:** Room database `real_estate_database` with four tables: properties, tenants, leases, rent_payments
*   **Navigation:** typed routes; detail screens receive an ID

## Input Validation & Error Handling
The `InputValidator` utility checks input before saving. Invalid input shows an error message and nothing is saved.
*   **Name:** at least 2 characters
*   **Email:** matches `Patterns.EMAIL_ADDRESS`
*   **Phone:** at least 10 characters; digits, `+` or `-` only
*   **Amount:** a number greater than 0
*   **Dates:** both start and end required
*   Database errors are caught in the ViewModel and shown to the user

## Testing
*   **Unit tests:** `RealEstateViewModelTest` covers property, tenant, lease and rent management, the empty state and error handling (run with `./gradlew test`)
*   **Instrumented tests:** `DatabaseCrudInstrumentedTest` covers create, read, update and delete for all four tables using an in-memory Room database (run with `./gradlew connectedAndroidTest`)
*   **Manual tests:** Add Tenant validation messages, data persistence after closing and reopening, navigation and back navigation

## Release
v1.0.0 with installable debug APK: https://github.com/Royan-L/Cat2Mobileapp/releases/tag/v1.0.0

## Known Limitations and Future Improvements
*   Initial sample data is inserted on first launch for demonstration
*   Validation is applied on the Add Tenant form; extend to other forms
*   No user login
*   API integration and WorkManager rent reminders are planned, not yet implemented
*   Proper database migrations (currently the database is rebuilt on schema change)

## How to Run
1. Clone this repository.
2. Open in Android Studio.
3. Sync Gradle and run the `app` configuration on an emulator or physical device.
