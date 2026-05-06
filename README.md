# projet-soa

Full-stack Gestion Soutenance workspace.

## Docker

From this folder:

```powershell
docker compose up --build
```

The compose stack starts:

- Angular frontend: http://localhost:4200
- Spring backend API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- MySQL 8.4 exposed on port `3307`
- Ollama exposed on port `11435`

Default Docker admin bootstrap:

- Admin login: `admin` / `admin12345`

No demo dataset is inserted automatically. Seed the MySQL database manually after the stack is running.

The first run downloads the Ollama model configured by `OLLAMA_MODEL` (`llama3.2:1b` by default), so chatbot readiness can take a little longer than the web app.

Optional overrides:

```powershell
Copy-Item docker.env.example .env
docker compose up --build
```

Stop the stack:

```powershell
docker compose down
```

Reset the database and Ollama model volume:

```powershell
docker compose down -v
```
