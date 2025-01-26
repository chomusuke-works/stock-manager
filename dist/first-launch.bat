@echo off
for /f "tokens=1,* delims==" %%a in (.env) do set %%a=%%b

docker compose up -d database

timeout /t 5 /nobreak

docker exec stock-manager_database mkdir /dist

docker cp script.sql stock-manager_database:/dist/script.sql
docker cp sample_content.sql stock-manager_database:/dist/sample_content.sql

docker exec stock-manager_database psql -f /dist/script.sql
docker exec stock-manager_database psql -f /dist/sample_content.sql

docker-compose up -d