# Classifieds App

A modern classifieds application with React (frontend) and Spring Boot (backend).

## Features
- User auth (signup/login) with session
- Categories, post/edit/delete ads, favorites
- Real-time chat (WebSockets/STOMP)
- Dark mode, responsive UI, PWA installable
- Payments (mock by default, Stripe when STRIPE_SECRET is set)
- Analytics (live counts), AI-style recommendations (based on behavior)

## Tech Stack
- Frontend: React, React Router, PWA (service worker, manifest)
- Backend: Spring Boot, Spring Security, WebSockets, H2 (file-based)

## Quick Start (Local Dev)
1. Backend
   - Requirements: Java 17+, Maven (mvnd or mvn)
   - From `backend/`:
     - Windows (PowerShell): `mvnd -DskipTests spring-boot:run`
     - Linux/macOS: `mvn -DskipTests spring-boot:run`

2. Frontend
   - Requirements: Node 18+
   - From `frontend/`:
     - `npm install`
     - `npm start`

App runs at http://localhost:3000 (frontend) and http://localhost:8080 (backend).

## Production Build
1. Backend
   - From `backend/`: `mvn -DskipTests clean package`
   - Output: `backend/target/classifieds-backend-0.0.1-SNAPSHOT.jar`

2. Frontend
   - From `frontend/`: `npm ci && npm run build`
   - Output: `frontend/build/`

## Environment Variables (Backend)
- `SPRING_PROFILES_ACTIVE`: `prod`
- `SERVER_PORT`: e.g. `8080`
- `CORS_ALLOWED_ORIGINS`: comma-separated origins (e.g. `https://your-frontend`)
- `STRIPE_SECRET`: Stripe secret key to enable real payments (optional)

## Deploy Option A (Beginner Friendly)
- Frontend on Netlify or Vercel
  - Build command: `npm run build`
  - Publish directory: `frontend/build`
- Backend on Render or Railway
  - Root: `backend`
  - Build: `mvn -DskipTests clean package`
  - Start: `java -Xms256m -Xmx512m -jar target/classifieds-backend-0.0.1-SNAPSHOT.jar`
  - Env: set variables above (include `CORS_ALLOWED_ORIGINS` to your frontend URL)
- Health check: `GET /api/actuator/health` → `{ "status": "UP" }`

## Deploy Option B (Single VPS with Nginx)
1. Server setup (Ubuntu 22.04)
   - `sudo apt update && sudo apt install -y openjdk-17-jre nginx`
   - `sudo snap install --classic certbot`
2. Copy artifacts
   - JAR to `/opt/classifieds/`
   - Frontend build to `/var/www/classifieds/build`
3. Systemd service `/etc/systemd/system/classifieds.service`
```
[Unit]
Description=Classifieds Backend
After=network.target

[Service]
Environment=SPRING_PROFILES_ACTIVE=prod
Environment=SERVER_PORT=8080
Environment=CORS_ALLOWED_ORIGINS=https://your-domain.com,https://www.your-domain.com
# Environment=STRIPE_SECRET=sk_test_xxx
WorkingDirectory=/opt/classifieds
ExecStart=/usr/bin/java -Xms256m -Xmx512m -jar /opt/classifieds/classifieds-backend-0.0.1-SNAPSHOT.jar
Restart=always

[Install]
WantedBy=multi-user.target
```
   - Enable: `sudo systemctl daemon-reload && sudo systemctl enable --now classifieds`
4. Nginx site `/etc/nginx/sites-available/classifieds`
```
server { listen 80; server_name your-domain.com www.your-domain.com; 
  root /var/www/classifieds/build; index index.html;
  location / { try_files $uri /index.html; }
  location /api/ {
    proxy_pass http://127.0.0.1:8080/api/;
    proxy_set_header Host $host;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
  }
  location /ws/ {
    proxy_pass http://127.0.0.1:8080/ws/;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
    proxy_read_timeout 3600s;
  }
  location = /sw.js { add_header Cache-Control "no-cache"; }
  location = /manifest.json { add_header Cache-Control "no-cache"; }
}
```
   - Enable: `sudo ln -s /etc/nginx/sites-available/classifieds /etc/nginx/sites-enabled/`
   - Test+reload: `sudo nginx -t && sudo systemctl reload nginx`
5. HTTPS
   - `sudo certbot --nginx -d your-domain.com -d www.your-domain.com`

## PWA & Icons
- Icons/manifest in `frontend/public/`
- Logo: `logo.svg`, favicon: `favicon.svg`

## Payments (Stripe)
- Set `STRIPE_SECRET` on backend to enable real payment intents
- Frontend uses same endpoints; create-intent returns `clientSecret` in Stripe mode

## Troubleshooting
- Backend logs (systemd): `journalctl -u classifieds -f`
- Nginx logs: `/var/log/nginx/{access,error}.log`
- CORS errors: ensure `CORS_ALLOWED_ORIGINS` matches your frontend URL exactly

## License
MIT (adjust as needed)
