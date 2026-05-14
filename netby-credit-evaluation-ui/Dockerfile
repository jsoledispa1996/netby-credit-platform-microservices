FROM node:20-alpine AS build
WORKDIR /app
COPY package.json .
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist/netby-credit-evaluation-ui/browser /usr/share/nginx/html
# Se copia como template; envsubst lo procesa al arrancar
COPY nginx.conf /etc/nginx/templates/default.conf.template
EXPOSE 80
# nginx:alpine ejecuta automáticamente envsubst sobre /etc/nginx/templates/*.template
# Variables requeridas: BACKEND_URL, NGINX_RESOLVER
# Valor por defecto para DEV (Docker usa DNS 127.0.0.11)
ENV BACKEND_URL=http://credit-orchestrator:8080 \
    NGINX_RESOLVER=127.0.0.11
