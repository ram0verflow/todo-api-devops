# A plain Ubuntu "server" for Ansible to configure.
# It only has Python (which Ansible needs); Java and the app are installed by the playbook.
FROM ubuntu:22.04
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update \
 && apt-get install -y --no-install-recommends python3 python3-apt sudo ca-certificates \
 && rm -rf /var/lib/apt/lists/*
EXPOSE 8080
CMD ["sleep", "infinity"]
