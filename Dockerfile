FROM babashka/babashka:alpine

ARG FURL
ENV FURL=$FURL

COPY bb.edn /usr/app/
COPY public /usr/app/public
COPY ahoy /usr/app/ahoy

WORKDIR /usr/app
CMD ["bb", "server"]
