# Challenges & Design Decisions

Is project ko banate waqt aaye problems, design dilemmas aur unke solutions.

---

## 1. Maven: "spring-boot-starter-parent not found"
- **Problem:** Project kholte hi pom.xml mein 5 errors aaye, saari dependencies "not found"
- **Reason:** Maven ne abhi tak internet se dependencies download nahi ki thi. Parent POM hi baaki sab dependencies ke versions decide karta hai, isliye uske bina sab fail ho rahi thi
- **Fix:** IntelliJ ke Maven panel se "Reload All Maven Projects" kiya
- **Seekha:** Ek root cause ki wajah se kai errors dikh sakte hain. Hamesha pehla error pehle theek karo

## 2. Redis mein ID counter rakhna risky tha
- **Problem:** Short code generation ke liye ID range counter Redis mein rakhne ka plan tha
- **Reason:** Redis cache hai. Restart pe counter reset hua to purane IDs dobara bante, aur short codes mein collision hota
- **Fix:** ID range allocation MySQL mein shift kiya (durable). Redis sirf cache aur rate-limit counters ke liye, jinka data loss safe hai
- **Seekha:** Har component ka role clear hona chahiye. Jo data kho nahi sakte, wo durable store mein hi jaana chahiye

## 3. Redis restart pe cache khaali hona
- **Problem:** Redis RAM mein data rakhta hai, restart pe saara cache chala jaata
- **Reason:** Khaali cache ke saath saari requests ek saath MySQL pe girti (cache stampede)
- **Fix:** Redis mein AOF persistence on kiya (`--appendonly yes`) aur Docker volume add kiya
- **Seekha:** RDB = periodic snapshot, AOF = har write ka log. AOF mein max ~1 second ka data loss hota hai

## 4. Secrets ko Git se bahar rakhna
- **Problem:** docker-compose.yml mein DB password hardcoded tha
- **Fix:** `.env` file (gitignored) + `.env.example` template. Compose `${VAR}` se values leta hai
- **Seekha:** Production mein secrets, secrets manager (AWS Secrets Manager / Vault) se aate hain, code se kabhi nahi

## 5. docker compose: "no configuration file provided"
- **Problem:** `docker compose up -d` chalane pe ye error aaya
- **Reason:** Terminal home folder (~) mein tha, project folder mein nahi. Compose current folder mein hi docker-compose.yml dhundta hai
- **Fix:** `cd` karke project root mein gaye, phir command chalayi
- **Seekha:** Docker Compose commands hamesha usi folder se chalani hain jahan compose file ho