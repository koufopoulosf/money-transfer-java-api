#!/bin/bash

echo "=== Testing Money Transfer Java API ==="
echo ""
echo "Starting the application with Docker Compose..."
docker-compose up --build -d
echo ""
echo "Waiting for services to be ready..."

sleep 30

echo "[1] Create Nick's account (balance: 1000)"
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{"accountNumber": "ACC001", "accountHolderName": "Nick", "balance": 1000.00}'
echo -e "\n"

echo "[2] Create George's account (balance: 500)"
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{"accountNumber": "ACC002", "accountHolderName": "George", "balance": 500.00}'
echo -e "\n"

echo "[3] Transfer 200 from Nick to George"
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{"fromAccountNumber": "ACC001", "toAccountNumber": "ACC002", "amount": 200.00, "description": "Payment"}'
echo -e "\n"

echo "[4] Check Nick's balance (should be 800)"
curl http://localhost:8080/api/accounts/ACC001
echo -e "\n"

echo "[5] Check George's balance (should be 700)"
curl http://localhost:8080/api/accounts/ACC002
echo -e "\n"

echo "[6] Transfer history for Nick"
curl http://localhost:8080/api/transfers/history/ACC001
echo -e "\n"

echo "[7] Test insufficient funds (should fail)"
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{"fromAccountNumber": "ACC001", "toAccountNumber": "ACC002", "amount": 1000.00, "description": "Sending too much"}'
echo -e "\n"

echo "[8] Test same account transfer (should fail)"
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{"fromAccountNumber": "ACC001", "toAccountNumber": "ACC001", "amount": 100.00, "description": "Sending money to myself"}'
echo -e "\n"

echo "=== Tests Done! ==="