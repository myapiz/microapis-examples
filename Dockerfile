FROM cimg/openjdk:20.0.2

COPY target/pack /srv/myapp

# Using a non-privileged user:
USER nobody
WORKDIR /srv/myapp

EXPOSE 9000

ENTRYPOINT ["sh", "./bin/main"]