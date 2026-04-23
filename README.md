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


### 🔧 Overview


The system uses a **rule-based deterministic NLP parser** (no AI/ML).
It converts natural language queries into a structured `ProfileFilterRequest` using:


* Regex pattern matching
* Keyword detection with word boundaries
* Java Locale-based country resolution
* Deterministic rule mapping


The output is then passed into a Specification-based query engine.


---


### 🧩 Supported Query Mappings


#### 👤 Gender Rules


| Input            | Output                    |
|------------------|---------------------------|
| male / males     | gender = male             |
| female / females | gender = female           |
| male and female  | gender = null (no filter) |


---


#### 🎂 Age Rules


| Input                  | Output                     |
|------------------------|----------------------------|
| young                  | min_age = 16, max_age = 24 |
| above X / older than X | min_age = X                |
| below X / under X      | max_age = X                |


---


#### 👥 Age Groups


| Input                | Output               |
|----------------------|----------------------|
| child / children     | age_group = child    |
| teenager / teenagers | age_group = teenager |
| adult / adults       | age_group = adult    |
| senior / seniors     | age_group = senior   |


---


#### 🌍 Country Mapping


* Uses Java `Locale.getISOCountries()`
* Matches country names inside input text
* Converts to ISO-2 code


Example:


```
"people from nigeria" → country_id = NG
"adult males from kenya" → country_id = KE
```


---


#### 📊 Probability Filters


| Input Pattern               | Output                      |
|-----------------------------|-----------------------------|
| gender confidence above X   | min_gender_probability = X  |
| country probability above X | min_country_probability = X |


---


### 🧪 Example Transformations


#### Example 1


```
"young males"
→ gender=male
→ min_age=16
→ max_age=24
```


---


#### Example 2


```
"females above 30"
→ gender=female
→ min_age=30
```


---


#### Example 3


```
"adult males from kenya with gender confidence above 0.7"
→ gender=male
→ age_group=adult
→ country_id=KE
→ min_gender_probability=0.7
```


---


### ❗ Query Failure Handling


If a query cannot be interpreted:


```json
{
 "status": "error",
 "message": "Unable to interpret query"
}
```


This ensures strict validation of unsupported inputs.


---


## ⚠️ Limitations


### 1. Rule-Based System Only


* No AI or semantic understanding
* Strict keyword + regex matching only


---


### 2. Limited Synonym Support


Only predefined terms are supported:


* young
* adult
* teenager
* senior


Unsupported synonyms:


* youths
* elders
* kids (not fully mapped)


---


### 3. No Context Awareness


The parser does not understand:


* negations (e.g. "not young")
* comparative reasoning (e.g. "between young and adult")
* vague expressions


---


### 4. Strict Parsing Behavior


If no rule matches, the system returns:


```
Unable to interpret query
```


---



## 🛠️ Tech Stack


* Java 21
* Spring Boot
* REST API
* Maven