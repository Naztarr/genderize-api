# Profile Intelligence Service

## 📌 Overview

This service accepts a name, enriches the profile using multiple external APIs, stores the processed result in a database, and provides endpoints for retrieval, filtering, and deletion.

---
## 🚀 Base URL
https://genderize-api-production.up.railway.app

## 📍 Endpoints

```
GET /api/classify?name=<name>
```
```
POST /api/profiles
```
```
GET /api/profiles
```
```
GET /api/profiles/{id}
```
```
DELETE /api/profiles/{id}
```


---

## ✅ Success Response

```json
{
  "status": "success",

  "data": {

    "id": "b3f9c1e2-7d4a-4c91-9c2a-1f0a8e5b6d12",

    "name": "ella",

    "gender": "female",

    "gender_probability": 0.99,

    "sample_size": 1234,

    "age": 46,

    "age_group": "adult",

    "country_id": "DRC",

    "country_probability": 0.85,

    "created_at": "2026-04-01T12:00:00Z"
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
  "status": "502", 
  "message": "${externalApi} returned an invalid response"
}
```

---

## ⚙️ Features

* Input validation
* External API integration: genderize, agify and nationalize
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

