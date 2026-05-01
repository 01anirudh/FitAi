#!/bin/bash
# =============================================================
# FitAI — Keycloak Setup Script
# Run this ONCE after Keycloak starts on EC2
# Usage: bash setup-keycloak.sh <EC2_PUBLIC_IP>
# =============================================================

EC2_IP="${1:-localhost}"
KEYCLOAK_URL="http://$EC2_IP:8080"

echo "⏳ Waiting for Keycloak to be ready at $KEYCLOAK_URL ..."
until curl -sf "$KEYCLOAK_URL/health/ready" > /dev/null 2>&1; do
  echo "   Still waiting..."
  sleep 5
done
echo "✅ Keycloak is up!"

echo ""
echo "🔐 Logging in to Keycloak admin..."
docker exec fitai-keycloak /opt/keycloak/bin/kcadm.sh config credentials \
  --server http://localhost:8080 \
  --realm master \
  --user admin \
  --password admin

echo ""
echo "🏗️  Creating realm: fitai-db"
docker exec fitai-keycloak /opt/keycloak/bin/kcadm.sh create realms \
  -s realm=fitai-db \
  -s enabled=true \
  -s displayName="FitAI" \
  2>&1 | grep -v "already exists" || true

echo ""
echo "📱 Creating client: fitai-frontend"
docker exec fitai-keycloak /opt/keycloak/bin/kcadm.sh create clients \
  -r fitai-db \
  -s clientId=fitai-frontend \
  -s publicClient=true \
  -s directAccessGrantsEnabled=true \
  -s "webOrigins=[\"*\"]" \
  -s "redirectUris=[\"*\"]" \
  2>&1 | grep -v "already exists" || true

echo ""
echo "✅ Keycloak setup complete!"
echo "   → Realm:  fitai-db"
echo "   → Client: fitai-frontend"
echo "   → Admin UI: http://$EC2_IP:8080"
echo ""
echo "💡 Next: Go to http://$EC2_IP:8080/admin and create your first user."
