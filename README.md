# Money Transfer Java API

This implementation uses Docker Compose for deployment instead of the standalone JAR approach mentioned in the spec. This choice was made mainly for consistency and simplicity purposes.

Local development was also avoided, I used GitHub Codespaces instead. More info can be found here:
https://docs.github.com/en/codespaces/setting-up-your-project-for-codespaces/adding-a-dev-container-configuration/setting-up-your-java-project-for-codespaces

## Running it

You need Docker installed.

```bash
chmod +x test.sh
./test.sh
```

The script will automatically:
1. Start the application with Docker Compose
2. Wait for services to be ready
3. Run all API tests
4. Display results

To stop:
```bash
docker-compose down
```

## Main decisions

**Transactions** - Used @Transactional so transfers are atomic. Both accounts update together or not at all.

**Concurrency** - Added @Version field on accounts for optimistic locking. Should help with concurrent transfers though I haven't load tested it.

**Simple structure** - One controller, service layer for business logic, Spring Data repositories. Tried to keep it straightforward.

**No DTOs** - Returning entities directly. Probably not ideal but requirements said keep it simple.

**No authentication** - Skipped since spec said internal service.

## Database

Two tables:

**accounts** - stores account info with balance and version field

**transfers** - logs all transfer attempts with status

## API endpoints

Create account:
```
POST /api/accounts
{"accountNumber":"ACC001","accountHolderName":"Nick","balance":1000}
```

Transfer:
```
POST /api/transfers
{"fromAccountNumber":"ACC001","toAccountNumber":"ACC002","amount":200}
```

Get account:
```
GET /api/accounts/ACC001
```

History:
```
GET /api/transfers/history/ACC001
```

## How it works

Transfer validates inputs, loads both accounts, checks balance, updates both, saves them. The @Transactional annotation handles rollback if anything fails. Transfer record gets saved regardless for audit purposes.

## What's missing

Better error handling, proper logging instead of console output, more validation, comprehensive tests. Focused on core functionality and kept scope limited per requirements.

Test script covers main scenarios - successful transfer, insufficient funds, same account validation, balance updates.