#!/bin/bash

# Script de déploiement automatisé
# Ce script déploie l'application complète dans Minikube

set -e

FRONTEND_IMAGE="shortener-app-frontend:latest"
BACKEND_IMAGE="shortener-app-backend:latest"
MINIKUBE_PROFILE="${MINIKUBE_PROFILE:-minikube}"

echo "════════════════════════════════════════════════════"
echo "📦 Déploiement de l'Application Shortener (Minikube)"
echo "════════════════════════════════════════════════════"

# Étape 1: Démarrer Minikube + activer Ingress
echo ""
echo "1️⃣  Démarrage de Minikube + activation de l'ingress..."

if minikube -p "$MINIKUBE_PROFILE" status >/dev/null 2>&1; then
  echo "✓ Minikube '$MINIKUBE_PROFILE' est déjà démarré"
else
  minikube start -p "$MINIKUBE_PROFILE"
  echo "✓ Minikube '$MINIKUBE_PROFILE' démarré"
fi

minikube -p "$MINIKUBE_PROFILE" addons enable ingress
echo "✓ Addon ingress activé"

echo ""
echo "Attente du contrôleur ingress-nginx..."
kubectl wait --namespace ingress-nginx --for=condition=ready pod --selector=app.kubernetes.io/component=controller --timeout=300s || true

# Étape 2: Construire les images Docker
echo ""
echo "2️⃣  Construction des images Docker..."

echo "  • Frontend..."
docker build -t "$FRONTEND_IMAGE" ./frontend
minikube -p "$MINIKUBE_PROFILE" image load "$FRONTEND_IMAGE"
echo "  ✓ Image frontend construite"

echo "  • Backend..."
docker build -t "$BACKEND_IMAGE" ./backend
minikube -p "$MINIKUBE_PROFILE" image load "$BACKEND_IMAGE"
echo "  ✓ Image backend construite"

# Étape 3: Déployer l'application
echo ""
echo "3️⃣  Déploiement de l'application..."
kubectl apply -f k8s/
echo "✓ Manifests appliqués"

# Étape 4: Attendre que les pods soient prêts
echo ""
echo "4️⃣  Attente du démarrage des pods..."
kubectl wait --for=condition=ready pod --selector=app=frontend --timeout=300s || true
kubectl wait --for=condition=ready pod --selector=app=backend --timeout=300s || true
echo "✓ Tous les pods sont prêts"

# Étape 5: Afficher les informations
echo ""
echo "════════════════════════════════════════════════════"
echo "✅ Déploiement terminé!"
echo "════════════════════════════════════════════════════"
echo ""
echo "📊 Deployments:"
kubectl get deployments
echo ""
echo "🔗 Services:"
kubectl get services
echo ""
echo "🌐 Ingress:"
kubectl get ingress
echo ""

echo "════════════════════════════════════════════════════"
echo "🚀 Accès à l'application"
echo "════════════════════════════════════════════════════"
echo ""
echo "Direct (via NGINX Ingress):"
echo "  kubectl port-forward -n ingress-nginx svc/ingress-nginx-controller 8000:80"
echo "  Puis rendez-vous sur http://localhost:8000"
echo ""
echo "════════════════════════════════════════════════════"
echo "🐛 Débogage"
echo "════════════════════════════════════════════════════"
echo ""
echo "Logs du backend:"
echo "  kubectl logs -f deployment/backend"
echo ""
echo "Logs du frontend:"
echo "  kubectl logs -f deployment/frontend"
echo ""
echo "Logs ingress-nginx:"
echo "  kubectl logs -n ingress-nginx -l app.kubernetes.io/component=controller -f"
echo ""
