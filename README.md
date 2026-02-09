# Projet virtualisation

## Présentation

Le projet est un raccourcisseur d'url. Le backend est fait avec SpringBoot et le frontend est fait avec un template
React + Vite.

Les images docker sont les suivantes:

- oraph/backend:2
- oraph/frontend:1

On utilise kubernetes pour gérer le déploiement et la communication entre les deux à l'aide de config CORS dans l'
ingress.

## Commandes

Il faut exécuter les commandes suivantes dans l'ordre:

1. `minikube addons enable ingress`
2. `kubectl apply -f ingress.yml`
3. `kubectl apply -f backend-deployment.yml`
4. `kubectl apply -f backend-service.yml`
5. `kubectl apply -f frontend-config.yml`
6. `kubectl apply -f frontend-deployment.yml`
7. `kubectl apply -f frontend-service.yml`
8. `minikube addons enable ingress-dns`
9. `minikube tunnel`