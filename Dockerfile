FROM node:14

WORKDIR /usr/src/app

RUN useradd --user-group --create-home --shell /bin/false appuser

RUN chown -R appuser:appuser /usr/src/app

COPY package*.json ./

RUN npm install --ignore-scripts

USER appuser

COPY src/ ./src/
COPY public/ ./public/

EXPOSE 3000

CMD ["npm", "start"]
