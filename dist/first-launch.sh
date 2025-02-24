source .env
docker compose up -d database
until [ "`docker inspect -f {{.State.Running}} stock-manager-database-1`"=="true" ]; do
    sleep 0.1;
done;

docker exec stock-manager-database-1 mkdir -p /dist/
docker cp ./script.sql stock-manager-database-1:/dist/script.sql
docker cp ./sample_content.sql stock-manager-database-1:/dist/sample_content.sql
docker exec stock-manager-database-1 psql $POSTGRES_DB -U $POSTGRES_USER -f /dist/script.sql
docker exec stock-manager-database-1 psql $POSTGRES_DB -U $POSTGRES_USER -f /dist/sample_content.sql

docker compose up -d
