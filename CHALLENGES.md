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

## 6. App start pe "Communications link failure"
- **Problem:** App start hote hi Flyway ka lamba error, "Communications link failure"
- **Reason:** Laptop restart ke baad Docker containers band the, MySQL chal hi nahi raha tha. Asli wajah stack trace ke sabse neeche thi: "Connection refused"
- **Fix:** `docker compose up -d` se containers start kiye, phir app chalaya
- **Seekha:** Stack trace mein sabse neeche wala "Caused by" asli root cause hota hai. App start karne se pehle uski dependencies (DB, Redis) chalni chahiye

## 7. One-time links aur forwarding ki problem
- **Problem:** Link sirf ek baar ya sirf intended insaan hi khol paaye, forward hone pe nahi
- **Reason:** Short URL bearer token hai, jiske paas link hai wahi access kar sakta hai
- **Fix:**
    - `remaining_uses` column (V4 migration) + atomic `UPDATE ... WHERE remaining_uses > 0`, taaki race condition na ho
    - Limited-use links ko Redis mein cache nahi karna, warna counter bypass ho jaata
    - WhatsApp/Slack preview bots ek use kha jaate hain, isliye interstitial "Continue" page (planned)
    - Specific recipient ke liye OTP/password protection (planned)
- **Seekha:** One-time link forwarding ko poori tarah nahi rokta. Identity verification hi asli solution hai

## 8. Sequential short codes guess ho sakte the
- **Problem:** Counter + Base62 se codes 1000000, 1000001, 1000002 jaise ban rahe the
- **Reason:** Sequence predictable thi. Koi bhi agle-pichhle codes try karke doosron ke links khol sakta tha (enumeration attack)
- **Fix:** Base62 se pehle `CodeShuffler` lagaya: multiply + digit reverse + offset, jo bijection hai. Codes random dikhte hain par unique rehte hain. Secret constants `.env` mein
- **Rejected alternatives:** id + random number (collision aa jaata), poora random code (har insert pe DB check aur retry)
- **Seekha:** Uniqueness aur unpredictability alag cheezein hain. Short URL ko secret nahi maanna chahiye, isliye rate limiting bhi lagegi

## 9. YAML mein galat indentation
- **Problem:** `app:` section galti se `server:` ke andar chala gaya (2 extra spaces)
- **Reason:** YAML mein spaces se hi nesting decide hoti hai. Ye `server.app.base-url` ban gaya, `app.base-url` nahi
- **Fix:** `app:` ko left se shuru kiya, `spring:` aur `server:` ke level pe
- **Seekha:** Aisi galti app start pe nahi pakdi jaati, kyunki format valid hai. Sirf setting galat jagah hoti hai, aur error tab aata hai jab code use dhundhta hai

## 10. App start pe "failed to convert String to long"
- **Problem:** `CodeGenConfig` add karte hi app start nahi hua. Value aa rahi thi `"${SHORTCODE_MULTIPLIER_A}"`
- **Reason:** Ye variable `.env` mein tha hi nahi, isliye Spring ne placeholder ko jaise ka taisa text maan liya aur long mein convert nahi kar paya
- **Fix:** `.env` mein `SHORTCODE_*` ki 4 values add ki
- **Seekha:** Jab tak koi bean config use nahi karta, galat config pakdi nahi jaati. `@ConfigurationProperties` ne startup pe hi fail karke galti dikha di, jo production mein late pata chalne se behtar hai (fail fast)

## 11. Transaction propagation se duplicate IDs ka khatra
- **Problem:** `UrlService.shorten()` aur `IdBlockService.reserveBlock()` dono `@Transactional` the. Default mein andar wala transaction bahar wale mein mil jaata hai
- **Reason:** URL save fail hone pe poora rollback hota, `next_value` wapas purana ho jaata, par app ki memory mein packet ke IDs bache rehte. Doosra instance wahi packet le leta → duplicate IDs
- **Fix:** `reserveBlock()` pe `@Transactional(propagation = Propagation.REQUIRES_NEW)`. Packet hamesha alag transaction mein turant commit hota hai
- **Seekha:** Transaction propagation samajhna zaroori hai. Bonus: lock bhi jaldi khulta hai, URL save hone tak nahi rukta