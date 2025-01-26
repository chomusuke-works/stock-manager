source .env
docker compose up -d database
sleep 5

docker exec stock-manager_database mkdir -p /dist/
docker cp ./script.sql stock-manager_database:/dist/script.sql
docker cp ./sample_content.sql stock-manager_database:/dist/sample_content.sql
docker exec stock-manager_database psql -f /dist/script.sql
docker exec stock-manager_database psql -f /dist/sample_content.sql

docker compose up -d