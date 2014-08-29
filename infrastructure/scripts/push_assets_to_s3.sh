#!/bin/sh

cd $(dirname $0)
current_path=$(pwd)

S3CMD="../s3cmd/s3cmd"
CONF="--config=../s3cmd/s3cfg"
HEADER=`echo --add-header='Cache-Control:max-age=604800'`
HEADER_GZIP=`echo --add-header='Content-Encoding:gzip'`
EXCLUDE=`echo --exclude '*.gz'`
INCLUDE=`echo --include '*.gz'`
ASSETJAR="../../code/journwe-webapp/target/journwe-webapp-*-assets.jar"
EXTRACTDIR="$current_path/temp"
ASSETDIR="$EXTRACTDIR/public/"
S3BUCKET="s3://journwe-assets/"

rm -r $EXTRACTDIR/
unzip -o -q $ASSETJAR -d $EXTRACTDIR/

echo "check md5 hash and uploade changed local assets to s3 bucket now... sorry, this can take a while ..."
echo "checking and uploading $ASSETDIR"
$S3CMD $CONF sync --recursive $HEADER $EXCLUDE $ASSETDIR $S3BUCKET
$S3CMD $CONF sync --recursive $HEADER $HEADER_GZIP $INCLUDE $ASSETDIR $S3BUCKET

echo "make assets accessible to everyone"
$S3CMD $CONF setacl $S3BUCKET --acl-public --recursive

rm -r $EXTRACTDIR/