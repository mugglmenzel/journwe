#!/bin/sh

S3CMD="../s3cmd/s3cmd"
CONF="--config=../s3cmd/s3cfg"
ASSETDIR1="../../code/journwe-webapp/target/scala-2.10/classes/public/"
ASSETDIR2="../../code/journwe-webapp/modules/acl/target/scala-2.10/classes/public/"
ASSETDIR3="../../code/journwe-webapp/modules/admin/target/scala-2.10/classes/public/"
ASSETDIR4="../../code/journwe-webapp/modules/core/target/scala-2.10/classes/public/"
ASSETDIR5="../../code/journwe-webapp/modules/data/target/scala-2.10/classes/public/"
ASSETDIR6="../../code/journwe-webapp/modules/dataCache/target/scala-2.10/classes/public/"
ASSETDIR7="../../code/journwe-webapp/modules/email/target/scala-2.10/classes/public/"
ASSETDIR8="../../code/journwe-webapp/modules/social/target/scala-2.10/classes/public/"
S3BUCKET="s3://journwe-assets/"

echo "check md5 hash and uploade changed local assets to s3 bucket now... sorry, this can take a while ..."
echo "checking and uploading $ASSETDIR1"
$S3CMD $CONF sync --recursive $ASSETDIR1 $S3BUCKET
echo "checking $ASSETDIR2"
$S3CMD $CONF sync --recursive $ASSETDIR2 $S3BUCKET
echo "checking $ASSETDIR3"
$S3CMD $CONF sync --recursive $ASSETDIR3 $S3BUCKET
echo "checking $ASSETDIR4"
$S3CMD $CONF sync --recursive $ASSETDIR4 $S3BUCKET
echo "checking $ASSETDIR5"
$S3CMD $CONF sync --recursive $ASSETDIR5 $S3BUCKET
echo "checking $ASSETDIR6"
$S3CMD $CONF sync --recursive $ASSETDIR6 $S3BUCKET
echo "checking $ASSETDIR7"
$S3CMD $CONF sync --recursive $ASSETDIR7 $S3BUCKET
echo "checking $ASSETDIR8"
$S3CMD $CONF sync --recursive $ASSETDIR8 $S3BUCKET
echo "make assets accessible to everyone"
$S3CMD $CONF setacl $S3BUCKET --acl-public --recursive