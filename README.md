# Projet virtualisation - Shortener

*Par Gian Franco SPADARO et Raphaël ORSI*

---

## Structure

- backend: api
- frontend: interface
- k8s: fichiers de déploiement du projet sous kubernetes
- build-images.sh: script pour rapidement build les images docker
- cleanup.sh: script pour rapidement supprimer l'infrastructure du projet dans minikube
- deploy.sh: script pour rapidement déployer tous le projet dans minikube
- contract.yml: contrat de l'api

## Technologies

- backend: Java 21, Spring Boot 3.2, Spring Data JPA, Gradle 8.4
- frontend: React 19, Vite 7.2
- bdd: H2 en local / PostgreSQL quand déployé
- gateway: NGINX Ingress Controller
- orchestration: Kubernetes (Minikube)
- containerisation: Docker

## Architecture

```[mermaid]
flowchart TB
  %% ====== Top row (front + gateway) ======
  subgraph Client["Client"]
    FE["Frontend (React)\nSPA: /, /shorten, /shortened, ...\nfetch: /api/shorten, /api/shortened"]
  end

  subgraph Ingress["Gateway (NGINX Ingress) - même host"]
    API["api-ingress\npath: /api(/|$)(.*)\nrewrite-target: /$2\n→ svc/backend:8080"]
    WEB["frontend-ingress\npath: /\n→ svc/frontend:80"]
  end

  %% Routing from client
  FE -- "GET /, /shorten, /shortened (SPA routes)" --> WEB
  FE -- "POST /api/shorten\nGET /api/shortened\nGET /api/shortened/{code}" --> API

  %% ====== Backend below gateway ======
  subgraph Backend["Backend (Spring Boot + JPA)"]
    BE["ShortenerController\nPOST /shorten\nGET /shortened\nGET /shortened/{encodedUrl}\n(307 Location)"]
  end

  API -- "rewrite /api/... → /...\nex: /api/shorten → /shorten" --> BE

  %% ====== Persistence ======
  subgraph DB["PostgreSQL (StatefulSet)"]
    PG["Database"]
  end

  BE -- "CRUD via JPA" --> PG
```

## Flux

### Liste des url raccourcies

```[mermaid]
sequenceDiagram
  autonumber
  participant FE as React App (App.tsx)
  participant GW as NGINX Ingress (same host)
  participant BE as Spring Boot (ShortenerController)
  participant DB as PostgreSQL

  FE->>GW: GET /api/shortened
  GW->>BE: GET /shortened  (rewrite)
  BE->>DB: SELECT * FROM shortened_url
  DB-->>BE: List<ShortenedUrl>
  BE-->>GW: 200 OK + JSON List
  GW-->>FE: 200 OK + JSON List
```

### Raccourcir une url

```[mermaid]
sequenceDiagram
  autonumber
  participant FE as React App (App.txt)
  participant GW as NGINX Ingress (same host)
  participant BE as Spring Boot (ShortenerController)
  participant SVC as ShortenerService
  participant DB as PostgreSQL

  Note over FE: User submit form
  FE->>GW: POST /api/shorten Content-Type: application/json { "url": "<trimmed>" }
  GW->>BE: POST /shorten (rewrite)

  BE->>BE: URLDecoder.decode(url, UTF-8)
  BE->>SVC: encodedUrl(decodedUrl)
  SVC-->>BE: encodedUrl
  BE->>SVC: saveOrRetrieve(encodedUrl, decodedUrl)

  alt Déjà existante
    SVC->>DB: Spring Data JPA
    DB-->>SVC: existing ShortenedUrl
  else Nouvelle entrée
    SVC->>DB: Spring Data JPA
    DB-->>SVC: created ShortenedUrl{id, url, encodedUrl}
  end

  SVC-->>BE: ShortenedUrl
  BE-->>GW: 201 Created + ShortenedUrl JSON
  GW-->>FE: 201 Created + ShortenedUrl JSON

  Note over FE: Add if not dupplicated
```

### Accéder à l'url d'origine

```[mermaid]
sequenceDiagram
  autonumber
  participant U as User (Browser tab)
  participant FE as React App (App.tsx)
  participant GW as NGINX Ingress (same host)
  participant BE as Spring Boot (ShortenerController)
  participant SVC as ShortenerService
  participant DB as PostgreSQL
  participant EXT as Target website

  U->>FE: Click short link (opens new tab)
  U->>GW: GET /api/shortened/{encodedUrl}
  GW->>BE: GET /shortened/{encodedUrl} (rewrite)

  BE->>SVC: getShortenedUrl(encodedUrl)
  SVC->>DB: Spring Data JPA
  DB-->>SVC: decoded_url (target)
  SVC-->>BE: URI(target)

  BE-->>GW: 307 Temporary Redirect\nLocation: <decoded_url>
  GW-->>U: 307 Temporary Redirect\nLocation: <decoded_url>
  U->>EXT: Follow redirect (GET <decoded_url>)
  EXT-->>U: 200 OK (page)
```

## Démarrage de l'application

### Pré-requis

- Minikube installé
- Docker installé et actif

### Déploiement

```[shell]
chmod +x dpeloy.sh

# Build les images et les déploie sur minikube
./deploy.sh

# Dans un nouveau terminal, ne pas quitter le processus
kubectl port-forward -n ingress-nginx svc/ingress-nginx-controller 8000:80

# Accéder à l'application
open http://localhost:8000
```

### Nettoyage

```[shell]
chmod +x cleanup.sh

# Suppression de l'infrastructure dans minikube
./cleanup.sh
```