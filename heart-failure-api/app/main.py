# app/main.py

import os
from flask import Flask, request, jsonify
import joblib
import numpy as np
import logging # Dodaj import logging

MODEL_DIR = os.path.join(os.path.dirname(__file__), '..', 'models')
MODEL_PATH = os.path.join(MODEL_DIR, 'grad_boost_clf.pkl')

app = Flask(__name__)


logging.basicConfig(level=logging.INFO)


try:
    model_grad_boost = joblib.load(MODEL_PATH)
    app.logger.info(f"Model loaded successfully from {MODEL_PATH}")
except FileNotFoundError:
    app.logger.error(f"Error: Model file not found at {MODEL_PATH}")
    model_grad_boost = None
except Exception as e:
    app.logger.error(f"Error loading model: {e}", exc_info=True)
    model_grad_boost = None


@app.route('/predict', methods=['POST'])
def predict():
    if model_grad_boost is None:
        app.logger.error("Prediction attempt failed: Model not loaded.")
        return jsonify({'error': 'Model is not available.'}), 500

    try:
        data = request.get_json(force=True)
        if data is None:
             return jsonify({'error': 'Invalid JSON payload or Content-Type header.'}), 400
    except Exception as e:
        app.logger.error(f"Error parsing JSON: {e}", exc_info=True)
        return jsonify({'error': 'Could not parse JSON payload.'}), 400

    try:
        features = np.array(list(data.values())).reshape(1, -1)

        expected_features = getattr(model_grad_boost, 'n_features_in_', None)
        if expected_features is None:
             app.logger.error("Cannot determine the expected number of features from the model.")
             return jsonify({'error': 'Model information regarding features is missing.'}), 500

        if features.shape[1] != expected_features:
             error_msg = f'Incorrect number of features. Expected {expected_features}, got {features.shape[1]}'
             app.logger.error(error_msg)
             return jsonify({'error': error_msg}), 400

        prediction = model_grad_boost.predict(features)
        prediction_probability = model_grad_boost.predict_proba(features)

        output = {
            'prediction': int(prediction[0]),
            'probability_0': float(prediction_probability[0][0]),
            'probability_1': float(prediction_probability[0][1])
        }

        app.logger.info(f"Prediction successful for input data. Result: {output}")
        return jsonify(output)

    except KeyError as e:
         app.logger.error(f"Missing feature in input data: {e}", exc_info=True)
         return jsonify({'error': f'Missing data for feature: {e}'}), 400
    except ValueError as e:
        app.logger.error(f"Invalid value provided for a feature: {e}", exc_info=True)
        return jsonify({'error': f'Invalid value for feature: {e}'}), 400
    except Exception as e:
        app.logger.error(f"Error during prediction processing: {e}", exc_info=True)
        return jsonify({'error': 'An error occurred during prediction processing.'}), 500


if __name__ == '__main__':
    app.run(port=5000, debug=True)