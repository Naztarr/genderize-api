# 📌 Intelligence Query Engine

## 🧠 Overview

The Intelligence Query Engine is a backend system that enables flexible querying of demographic profile data through:

- Advanced filtering
- Sorting
- Pagination
- Rule-based Natural Language Query parsing

It transforms both structured query parameters and natural language input into a unified `ProfileFilterRequest`, which is then executed using a **Specification-based query system** for efficient database filtering.

---

## ⚙️ Core Features

- Advanced multi-field filtering (combinable conditions)
- Dynamic sorting (age, created_at, gender_probability)
- Pagination with limits and page control
- Natural language query interpretation (rule-based NLP)
- Strict validation and error handling

---

## 🔍 Advanced Filtering

Supported query parameters:

| Field                   | Description                    |
|-------------------------|--------------------------------|
| gender                  | male / female                  |
| age_group               | child, teenager, adult, senior |
| country_id              | ISO country code               |
| min_age                 | minimum age                    |
| max_age                 | maximum age                    |
| min_gender_probability  | gender confidence threshold    |
| min_country_probability | country confidence threshold   |

### Example
```/api/profiles?gender=male&country_id=NG&min_age=25```


### All filters are **combinable** and evaluated using AND logic.

---

## 🔃 Sorting

| Parameter | Values                              |
|-----------|-------------------------------------|
| sort_by   | age, created_at, gender_probability |
| order     | asc, desc                           |

### Example
```/api/profiles?sort_by=age&order=desc```


Default sorting:
- `created_at DESC`

---

## 📄 Pagination

| Parameter | Default | Constraint |
|-----------|---------|------------|
| page      | 1       | ≥ 1        |
| limit     | 10      | max 50     |

### Response Format

```json
{
  "status": "success",
  "page": 1,
  "limit": 10,
  "total": 2026,
  "data": []
}
```
---
## 🧠 Natural Language Parsing Approach
### 🔧 Approach
The system uses a rule-based Natural Language Processing (NLP) parser. It does not use AI or machine learning.

Instead, it relies on:

- Keyword matching
- Regular expressions
- Java Locale country mapping
- Deterministic rule-based extraction

The parsed output is converted into a ProfileFilterRequest, which is then processed by the Specification-based filtering engine.
### Examples:

```
"females above 30" → gender=female, min_age=30
"young males" → age range 16–24
adult males from kenya with gender confidence above 0.7 
Parsed into:
- gender = male
- age_group = adult
- country_id = KE
- min_gender_probability = 0.7
```
## Limitations
1. Rule-Based Only
   - No AI/ML or semantic understanding
   - Strict keyword and regex logic only
2. Limited Language Understanding

    - Cannot handle complex or ambiguous queries:

    Example not supported:
   ```
   users who are not young but not old
   ```
3. Limited Synonyms

   Only predefined keywords are supported:

    - young
    - adult
    - teenager
    - senior

    Synonyms like:

    - youths
    - elders, are not recognized.

## 🛠️ Tech Stack

* Java 21
* Spring Boot
* REST API
* Maven

