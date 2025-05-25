#!/bin/bash

BASE_URL="${BASE_URL:-http://localhost:8080/api/users}"
CONTENT_TYPE="Content-Type: application/json"

fail_if_not() {
  local expected=$1
  local actual=$2
  local message=$3
  if [[ "$expected" != "$actual" ]]; then
    echo " ❌ $message (Expected: $expected, Got: $actual)"
    exit 1
  else
    echo " ✅ $message (Status: $actual)"
  fi
}

echo "=== 1. GET all users ==="
RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL")
BODY=$(echo "$RESPONSE" | sed '$d')
STATUS=$(echo "$RESPONSE" | tail -n1)
fail_if_not "200" "$STATUS" "GET all users"
echo "Users count: $(echo "$BODY" | jq 'length')"
echo "-------------------------------"

echo "=== 2. CREATE new user ==="
USER_JSON=$(cat <<EOF
{
  "firstName": "Verified",
  "lastName": "Tester",
  "maidenName": "Script",
  "gender": "other",
  "email": "verified.tester@example.com",
  "phone": "+10000000000",
  "username": "verifytester",
  "password": "secure123",
  "birthDate": "2000-01-01"
}
EOF
)

RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL" -H "$CONTENT_TYPE" -d "$USER_JSON")
BODY=$(echo "$RESPONSE" | sed '$d')
STATUS=$(echo "$RESPONSE" | tail -n1)
fail_if_not "201" "$STATUS" "Create new user"
USER_ID=$(echo "$BODY" | jq -r '.id')
echo "Created user with ID: $USER_ID"
echo "-------------------------------"

echo "=== 3. GET user by ID: $USER_ID ==="
RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/$USER_ID")
BODY=$(echo "$RESPONSE" | sed '$d')
STATUS=$(echo "$RESPONSE" | tail -n1)
fail_if_not "200" "$STATUS" "GET user by ID $USER_ID"
USERNAME=$(echo "$BODY" | jq -r '.username')
fail_if_not "verifytester" "$USERNAME" "Username check"
echo "-------------------------------"

echo "=== 4. UPDATE user by ID: $USER_ID ==="
UPDATED_JSON=$(cat <<EOF
{
  "id": $USER_ID,
  "firstName": "Updated",
  "lastName": "User",
  "maidenName": "Verifier",
  "gender": "non-binary",
  "email": "updated.tester@example.com",
  "phone": "+19999999999",
  "username": "updatedtester",
  "password": "updatedpass",
  "birthDate": "1999-12-31"
}
EOF
)

RESPONSE=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/$USER_ID" -H "$CONTENT_TYPE" -d "$UPDATED_JSON")
BODY=$(echo "$RESPONSE" | sed '$d')
STATUS=$(echo "$RESPONSE" | tail -n1)
fail_if_not "200" "$STATUS" "Update user ID $USER_ID"
USERNAME=$(echo "$BODY" | jq -r '.username')
fail_if_not "updatedtester" "$USERNAME" "Updated username check"
echo "-------------------------------"

echo "=== 5. DELETE user by ID: $USER_ID ==="
RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/$USER_ID")
STATUS=$(echo "$RESPONSE" | tail -n1)
fail_if_not "204" "$STATUS" "Delete user ID $USER_ID"
echo "-------------------------------"

echo "=== 6. Verify deleted user is gone ==="
RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/$USER_ID")
STATUS=$(echo "$RESPONSE" | tail -n1)
fail_if_not "404" "$STATUS" "GET deleted user returns 404"
echo "-------------------------------"

echo "=== 7. Try updating deleted user ==="
RESPONSE=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/$USER_ID" -H "$CONTENT_TYPE" -d "$UPDATED_JSON")
STATUS=$(echo "$RESPONSE" | tail -n1)
fail_if_not "404" "$STATUS" "Update deleted user returns 404"
echo "-------------------------------"

echo "All tests passed successfully ✅"
