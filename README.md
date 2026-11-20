# Real Estate Management App (CAT 2)

## Project Overview
[Briefly describe the app here - e.g., A mobile application designed to help landlords manage properties, tenants, and rent payments efficiently.]

## Team Members & Roles
*   **Member 1:** UI/UX & Frontend
*   **Member 2:** Database & CRUD
*   **Member 3:** Application Architecture & Kotlin Logic
*   **Member 4:** Navigation, API & Background Processing
*   **Member 5 (You):** Testing, Validation & GitHub Collaboration

## Architecture Overview
The application follows the MVVM (Model-View-ViewModel) architecture:
*   **UI Layer:** Jetpack Compose (Screens and Components)
*   **ViewModel Layer:** Manages UI state and business logic.
*   **Repository Layer:** Handles data operations.
*   **Data Source:** Room Database for local persistence.

## Input Validation & Error Handling
As part of the QA process, input validation was implemented using the `InputValidator` utility. The application prevents users from submitting invalid data by checking:
*   **Name:** Must be at least 2 characters.
*   **Email:** Must match standard email formats (using `Patterns.EMAIL_ADDRESS`).
*   **Phone:** Must be at least 10 digits.
*   **Date Ranges:** Lease end dates cannot be before start dates.

## Testing Evidence
*   **Validation Testing:** Verified that the "Add Tenant" form correctly displays error messages when fields are left blank or formatted incorrectly.
*   **Data Persistence:** Verified that tenant data remains in the application after closing and reopening it.
*   **Navigation Testing:** Verified smooth transitions between screens and back navigation.

## How to Run
1. Clone this repository.
2. Open in Android Studio.
3. Sync Gradle and run the `app` configuration on an emulator or physical device.