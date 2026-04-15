# Gender Classification API

## 📌 Overview

This API classifies a given name by gender using the Genderize.io API. It processes the response and applies additional business logic to determine confidence levels.

---
## 🚀 Base URL
https://genderize-api-production.up.railway.app

## 📍 Endpoint

```
GET /api/classify?name=<name>
```

---

## ✅ Success Response

```json
{
  "status": "success",
  "data": {
    "name": "john",
    "gender": "male",
    "probability": 0.99,
    "sample_size": 1234,
    "is_confident": true,
    "processed_at": "2026-04-01T12:00:00Z"
  }
}
```

---

## ❌ Error Responses

### Missing Name (400)

```json
{
  "status": "error",
  "message": "Name is required"
}
```

### Invalid Format (422)

```json
{
  "status": "error",
  "message": "Invalid name format"
}
```

### No Prediction Available (200)

```json
{
  "status": "error",
  "message": "No prediction available for the provided name"
}
```

### External API Failure (502)

```json
{
  "status": "error",
  "message": "External API error"
}
```

---

## ⚙️ Features

* Input validation
* External API integration
* Confidence scoring logic
* ISO 8601 timestamp generation
* Proper HTTP status handling
* CORS enabled

---

## 🛠️ Tech Stack

* Java 21
* Spring Boot
* REST API
* Maven

