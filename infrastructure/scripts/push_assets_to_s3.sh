#!/bin/sh

S3CMD="../s3cmd/s3cmd"
CONF="--config=../s3cmd/.s3cfg"
ASSETDIR="../../code/journwe-webapp/target/scala-2.10/classes/public"
S3BUCKET="s3://journwe-assets/"

echo "uploading local assets to s3 bucket now..."
$S3CMD $CONF sync --delete-removed --recursive $ASSETDIR $S3BUCKET
echo "make assets accessible to everyone"
$S3CMD $CONF setacl $S3BUCKET --acl-public --recursive