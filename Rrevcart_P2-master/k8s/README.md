# RevCart — Kubernetes Deployment

## Folder Structure

```
k8s/
├── namespace.yaml          # revcart namespace
├── secrets.yaml            # DB/mail/OAuth credentials (base64)
├── configmap.yaml          # shared env vars for all services
├── ingress.yaml            # nginx Ingress — routes /api/* and /*
├── deploy.sh               # one-shot deploy script
├── infra/
│   ├── consul.yaml         # Consul service discovery
│   ├── mysql.yaml          # MySQL 8.0  + PVC
│   ├── mongodb.yaml        # MongoDB 7  + PVC
│   └── redis.yaml          # Redis 7    + PVC
└── services/
    ├── api-gateway.yaml    # port 8080
    ├── auth-service.yaml   # port 8081
    ├── user-service.yaml   # port 8082
    ├── product-service.yaml# port 8083
    ├── cart-service.yaml   # port 8084
    ├── order-service.yaml  # port 8085
    ├── payment-service.yaml# port 8086
    ├── notification-service.yaml # port 8087
    ├── delivery-service.yaml     # port 8088
    ├── analytics-service.yaml    # port 8089
    ├── admin-service.yaml        # port 8090
    └── frontend.yaml             # port 80  (nginx)
```

## Prerequisites

- kubectl configured against your cluster
- An Ingress controller installed (nginx-ingress)
- Docker images pushed to a container registry

## Step 1 — Build & Push Images

```bash
REGISTRY=<your-dockerhub-or-ecr-registry>

# Backend services (from microservices/ root with parent pom)
for svc in api-gateway auth-service user-service product-service cart-service \
            order-service payment-service notification-service delivery-service \
            analytics-service admin-service; do
  docker build -t $REGISTRY/revcart/$svc:latest ./microservices/$svc
  docker push $REGISTRY/revcart/$svc:latest
done

# Frontend
docker build -t $REGISTRY/revcart/frontend:latest ./revcart-frontend
docker push $REGISTRY/revcart/frontend:latest
```

## Step 2 — Update Secrets

Edit `k8s/secrets.yaml` and replace the base64 values with your own:

```bash
echo -n 'YourStrongPassword123!' | base64   # db-password
echo -n 'your-email@gmail.com'   | base64   # mail-username
echo -n 'your-app-password'      | base64   # mail-password
echo -n 'your-google-client-id'  | base64   # google-client-id
echo -n 'your-google-client-secret' | base64 # google-client-secret
```

## Step 3 — Deploy

```bash
# Option A: use the deploy script (replaces <YOUR_REGISTRY> automatically)
bash k8s/deploy.sh <your-registry>

# Option B: manual apply
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/infra/
kubectl apply -f k8s/services/
kubectl apply -f k8s/ingress.yaml
```

## Step 4 — Get the External IP

```bash
kubectl get ingress revcart-ingress -n revcart
```

Open `http://<EXTERNAL-IP>` in your browser.

## Useful Commands

```bash
# Watch all pods
kubectl get pods -n revcart -w

# Logs for a service
kubectl logs -f deployment/auth-service -n revcart

# Restart a deployment
kubectl rollout restart deployment/product-service -n revcart

# Scale a service
kubectl scale deployment/product-service --replicas=2 -n revcart

# Teardown everything
kubectl delete namespace revcart
```

## On AWS EKS

1. Install nginx-ingress for EKS:
   ```bash
   kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.10.1/deploy/static/provider/aws/deploy.yaml
   ```
2. The Ingress will automatically provision an AWS NLB.
3. For HTTPS, add the ACM cert ARN annotation to `ingress.yaml`:
   ```yaml
   annotations:
     service.beta.kubernetes.io/aws-load-balancer-ssl-cert: arn:aws:acm:<region>:<account>:certificate/<id>
   ```
