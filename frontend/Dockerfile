FROM node:20-alpine AS build

WORKDIR /app
COPY package*.json ./

RUN npm ci
COPY . .

ARG VITE_BACKEND_URL
ENV VITE_BACKEND_URL=$VITE_BACKEND_URL

RUN npm run build

FROM node:20-alpine

WORKDIR /app
RUN npm i -g serve
COPY --from=build /app/dist ./dist

EXPOSE 3000
CMD ["serve", "-s", "dist", "-l", "3000"]