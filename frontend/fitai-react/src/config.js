// FitAI Backend Configuration
// Change these to match your local environment

export const CONFIG = {
  gatewayUrl:      import.meta.env.VITE_GATEWAY_URL || "http://localhost:9090",
  keycloakUrl:     import.meta.env.VITE_KEYCLOAK_URL || "http://localhost:8080",
  keycloakRealm:   import.meta.env.VITE_KEYCLOAK_REALM || "fitai-db",
  keycloakClientId:import.meta.env.VITE_KEYCLOAK_CLIENT_ID || "fitai-frontend",
};
