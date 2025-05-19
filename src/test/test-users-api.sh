#!/bin/bash

BASE_URL="http://localhost:8080/api/users"
CONTENT_TYPE="Content-Type: application/json"

print_response() {
  echo -e "\nRESPONSE:"
  echo "$1"
  echo "-------------------------------"
}

echo "=== 1. GET all users ==="
RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X GET "$BASE_URL")
print_response "$RESPONSE"

echo "=== 2. CREATE new user (POST) ==="
RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$BASE_URL" \
  -H "$CONTENT_TYPE" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "maidenName": "Smith",
    "gender": "male",
    "email": "john.doe@example.com",
    "phone": "+1234567890",
    "username": "johndoe",
    "password": "password123",
    "birthDate": "1990-01-01"
  }')
print_response "$RESPONSE"

USER_ID=$(echo "$RESPONSE" | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo "Created user with ID: $USER_ID"
echo "-------------------------------"

echo "=== 3. GET created user by ID ==="
RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X GET "$BASE_URL/$USER_ID")
print_response "$RESPONSE"

echo "=== 4. UPDATE user ==="
RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X PUT "$BASE_URL/$USER_ID" \
  -H "$CONTENT_TYPE" \
  -d "{
    \"id\": \"$USER_ID\",
    \"firstName\": \"Updated John\",
    \"lastName\": \"Doe Updated\",
    \"maidenName\": \"Smith Smith\",
    \"gender\": \"male male\",
    \"email\": \"john.updated@example.com\",
    \"phone\": \"+0987654321\",
    \"username\": \"johnny\",
    \"password\": \"newpassword\",
    \"birthDate\": \"1991-11-11\"
  }")
print_response "$RESPONSE"

echo "=== 5. DELETE user ==="
RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X DELETE "$BASE_URL/$USER_ID")
print_response "$RESPONSE"

echo "=== 6. VERIFY deletion (GET deleted user) ==="
RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X GET "$BASE_URL/$USER_ID")
print_response "$RESPONSE"

echo "=== 7. RE-DELETE user to verify 404 ==="
RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X DELETE "$BASE_URL/$USER_ID")
print_response "$RESPONSE"

echo "=== 8. UPDATE deleted user to verify 404 ==="
RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X PUT "$BASE_URL/$USER_ID" \
  -H "$CONTENT_TYPE" \
  -d "{
    \"id\": \"$USER_ID\",
    \"firstName\": \"Updated John\",
    \"lastName\": \"Doe Updated\",
    \"maidenName\": \"Smith Smith\",
    \"gender\": \"male male\",
    \"email\": \"john.updated@example.com\",
    \"phone\": \"+0987654321\",
    \"username\": \"johnny\",
    \"password\": \"newpassword\",
    \"birthDate\": \"1991-11-11\"
  }")
print_response "$RESPONSE"
