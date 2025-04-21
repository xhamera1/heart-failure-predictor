# Heart Failure Predictor Application

A comprehensive system integrating a Java Spring Boot web application with a Python Flask ML API to predict heart failure risk based on patient diagnostic data.

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Machine Learning Model Details](#machine-learning-model-details)
4. [Tech Stack](#tech-stack)
5. [Requirements](#requirements)
6. [Database](#database)
7. [Setup & Installation](#setup--installation)
8. [Running the Application](#running-the-application)
9. [Usage](#usage)
10. [License](#license)

---

## Overview

This project provides a web-based interface for predicting the likelihood of heart failure. It utilizes a machine learning model, trained on relevant patient data, which is served via a dedicated Python/Flask API. The primary interface is a Java/Spring Boot application that allows users to register, log in, submit data for prediction, and review their historical prediction results. The goal is to provide an accessible tool for preliminary heart failure risk assessment.

## Features

* **Web Application (Spring Boot):**
    * Secure user registration and login powered by Spring Security.
    * Password hashing using BCrypt.
    * Intuitive web form for inputting patient diagnostic data with input validation.
    * Real-time interaction with the ML Prediction API to fetch predictions.
    * Clear presentation of prediction results (risk detected/not detected) and associated probabilities.
    * History page for authenticated users to review their past prediction records.
* **Prediction API (Flask):**
    * Serves a pre-trained Gradient Boosting Classifier model.
    * Provides a `/predict` REST endpoint accepting POST requests with JSON data.
    * Returns the predicted class (0 or 1) and the calculated probabilities for class 0 (probability_0) and class 1 (probability_1) as JSON.
    * Handles basic error checking for input feature count.

## Machine Learning Model Details

The prediction capability is driven by a supervised machine learning model:

* **Objective:** To classify patients based on their risk of heart failure using diagnostic measurements.
* **Data Source:** Trained using the publicly available [Heart Failure Prediction Dataset](https://www.kaggle.com/datasets/fedesoriano/heart-failure-prediction) from Kaggle.
* **Preprocessing:** Categorical features present in the dataset (`Sex`, `ChestPainType`, `RestingECG`, `ExerciseAngina`, `ST_Slope`) were converted into numerical format using Scikit-learn's `OrdinalEncoder` prior to training.
* **Model:** A **Gradient Boosting Classifier** (`sklearn.ensemble.GradientBoostingClassifier`) was selected and trained for the prediction task. Key training parameters included `n_estimators=50`; other hyperparameters used Scikit-learn defaults.
* **Performance:** The trained model demonstrated the following accuracy on the dataset split:
    * Training Set Accuracy: **~91.7%** 
    * Test Set Accuracy: **~85.9%** 
* **Persistence:** The final trained model is saved to a file (`grad_boost_clf.pkl`) using Joblib for loading into the Flask API.

## Tech Stack

This project is built using a modern Java/Spring Boot stack combined with a Python/Flask ML service:

* **Web Application Backend & UI:**
    * **Java 21**
    * **Spring Boot 3.4.4**, with essential dependencies:
        * `spring-boot-starter-web`
        * `spring-boot-starter-thymeleaf`
        * `spring-boot-starter-data-jpa` (with Hibernate)
        * `spring-boot-starter-validation`
        * `spring-boot-starter-security`
        * `spring-boot-devtools`
    * **Database:** MySQL (interaction via Spring Data JPA)
    * **Frontend Templating:** Thymeleaf
    * **Build Tool:** Maven
* **ML Prediction API:**
    * **Language:** Python (3.8+ recommended)
    * **Framework:** Flask
    * **ML/Data Libraries:** Scikit-learn, Pandas, NumPy, Joblib

## Requirements

To build and run this project locally, you need:

* Java Development Kit (JDK) 21 or higher
* Apache Maven 3.6+
* Python 3.8 or higher & pip package manager
* MySQL Server (5.7+ recommended)
* Git for cloning the repository
* **Python Dependencies:** Required Python packages for the Flask API are listed in `heart-failure-api/requirements.txt`. They need to be installed using pip (see Setup section).
* **Environment Variable:** `DB_PASSWORD` must be configured in your environment, holding the password for the MySQL user that the Spring Boot application will connect with.
  

## Database

The application uses a MySQL database, expected to be named `heartfailure_db`.

The required table structure is defined in `database/schema.sql`. Please execute this script against your MySQL instance before running the Spring Boot application for the first time to initialize the `users` and `prediction_records` tables.

## Setup & Installation

### Clone Repository:

```bash
git clone https://github.com/xhamera1/heart-failure-predictor.git
cd heart-failure-predictor
```

### Database Setup:

1. Ensure your MySQL server is running.
2. Connect to MySQL and create the database: `CREATE DATABASE heartfailure_db;`
3. Execute the schema script (adjust username -u as needed):

```bash
mysql -u <your_db_username> -p heartfailure_db < database/schema.sql
```

**Important:** Set the `DB_PASSWORD` environment variable to the correct password for your MySQL user. The method depends on your OS and terminal (e.g., `export DB_PASSWORD='your_password'` on Linux/macOS).

### Python Flask API Setup:

1. Navigate to the API directory: `cd heart-failure-api`
2. Create and activate a Python virtual environment (recommended):

```bash
# Linux/macOS
python3 -m venv venv && source venv/bin/activate
# Windows
python -m venv venv && .\venv\Scripts\activate
```

3. Install required Python packages:

```bash
pip install -r requirements.txt
```

### Java Spring Boot Application Setup:

1. Navigate to the Java application directory: `cd ../heart-failure-predictor`
2. Verify database connection details and the Flask API URL (`prediction.api.url`) in `src/main/resources/application.properties`.
3. Build the project using Maven (this will also download dependencies):

```bash
mvn clean package
```
(Alternatively, `mvn clean install`)

## Running the Application

Both the Flask API and the Spring Boot application must be running concurrently.

### Start the Flask API:

1. Ensure you are in the `flask_api` directory and the virtual environment is activated.
2. Set the `FLASK_APP` environment variable and run:

```bash
# Linux/macOS:
export FLASK_APP=app/main.py
flask run
# Windows:
set FLASK_APP=app/main.py
flask run
```

The API should start, typically listening on `http://127.0.0.1:5000`. Check console output.

### Start the Spring Boot Application:

1. Ensure you are in the `heart-failure-predictor` directory.
2. Make sure the `DB_PASSWORD` environment variable is set in your current terminal session or system-wide.
3. Run the application using Maven Plugin:
   
   ```bash
   mvn spring-boot:run
   ```
   
   OR:  Using your IDE: Run the main application class (`HeartFailurePredictorApplication.java`) directly from your IDE.

The web application should start, typically listening on `http://localhost:8080`. Check console output.

## Usage

1. Open your web browser and navigate to `http://localhost:8080`.
2. You should be directed to the login page (`/auth/login`).
3. Use the "Register here" link (`/auth/register`) to create a new account if you don't have one.
4. Log in using your username and password.
5. After successful login, you will land on the prediction page (`/predict`).
6. Fill out the form with the patient's diagnostic details. Ensure all fields are completed according to the descriptions.
7. Click the "Predict" button. The result from the ML model will be displayed below the form.
8. Click the "View Prediction History" link (`/history`) to see a table of all predictions you have made.
9. Use the "Logout" button in the header to securely end your session.

## UI Design & Mockups

Detailed UI designs and prototypes for the web application can be found in Figma:
[View Figma Designs](https://github.com/xhamera1/heart-failure-predictor) TODO


## License

This project is licensed under the MIT License.
For detailed information, please refer to the LICENSE file.
