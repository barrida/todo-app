#!/bin/bash

set -e

echo "Configuring Couchbase cluster..."

# Initialize the Couchbase cluster
couchbase-cli cluster-init \
  --cluster-username ${COUCHBASE_USER} \
  --cluster-password ${COUCHBASE_PASSWORD} \
  --services data,index,query \
  --cluster-ramsize 1024 \
  --cluster-index-ramsize 256 \
  --cluster-fts-ramsize 256

# Create the bucket
echo "Creating bucket 'todo-bucket'..."
couchbase-cli bucket-create \
  --cluster http://127.0.0.1:8091 \
  --username ${COUCHBASE_USER} \
  --password ${COUCHBASE_PASSWORD} \
  --bucket ${COUCHBASE_BUCKET} \
  --bucket-type couchbase \
  --bucket-ramsize 512

echo "Couchbase setup completed successfully."
