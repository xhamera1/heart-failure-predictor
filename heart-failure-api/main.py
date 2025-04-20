from flask import Flask, request, jsonify
import joblib
import numpy as np

app = Flask(__name__)
model_random_forest = joblib.load('random_forest_model.pkl')
model_grad_boost = joblib.load('grad_boost_clf.pkl')


@app.route('/predict', methods=['POST', 'GET'])
def predict():
    try:
        data = request.get_json(force=True)

        features = np.array(list(data.values())).reshape(1, -1)

        if features.shape[1] != model_grad_boost.n_features_in_:
             return jsonify({'error': f'Incorrect number of features. Expected {model_grad_boost.n_features_in_}, got {features.shape[1]}'}), 400


        prediction = model_grad_boost.predict(features)
        prediction_probability = model_grad_boost.predict_proba(features)

        output = {'prediction': int(prediction[0]),
                  'probability_0': float(prediction_probability[0][0]),
                  'probability_1': float(prediction_probability[0][1])}

        return jsonify(output)
    except Exception as e:
        app.logger.error(f"Error during prediction: {e}", exc_info=True)
        return jsonify({'error': 'An error occurred during prediction.'}), 500


if __name__ == '__main__':
    app.run(port=5000, debug=True)