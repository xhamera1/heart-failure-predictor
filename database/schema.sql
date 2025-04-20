-- Drop the database if it exists
DROP DATABASE IF EXISTS heartfailure_db;

-- Create the heartfailure_db database (Note: original comment mentioned 'todorails schema', updated name)
CREATE DATABASE heartfailure_db;

-- Select the heartfailure_db database for use
USE heartfailure_db;

-- Create User table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    roles VARCHAR(255) NOT NULL
);

-- Create Prediction Records table (Note: original comment mentioned 'Task table')
CREATE TABLE prediction_records (
    -- Unique identifier for the prediction record
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Foreign key referencing the user who made the prediction
    -- Assumes you have a 'users' table with a BIGINT 'id' primary key column
    user_id BIGINT NOT NULL,

    -- Input features from the form (corresponding to dataset columns)
    age INT NOT NULL,                       -- Patient's age
    sex ENUM('M', 'F') NOT NULL,            -- Sex (M - Male, F - Female)
    chest_pain_type ENUM('ATA', 'NAP', 'ASY', 'TA') NOT NULL, -- Type of chest pain
    resting_bp SMALLINT UNSIGNED NOT NULL,  -- Resting blood pressure (mm Hg)
    cholesterol SMALLINT UNSIGNED NOT NULL, -- Cholesterol level (mm/dl)
    fasting_bs BOOLEAN NOT NULL,            -- Fasting blood sugar > 120 mg/dl (1 = true, 0 = false)
    resting_ecg ENUM('Normal', 'ST', 'LVH') NOT NULL, -- Resting ECG results
    max_hr SMALLINT UNSIGNED NOT NULL,      -- Maximum heart rate achieved
    exercise_angina ENUM('Y', 'N') NOT NULL,-- Exercise-induced angina (Y - Yes, N - No)
    oldpeak DECIMAL(3, 1) NOT NULL,         -- ST depression induced by exercise
    st_slope ENUM('Up', 'Flat', 'Down') NOT NULL, -- Slope of the peak exercise ST segment

    -- Prediction result returned by the ML model
    predicted_heart_disease BOOLEAN NOT NULL, -- Predicted outcome (1 = heart disease, 0 = none)

    -- Timestamp of the prediction
    prediction_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id)
);