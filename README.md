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
2. `kubectl apply -f k8s/ingress.yml`
3. `kubectl apply -f k8s/backend-deployment.yml`
4. `kubectl apply -f k8s/frontend-deployment.yml`
5. `minikube addons enable ingress-dns`
6. `minikube tunnel`