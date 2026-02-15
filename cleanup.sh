#!/bin/bash

# Script de nettoyage
# Supprime tous les déploiements et le cluster Kind

set -e

CLUSTER_NAME="order-app"

echo "🗑️  Suppression de l'application..."

# Supprimer les déploiements
kubectl delete -f k8s/
kubectl delete namespace ingress-nginx

echo "✓ Application supprimée"

echo ""
echo "✅ Nettoyage terminé!"