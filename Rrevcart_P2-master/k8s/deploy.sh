#!/bin/bash
# deploy.sh — applies all Kubernetes manifests in the correct dependency order

set -e

REGISTRY=${1:-"<YOUR_REGISTRY>"}

echo "==> Replacing registry placeholder with: $REGISTRY"
find k8s/services -name "*.yaml" -exec sed -i "s|<YOUR_REGISTRY>|$REGISTRY|g" {} +

echo "==> 1. Namespace"
kubectl apply -f k8s/namespace.yaml

echo "==> 2. Secrets & ConfigMap"
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/configmap.yaml

echo "==> 3. Infrastructure (Consul, MySQL, MongoDB, Redis)"
kubectl apply -f k8s/infra/consul.yaml
kubectl apply -f k8s/infra/mysql.yaml
kubectl apply -f k8s/infra/mongodb.yaml
kubectl apply -f k8s/infra/redis.yaml

echo "==> Waiting for infra to be ready..."
kubectl rollout status deployment/consul   -n revcart --timeout=120s
kubectl rollout status deployment/mysql    -n revcart --timeout=180s
kubectl rollout status deployment/mongodb  -n revcart --timeout=180s
kubectl rollout status deployment/redis    -n revcart --timeout=120s

echo "==> 4. Microservices"
kubectl apply -f k8s/services/

echo "==> 5. Ingress"
kubectl apply -f k8s/ingress.yaml

echo ""
echo "==> Done! Get the external IP with:"
echo "    kubectl get ingress revcart-ingress -n revcart"
