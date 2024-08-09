# Use the official Couchbase image as the base
FROM couchbase:latest

# Set environment variables for Couchbase
ENV COUCHBASE_USER=admin
ENV COUCHBASE_PASSWORD=password
ENV COUCHBASE_BUCKET=todo-bucket

# Copy the configuration script into the container
COPY configure-server.sh /opt/couchbase/

# Make the script executable
RUN chmod +x /opt/couchbase/configure-server.sh

# Expose necessary ports
EXPOSE 8091 8092 8093 8094 8095 8096 11210 11211

# Entry point to start Couchbase server and run the configuration script
ENTRYPOINT ["/bin/bash", "-c", "couchbase-server & sleep 30 && /opt/couchbase/configure-server.sh && tail -f /dev/null"]