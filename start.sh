#!/usr/bin/env bash
set -euo pipefail

# ─── Colors ────────────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
log()  { echo -e "${CYAN}[INMAR]${NC} $*"; }
ok()   { echo -e "${GREEN}[INMAR]${NC} $*"; }
warn() { echo -e "${YELLOW}[INMAR]${NC} $*"; }
err()  { echo -e "${RED}[INMAR]${NC} $*"; }

# ─── Banner ────────────────────────────────────────────────────────────────────
echo -e "${CYAN}"
cat << 'EOF'
  _____ _   _ __  __    _    ____
 |_   _| \ | |  \/  |  / \  |  _ \
   | | |  \| | |\/| | / _ \ | |_) |
   | | | |\  | |  | |/ ___ \|  _ <
   |_| |_| \_|_|  |_/_/   \_\_| \_\
 Metadata API - Spring Boot + Kotlin
EOF
echo -e "${NC}"

# ─── Prereqs ───────────────────────────────────────────────────────────────────
for cmd in docker docker-compose curl; do
  if ! command -v "$cmd" &>/dev/null; then
    # try docker compose (v2)
    if [[ "$cmd" == "docker-compose" ]] && docker compose version &>/dev/null; then
      COMPOSE_CMD="docker compose"
    else
      err "Required command not found: $cmd"
      exit 1
    fi
  fi
done
COMPOSE_CMD="${COMPOSE_CMD:-docker-compose}"
log "Using compose command: $COMPOSE_CMD"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# ─── Args ──────────────────────────────────────────────────────────────────────
BUILD_FLAG=""
case "${1:-}" in
  --build|-b) BUILD_FLAG="--build"; log "Forcing Docker image rebuild" ;;
  --down|-d)
    log "Stopping and removing containers..."
    $COMPOSE_CMD down -v
    ok "All containers stopped and volumes removed."
    exit 0
    ;;
  --logs|-l)
    $COMPOSE_CMD logs -f api
    exit 0
    ;;
  --help|-h)
    echo "Usage: $0 [--build|-b] [--down|-d] [--logs|-l]"
    echo "  --build  Force rebuild of Docker images"
    echo "  --down   Stop and remove containers + volumes"
    echo "  --logs   Tail API logs"
    exit 0
    ;;
esac

# ─── Build & Start ─────────────────────────────────────────────────────────────
log "Starting INMAR Metadata API stack..."
$COMPOSE_CMD up -d $BUILD_FLAG

# ─── Wait for API ──────────────────────────────────────────────────────────────
log "Waiting for API to become healthy..."
MAX_WAIT=120
ELAPSED=0
INTERVAL=5

while [ $ELAPSED -lt $MAX_WAIT ]; do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health 2>/dev/null || echo "000")
  if [ "$STATUS" = "200" ]; then
    ok "API is up! (took ${ELAPSED}s)"
    break
  fi
  printf "  ⏳ Still starting... [%ds elapsed] (HTTP %s)\r" "$ELAPSED" "$STATUS"
  sleep $INTERVAL
  ELAPSED=$((ELAPSED + INTERVAL))
done

if [ $ELAPSED -ge $MAX_WAIT ]; then
  err "API did not start within ${MAX_WAIT}s. Check logs:"
  $COMPOSE_CMD logs --tail=50 api
  exit 1
fi

# ─── Summary ───────────────────────────────────────────────────────────────────
echo ""
ok "════════════════════════════════════════════════════"
ok "  INMAR Metadata API is ready!"
ok "════════════════════════════════════════════════════"
echo ""
echo -e "  ${CYAN}API Base URL:${NC}     http://localhost:8080/api/v1"
echo -e "  ${CYAN}Health Check:${NC}     http://localhost:8080/actuator/health"
echo -e "  ${CYAN}PostgreSQL:${NC}       localhost:5432 (db: inmar)"
echo ""
echo -e "  ${CYAN}Default Credentials:${NC}"
echo -e "    Admin:  admin / password  (full CRUD)"
echo -e "    User:   user  / password  (read-only)"
echo ""
echo -e "  ${CYAN}Quick start - get a JWT token:${NC}"
echo -e "    curl -X POST http://localhost:8080/api/v1/auth/login \\"
echo -e "      -H 'Content-Type: application/json' \\"
echo -e "      -d '{\"username\":\"admin\",\"password\":\"password\"}'"
echo ""
echo -e "  ${CYAN}Then use Basic Auth directly:${NC}"
echo -e "    curl -u admin:password http://localhost:8080/api/v1/location"
echo ""
echo -e "  ${CYAN}SKU search example:${NC}"
echo -e "    curl -u user:password -X POST http://localhost:8080/api/v1/sku/search \\"
echo -e "      -H 'Content-Type: application/json' \\"
echo -e "      -d '{\"location\":\"Perimeter\",\"department\":\"Bakery\",\"category\":\"Bakery Bread\",\"subcategory\":\"Bagels\"}'"
echo ""
echo -e "  ${CYAN}Logs:${NC}             $0 --logs"
echo -e "  ${CYAN}Stop:${NC}             $0 --down"
echo ""
